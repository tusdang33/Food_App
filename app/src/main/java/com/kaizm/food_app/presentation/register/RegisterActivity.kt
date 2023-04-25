package com.kaizm.food_app.presentation.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kaizm.food_app.MainActivity
import com.kaizm.food_app.databinding.ActivityRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenCreated {
            viewModel.event.collect { event ->
                when(event) {
                    is RegisterViewModel.Event.RegSuccess -> navigateToMain()
                    is RegisterViewModel.Event.RegFail -> Toast.makeText(
                        this@RegisterActivity, "Fail", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.btnReg.setOnClickListener {
            viewModel.register(
                binding.edtEmail.text.toString(),
                binding.edtPass.text.toString(),
                binding.edtPassConfirm.text.toString()
            )
        }
    }

    private fun navigateToMain() {
//        startActivity(Intent(this, MainActivity::class.java))
//        finishAffinity()
    }
}