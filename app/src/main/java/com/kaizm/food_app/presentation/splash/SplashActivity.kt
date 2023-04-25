package com.kaizm.food_app.presentation.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kaizm.food_app.MainActivity
import com.kaizm.food_app.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.checkCurrentUser()
        lifecycleScope.launchWhenCreated {
            viewModel.event.collect { event ->
                when(event) {
                    is SplashViewModel.Event.LoginSuccess -> navigateTo(MainActivity::class.java)
                    is SplashViewModel.Event.LoginFail -> navigateTo(MainActivity::class.java)
                }
            }
        }
    }

    private fun <T> navigateTo(cls: Class<T>) {
        lifecycleScope.launch {
            delay(1500L)
            startActivity(Intent(this@SplashActivity, cls))
            finishAffinity()
        }
    }
}