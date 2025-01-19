package ru.gb.dz18.presentation

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import coil.load
import com.google.android.material.internal.ContextUtils.getActivity
import ru.gb.dz18.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        binding.date.text=intent.getStringExtra("date")
        binding.pic.load(intent.getStringExtra("img"))
        binding.button.setOnClickListener {
            val intent = Intent(this, FirstActivity::class.java)
            getActivity(this)?.finish()
            startActivity(intent)
        }
    }
}