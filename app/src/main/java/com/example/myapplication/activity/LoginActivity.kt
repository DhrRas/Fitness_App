package com.example.myapplication.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.global.DB
import com.example.myapplication.manager.SessionManager

class LoginActivity : AppCompatActivity() {
    var db: DB?=null
    var session:SessionManager?=null
    var edtUserName: EditText?=null
    var edtPassword: EditText?=null

    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DB(this)
        session = SessionManager(this)
        edtUserName = binding.edtUserName
        edtPassword = binding.edtPassword

        binding.btnLogin.setOnClickListener {

        }

        binding.txtForgotPassword.setOnClickListener {

        }
    }
}


