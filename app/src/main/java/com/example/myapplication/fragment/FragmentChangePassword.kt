package com.example.myapplication.fragment

import android.database.DatabaseUtils
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentChangePasswordBinding
import com.example.myapplication.global.DB
import com.example.myapplication.global.MyFunction


class FragmentChangePassword : Fragment() {
    private lateinit var binding: FragmentChangePasswordBinding
    private var db: DB? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Change Password"
        db = activity?.let { DB(it) }

        fillOldMobile()

        binding.btnChangedPassword.setOnClickListener {
            try {
                if (binding.edtNewPassword.text.toString().trim().isNotEmpty()) {
                    if (binding.edtNewPassword.text.toString()
                            .trim() == binding.edtConfirmPassword.text.toString().trim()
                    ) {
                        val sqlQuery = "UPDATE ADMIN SET PASSWORD=" + DatabaseUtils.sqlEscapeString(
                            binding.edtNewPassword.text.toString().trim()
                        ) + ""
                        db?.executeQuery(sqlQuery)
                        showToast("Password Changed Successfully")
                    } else {
                        showToast("Password not matched")
                    }
                } else {
                    showToast("Enter new Password")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.btnChangedMobile.setOnClickListener {
            try {
                if (binding.edtNewPassword.text.toString().trim().isNotEmpty()) {
                    val sqlQuery =
                        "UPDATE ADMIN SET MOBILE = '" + binding.edtNewNumber.text.toString()
                            .trim() + "'"
                    db?.executeQuery(sqlQuery)
                    showToast("Mobile number changed successfully")
                } else {
                    showToast("Enter New Mobile number")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fillOldMobile() {
        try {
            val sqlQuery = "SELECT MOBILE FROM ADMIN"
            db?.fireQuery(sqlQuery)?.use {
                val mobile = MyFunction.getValue(it, "MOBILE")
                if (mobile.trim().isNotEmpty()) {
                    binding.edtOldNumber.setText(mobile)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showToast(value: String) {
        Toast.makeText(activity, value, Toast.LENGTH_LONG).show()
    }


}