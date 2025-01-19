package ru.gb.dz18.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ru.gb.dz18.data.App
import ru.gb.dz18.data.Attractions
import ru.gb.dz18.data.MyAdapter
import ru.gb.dz18.databinding.ActivityFirstBinding


class FirstActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirstBinding
    private val photoAdapter = MyAdapter { Attractions ->
        onItemClick(Attractions) }

    val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val attractionsDao = (application as App).db.attractionsDao()
                return MainViewModel(attractionsDao) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.adapter = photoAdapter

        binding.addBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.delBtn.setOnClickListener {
            viewModel.onDelBtn()
        }

        lifecycleScope
            .launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.allPhotos
                        .collect {
                            photoAdapter.setData(viewModel.allPhotos.value)
                        }
                }
            }
    }

    fun onItemClick(item: Attractions) {
        val intent = Intent(this, SecondActivity::class.java)
        intent.putExtra("date", item.date)
        intent.putExtra("img", item.uri)
        startActivity(intent)
    }
}