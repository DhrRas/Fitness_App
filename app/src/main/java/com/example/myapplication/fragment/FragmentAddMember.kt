package com.example.myapplication.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.EditText
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentAddMemberBinding
import com.example.myapplication.global.DB
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.widget.AdapterView as AdapterView1


class FragmentAddMember : Fragment() {
    var db: DB? = null

    private lateinit var binding: FragmentAddMemberBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = activity?.let { DB(it) }
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view1, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd/MM/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.edtJoining.setText(sdf.format(cal.time))

                binding.spMemberShip.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            p2: Int,
                            p3: Long
                        ) {
                            val value = binding.spMemberShip.selectedItem.toString().trim()
                            if (value == "Select") {
                                binding.edtExpire.setText("")
                            } else {
                                if (binding.edtJoining.text.toString().trim().isNotEmpty()) {
                                    if (value == "1 Month") {
                                        calculateExpireDate(1, binding.edtExpire)
                                    } else if (value == "3 Months") {
                                        calculateExpireDate(3, binding.edtExpire)
                                    } else if (value == "6 Months") {
                                        calculateExpireDate(6, binding.edtExpire)
                                    } else if (value == "1 Year") {
                                        calculateExpireDate(12, binding.edtExpire)
                                    } else if (value == "3 Years") {
                                        calculateExpireDate(36, binding.edtExpire)
                                    }

                                } else {
                                    showToast("Select Joining date first")
                                }

                            }
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
                        }
                    }
            }

        binding.imgPicDate.setOnClickListener {
            activity?.let { it1 ->
                DatePickerDialog(
                    it1, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun calculateExpireDate(month: Int, edtExpiry: EditText) {
        val dtStart = binding.edtJoining.text.toString().trim()
        if (dtStart.isNotEmpty()) {
            val format = SimpleDateFormat("dd/MM/yyyy")
            val date1 = format.parse(dtStart)
            val cal = Calendar.getInstance()
            cal.time = date1
            cal.add(Calendar.MONTH, month)
            val myFormat = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            edtExpiry.setText(sdf.format(cal.time))
        }
    }

    fun showToast(value: String) {
        Toast.makeText(activity, value, Toast.LENGTH_LONG).show()
    }
}
