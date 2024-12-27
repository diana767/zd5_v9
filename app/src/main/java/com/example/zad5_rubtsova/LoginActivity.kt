package com.example.zad5_rubtsova

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zad5_rubtsova.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            val selectedRole = binding.spinner.selectedItem.toString()
            if (authenticateUser(email, password, selectedRole)) {
                // Save credentials if it's the first login
                saveUserCredentials(email, password, selectedRole)

                // Redirect based on the selected role
                when (selectedRole) {
                    "Министерство" -> startActivity(Intent(this, MainActivity::class.java))
                    "Преподаватель" -> startActivity(Intent(this, StudentActivity::class.java))
                    "Студент" -> startActivity(Intent(this, UniversitySearchActivity::class.java))
                }
                finish()
            } else {
                Toast.makeText(this, "Неверные учетные данные", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun authenticateUser(email: String, password: String, role: String): Boolean {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val storedEmail = sharedPreferences.getString("email_$role", null)
        val storedPassword = sharedPreferences.getString("password_$role", null)

        // Check if the email matches and if the password is correct
        return email == storedEmail && password == storedPassword
    }

    private fun saveUserCredentials(email: String, password: String, role: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email_$role", email)
        editor.putString("password_$role", password)
        editor.apply()
    }
}