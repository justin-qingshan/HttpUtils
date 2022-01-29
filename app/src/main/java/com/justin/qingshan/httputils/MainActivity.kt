package com.justin.qingshan.httputils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import com.justin.qingshan.httputils.viewmodel.SampleViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: SampleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[SampleViewModel::class.java]

        initListener()

        initObserver()
    }

    private fun initObserver() {
        viewModel.error.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.getResult.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initListener() {
        findViewById<AppCompatButton>(R.id.btn_get_sample).setOnClickListener {
            viewModel.getSample()
        }

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val fd = it.data?.data?.let { uri -> contentResolver.openFileDescriptor(uri, "r") }
                if (fd != null) {
                    viewModel.postFileSample(fd)
                } else {
                    Toast.makeText(this, "file select failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<AppCompatButton>(R.id.btn_upload_file_sample).setOnClickListener {

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)

            launcher.launch(intent)
        }

        findViewById<AppCompatButton>(R.id.btn_post_string_sample).setOnClickListener {
            viewModel.postSample()
        }

        findViewById<AppCompatButton>(R.id.btn_head_sample).setOnClickListener {
            viewModel.headSample()
        }
    }
}