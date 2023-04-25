package com.kaizm.food_app.presentation.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kaizm.food_app.MainActivity
import com.kaizm.food_app.databinding.ActivityLoginBinding
import com.kaizm.food_app.presentation.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenCreated {
            viewModel.event.collect { event ->
                when(event) {
                    is LoginViewModel.Event.LoginSuccess -> navigateToMain()
                    is LoginViewModel.Event.LoginFail -> Toast.makeText(
                        this@LoginActivity, "Fail", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.tvCreate.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            viewModel.login(binding.edtEmail.text.toString(), binding.edtPass.text.toString())
        }

    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }
}