package ru.gb.dz18

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.gb.dz18.data.App
import ru.gb.dz18.databinding.FragmentMainBinding
import ru.gb.dz18.presentation.MainViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor

private const val FILENAME_FORMAT = "yyy-MM-dd-HH-mm-ss"

class MainFragment : Fragment() {

    val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val attractionsDao = (activity?.application as App).db.attractionsDao()
                return MainViewModel(attractionsDao) as T
            }
        }
    }

    companion object {
        fun newInstance() = MainFragment()
        private val REQUEST_PERMISSIONS: Array<String> = buildList {
            add(Manifest.permission.CAMERA)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }

    private var imageCapture: ImageCapture? = null
    private lateinit var executor: Executor
    private lateinit var binding: FragmentMainBinding

    private val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
        .format(System.currentTimeMillis())

    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (map.values.all { it }) {
                startCamera()
                Toast.makeText(context, "permission is Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "permission is not Granted", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        executor = ContextCompat.getMainExecutor(requireContext())
        checkPermissions()
        binding.takePhotoButton.setOnClickListener {
            takePhoto()
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        val outputOptions = context?.let {
            ImageCapture.OutputFileOptions
                .Builder(
                    it.contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                .build()
        }

        if (outputOptions != null) {
            imageCapture.takePicture(
                outputOptions,
                executor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Toast.makeText(
                            context,
                            "Photo saved on: ${outputFileResults.savedUri}",
                            Toast.LENGTH_SHORT
                        ).show()

                        val uri = (outputFileResults.savedUri).toString()
                        viewModel.onAddBtn(name, uri)
                        activity?.finish()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(
                            context,
                            "Photo failed: ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        exception.printStackTrace()
                    }
                })
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            preview.surfaceProvider = binding.viewFinder.surfaceProvider
            imageCapture = ImageCapture.Builder().build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        }, executor)
    }

    private fun checkPermissions() {
        val isAllGranted = REQUEST_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (isAllGranted) {
            startCamera()
        } else {
            launcher.launch(REQUEST_PERMISSIONS)
        }
    }
}