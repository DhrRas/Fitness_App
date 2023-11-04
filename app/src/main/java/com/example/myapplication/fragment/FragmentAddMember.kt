package com.example.myapplication.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.database.DatabaseUtils
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.R.style
import com.example.myapplication.databinding.FragmentAddMemberBinding
import com.example.myapplication.databinding.RenewDialogBinding
import com.example.myapplication.global.CaptureImage
import com.example.myapplication.global.DB
import com.example.myapplication.global.MyFunction
import com.github.florent37.runtimepermission.RuntimePermission
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class FragmentAddMember : Fragment() {
    var db: DB? = null
    var oneMonth: String? = ""
    var threeMonths: String? = ""
    var sixMonths: String? = ""
    var oneYear: String? = ""
    var threeYear: String? = ""

    private lateinit var binding: FragmentAddMemberBinding
    private lateinit var bindingDialog: RenewDialogBinding
    private var captureImage: CaptureImage? = null
    private val REQUEST_CAMERA = 1234
    private val REQUEST_GALLERY = 5664
    private var actualImagePath = ""
    private var gender = "Male"
    private var ID = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Add New Member"
        db = activity?.let { DB(it) }
        captureImage = CaptureImage(activity)

        ID = arguments!!.getString("ID").toString()

        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view1, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd/MM/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.edtJoining.setText(sdf.format(cal.time))
            }

        bindingDialog.imgDialogPicDate.setOnClickListener {
            activity?.let { it1 ->
                DatePickerDialog(
                    it1, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }

        bindingDialog.spDialogMemberShip.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    p2: Int,
                    p3: Long
                ) {
                    val value = bindingDialog.spDialogMemberShip.selectedItem.toString().trim()
                    if (value == "Select") {
                        bindingDialog.edtDialogExpire.setText("")
                        calculateTotal(
                            bindingDialog.spDialogMemberShip,
                            bindingDialog.edtDialogDiscount,
                            bindingDialog.edtDialogAmount
                        )

                    } else {
                        if (binding.edtJoining.text.toString().trim().isNotEmpty()) {
                            if (value == "1 Month") {
                                calculateExpireDate(1, binding.edtJoining, binding.edtExpire)
                                calculateTotal(
                                    bindingDialog.spDialogMemberShip,
                                    bindingDialog.edtDialogDiscount,
                                    bindingDialog.edtDialogAmount
                                )
                            } else if (value == "3 Months") {
                                calculateExpireDate(3, binding.edtJoining, binding.edtExpire)
                                calculateTotal(
                                    bindingDialog.spDialogMemberShip,
                                    bindingDialog.edtDialogDiscount,
                                    bindingDialog.edtDialogAmount
                                )
                            } else if (value == "6 Months") {
                                calculateExpireDate(6, binding.edtJoining, binding.edtExpire)
                                calculateTotal(
                                    bindingDialog.spDialogMemberShip,
                                    bindingDialog.edtDialogDiscount,
                                    bindingDialog.edtDialogAmount
                                )
                            } else if (value == "1 Year") {
                                calculateExpireDate(12, binding.edtJoining, binding.edtExpire)
                                calculateTotal(
                                    bindingDialog.spDialogMemberShip,
                                    bindingDialog.edtDialogDiscount,
                                    bindingDialog.edtDialogAmount
                                )
                            } else if (value == "3 Years") {
                                calculateExpireDate(36, binding.edtJoining, binding.edtExpire)
                                calculateTotal(
                                    bindingDialog.spDialogMemberShip,
                                    bindingDialog.edtDialogDiscount,
                                    bindingDialog.edtDialogAmount
                                )
                            }

                        } else {
                            showToast("Select Joining date first")
                            bindingDialog.spDialogMemberShip.setSelection(0)
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }

        binding.edtDiscount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null) {
                    calculateTotal(binding.spMemberShip, binding.edtDiscount, binding.edtAmount)
                }
            }
        })

        binding.radioGroup.setOnCheckedChangeListener { radioGroup, id ->
            when (id) {
                R.id.rdMale -> {
                    gender = "Male"
                }

                R.id.rdFemale -> {
                    gender = "Female"
                }
            }
        }

        binding.btnAddMemberSave.setOnClickListener {
            if (validate()) {
                saveData()
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

        binding.imgTakeImage.setOnClickListener {
            getImage()
        }
        getFee()
        binding.btnActiveInactive.setOnClickListener {
            try {
                if (getStatus() == "A") {
                    val sqlQuery = "UPDATE MEMBER SET STATUS = 'D' WHERE ID='$ID'"
                    db?.fireQuery(sqlQuery)
                    showToast("Member is Inactive now")
                } else {
                    val sqlQuery = "UPDATE MEMBER SET STATUS = 'A' WHERE ID ='$ID'"
                    db?.fireQuery(sqlQuery)
                    showToast("Member is Active now")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (ID.trim().isNotEmpty()) {
            if (getStatus() == "A") {
                binding.btnActiveInactive.text = "Inactive"
                binding.btnActiveInactive.visibility = View.VISIBLE
            } else {
                binding.btnActiveInactive.text = "Active"
                binding.btnActiveInactive.visibility = View.VISIBLE
            }
            loadData()
        } else {
            binding.btnActiveInactive.visibility = View.GONE
        }

        binding.btnRenewalSave.setOnClickListener {
            if (ID.trim().isNotEmpty()) {
                openRenewalDialog()
            }
        }
    }

    private fun getStatus(): String {
        var status = " "
        try {
            val sqlQuery = " SELECT STATUS FROM MEMBER WHERE ID = '$ID'"
            db?.fireQuery(sqlQuery)?.use {
                if (it.count > 0) {
                    status = MyFunction.getValue(it, "STATUS")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return status
    }

    private fun getFee() {
        try {
            val sqlQuery = "SELECT  * FROM FEE WHERE ID = '1' "
            db?.fireQuery(sqlQuery)?.use {
                if (it.count > 0) {
                    oneMonth = MyFunction.getValue(it, "ONE_MONTH")
                    threeMonths = MyFunction.getValue(it, "THREE_MONTHs")
                    sixMonths = MyFunction.getValue(it, "SIX_MONTHs")
                    oneYear = MyFunction.getValue(it, "ONE_YEAR")
                    threeYear = MyFunction.getValue(it, "THREE_YEARS")
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateTotal(spMember: Spinner, edtDis: EditText, edtAmt: EditText) {
        val month = spMember.selectedItem.toString().trim()
        var discount = edtDis.text.toString().trim()
        if (edtDis.text.toString().toString().isEmpty()) {
            discount = "0"
        }
        if (month == "Select") {
            edtAmt.setText("")
        } else if (month == "1 Month") {
            if (discount.trim().isEmpty()) {
                discount = "0"
            }
            if (oneMonth!!.trim().isNotEmpty()) {
                val discountAmount =
                    ((oneMonth!!.toDouble() * discount.toDouble()) / 100) // finding out the discount amount

                val total =
                    oneMonth!!.toDouble() - discountAmount // Minus discount amount from main amount
                edtAmt.setText(String.format("%.2f", total))
            }

        } else if (month == "3 Months") {
            if (discount.trim().isEmpty()) {
                discount = "0"
            }

            if (threeMonths!!.trim().isNotEmpty()) {
                val discountAmount =
                    ((threeMonths!!.toDouble() * discount.toDouble()) / 100) // finding out the discount amount
                val total =
                    threeMonths!!.toDouble() - discountAmount // Minus discount amount from main amount
                edtAmt.setText(String.format("%.2f", total))
            }
        } else if (month == "6 Month") {
            if (discount.trim().isEmpty()) {
                discount = "0"
            }

            if (sixMonths!!.trim().isNotEmpty()) {
                val discountAmount =
                    ((sixMonths!!.toDouble() * discount.toDouble()) / 100) // finding out the discount amount
                val total =
                    sixMonths!!.toDouble() - discountAmount // Minus discount amount from main amount
                edtAmt.setText(String.format("%.2f", total))
            }

        } else if (month == "1 Years") {
            if (discount.trim().isEmpty()) {
                discount = "0"
            }

            if (oneYear!!.trim().isNotEmpty()) {
                val discountAmount =
                    ((oneYear!!.toDouble() * discount.toDouble()) / 100) // finding out the discount amount
                val total =
                    oneYear!!.toDouble() - discountAmount // Minus discount amount from main amount
                edtAmt.setText(String.format("%.2f", total))
            }

        } else if (month == "3 Years") {
            if (discount.trim().isEmpty()) {
                discount = "0"
            }

            if (threeYear!!.trim().isNotEmpty()) {
                val discountAmount =
                    ((threeYear!!.toDouble() * discount.toDouble()) / 100) // finding out the discount amount
                val total =
                    threeYear!!.toDouble() - discountAmount // Minus discount amount from main amount
                edtAmt.setText(String.format("%.2f", total))
            }

        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun calculateExpireDate(month: Int, edtJoining: EditText, edtExpiry: EditText) {
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

    private fun getImage() {
        val items: Array<CharSequence>
        try {

            items = arrayOf("Take Photo", "Choose Image", "Cancel")
            val builder = android.app.AlertDialog.Builder(activity)
            builder.setCancelable(false)
            builder.setTitle("Select Image")
            builder.setItems(items) { dialogInterface, i ->
                if (items[i] == "Take Photo") {
                    RuntimePermission.askPermission(this)
                        .request(android.Manifest.permission.CAMERA)
                        .onAccepted {
                            takePicture()
                        }
                        .onDenied {
                            android.app.AlertDialog.Builder(activity)
                                .setMessage("Please accept our permission to capture image")
                                .setPositiveButton("Yes") { dialogInterface, i ->
                                    it.askAgain()
                                }

                                .setNegativeButton("No") { dialogInterface, i ->
                                    dialogInterface.dismiss()
                                }
                                .show()
                        }.ask()

                } else if (items[i] == "Choose Image") {
                    RuntimePermission.askPermission(this)
                        .request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .onAccepted {
                            takeFromGallery()
                        }
                        .onDenied {
                            android.app.AlertDialog.Builder(activity)
                                .setMessage("Please accept our permission to capture image")
                                .setPositiveButton("Yes") { dialogInterface, i ->
                                    it.askAgain()
                                }

                                .setNegativeButton("No") { dialogInterface, i ->
                                    dialogInterface.dismiss()
                                }
                                .show()
                        }.ask()

                } else {
                    dialogInterface.dismiss()
                }
            }
            builder.show()

        } catch (e: Exception) {
            e.printStackTrace()

        }
    }

    private fun takePicture() {
        val takePicIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureImage?.setImageUri())
        takePicIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(takePicIntent, REQUEST_CAMERA)
    }

    @Suppress("DEPRECATION")
    private fun takeFromGallery() {
        val intent = Intent()
        intent.type = "image/jpg"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            captureImage(captureImage?.getRightAngleImage(captureImage?.imagePath).toString())
        } else if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            captureImage(
                captureImage?.getRightAngleImage(
                    captureImage?.getPath(
                        data?.data,
                        context
                    )
                ).toString()
            )
        }
    }

    private fun captureImage(path: String) {
        Log.d("FragmentAdd", "imagePath: $path")
        getImagePath(captureImage?.decodeFile(path))
    }

    private fun getImagePath(bitmap: Bitmap?) {
        val tempUri: Uri? = captureImage?.getImageUri(activity, bitmap)
        actualImagePath = captureImage?.getRealPathFromURI(tempUri, activity).toString()
        Log.d("FragmentAdd", "ActualImagePath: $actualImagePath")

        activity?.let {
            Glide.with(it)
                .load(actualImagePath)
                .into(binding.imgPic)
        }
    }

    private fun validate(): Boolean {
        if (binding.edtFirstName.text.toString().trim().isEmpty()) {
            showToast("Enter first name")
            return false
        } else if (binding.edtLastName.text.toString().trim().isEmpty()) {
            showToast("Enter last name")
            return false
        } else if (binding.edtAge.text.toString().trim().isEmpty()) {
            showToast("Enter age")
            return false
        } else if (binding.edtMobile.text.toString().trim().isEmpty()) {
            showToast("Enter mobile number")
            return false
        }
        return true
    }

    private fun saveData() {
        try {
            var myIncrementId = ""
            if (ID.trim().isEmpty()) {
                myIncrementId = getIncrementId()
            } else {
                myIncrementId = ID
            }
            val sqlQuery = "INSERT OR REPLACE INTO MEMBER(ID, FIRST_NAME, LAST_NAME, GENDER, AGE," +
                    "WEIGHT, MOBILE, ADDRESS, DATE_OF_JOINING, MEMBERSHIP, EXPIRE_ON, DISCOUNT, TOTAL, IMAGE_PATH, STATUS) VALUES" +
                    "('" + myIncrementId + "'," + DatabaseUtils.sqlEscapeString(
                binding.edtFirstName.text.toString().trim()
            ) + ", " +
                    "" + DatabaseUtils.sqlEscapeString(
                binding.edtLastName.text.toString().trim()
            ) + ", '" + gender + "'," +
                    "'" + binding.edtAge.text.toString()
                .trim() + "', '" + binding.edtWeight.text.toString().trim() + "', " +
                    "" + binding.edtMobile.text.toString()
                .trim() + ", " + DatabaseUtils.sqlEscapeString(
                binding.edtAddress.text.toString().trim()
            ) + ","
            "'" + MyFunction.returnSQLDateFormat(
                binding.edtJoining.text.toString()
                    .trim()
            ) + "', '" + binding.spMemberShip.selectedItem.toString().trim() + "', " +
                    "'" + MyFunction.returnSQLDateFormat(
                binding.edtExpire.text.toString()
                    .trim()
            ) + "', '" + binding.edtDiscount.text.toString().trim() + "'," +
                    "'" + binding.edtAmount.text.toString()
                .trim() + "','" + actualImagePath + "', 'A')"
            db?.executeQuery(sqlQuery)
            showToast("Data saved successfully.")

            if (ID.trim().isEmpty()) {
                clearData()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getIncrementId(): String {
        var incrementId = ""
        try {
            val sqlQuery = "SELECT ISNULL (MAX(ID)+1, '1') AS ID FROM MEMBER"
            db?.fireQuery(sqlQuery)?.use {
                if (it.count > 0) {
                    incrementId = MyFunction.getValue(it, "ID")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return incrementId
    }

    private fun clearData() {
        binding.edtFirstName.setText("")
        binding.edtLastName.setText("")
        binding.edtAge.setText("")
        binding.edtWeight.setText("")
        binding.edtMobile.setText("")
        binding.edtAddress.setText("")
        binding.edtJoining.setText("")
        actualImagePath = ""

        Glide.with(this)
            .load(R.drawable.boy)
            .into(binding.imgPic)
    }

    private fun loadData() {
        try {
            val sqlQuery = "SELECT * FROM MEMBER WHERE ID = '$ID'"
            db?.fireQuery(sqlQuery)?.use {
                if (it.count > 0) {
                    val firstName = MyFunction.getValue(it, "FIRST_NAME")
                    val lastName = MyFunction.getValue(it, "LAST_NAME")
                    val age = MyFunction.getValue(it, "AGE")
                    val gender = MyFunction.getValue(it, "GENDER")
                    val weight = MyFunction.getValue(it, "WEIGHT")
                    val mobileNo = MyFunction.getValue(it, "MOBILE")
                    val address = MyFunction.getValue(it, "ADDRESS")
                    val dateofJoin = MyFunction.getValue(it, "DATE_OF_JOINING")
                    val membership = MyFunction.getValue(it, "MEMBERSHIP")
                    val expiry = MyFunction.getValue(it, "EXPIRY_ON")
                    val discount = MyFunction.getValue(it, "DISCOUNT")
                    val total = MyFunction.getValue(it, "TOTAL")
                    actualImagePath = MyFunction.getValue(it, "IMAGE_PATH")

                    binding.edtFirstName.setText(firstName)
                    binding.edtLastName.setText(lastName)
                    binding.edtAge.setText(age)
                    binding.edtWeight.setText(weight)
                    binding.edtMobile.setText(mobileNo)
                    binding.edtAddress.setText(address)
                    binding.edtJoining.setText(MyFunction.returnUserDateFormat(dateofJoin))
                    binding.edtFirstName.setText(firstName)
                    binding.edtFirstName.setText(firstName)

                    if (actualImagePath.isNotEmpty()) {
                        Glide.with(this)
                            .load(actualImagePath)
                            .into(binding.imgPic)

                    } else {
                        if (gender == "Male") {
                            Glide.with(this)
                                .load(R.drawable.boy)
                                .into(binding.imgPic)
                        } else {
                            Glide.with(this)
                                .load(R.drawable.girl)
                                .into(binding.imgPic)
                        }
                    }
                    if (membership.trim().isNotEmpty()) {
                        when (membership) {
                            "1 Month" -> {
                                binding.spMemberShip.setSelection(1)
                            }

                            "3 Months" -> {
                                binding.spMemberShip.setSelection(2)
                            }

                            " 6 Months" -> {
                                binding.spMemberShip.setSelection(3)
                            }

                            " 1 Year" -> {
                                binding.spMemberShip.setSelection(4)
                            }

                            "3 Years" -> {
                                binding.spMemberShip.setSelection(5)
                            }

                            else -> {
                                binding.spMemberShip.setSelection(0)
                            }
                        }
                    }
                    if (gender == "Male") {
                        binding.radioGroup.check(R.id.rdMale)
                    } else {
                        binding.radioGroup.check(R.id.rdFemale)
                    }
                    binding.edtExpire.setText(MyFunction.returnUserDateFormat(expiry))
                    binding.edtAmount.setText(total)
                    binding.edtDiscount.setText(discount)

                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                    val eDate = sdf.parse(expiry)
                    if (eDate!!.after(Date())) { // if expiry date greater than current date than
                        binding.btnRenewalSave.visibility = View.GONE
                    } else {
                        if (getStatus() == "A") {
                            binding.btnRenewalSave.visibility = View.VISIBLE
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun openRenewalDialog() {
        bindingDialog = RenewDialogBinding.inflate(LayoutInflater.from(activity))
        val dialog = Dialog(activity!!, style.AlertDialogCustom)
        dialog.setContentView(bindingDialog.root)
        dialog.setCancelable(false)
        dialog.show()

        bindingDialog.edtDialogJoining.setText(binding.edtExpire.text.toString().trim())
        bindingDialog.imgDialogRenewBack.setOnClickListener {
            dialog.dismiss()
        }

        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view1, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd/MM/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                bindingDialog.edtDialogJoining.setText(sdf.format(cal.time))
            }

        bindingDialog.imgDialogPicDate.setOnClickListener {
            activity?.let { it1 ->
                DatePickerDialog(
                    it1, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

        }

        bindingDialog.spDialogMemberShip.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    p2: Int,
                    p3: Long
                ) {
                    val value = bindingDialog.spDialogMemberShip.selectedItem.toString().trim()
                    if (value == "Select") {
                        bindingDialog.edtDialogExpire.setText("")
                        calculateTotal(
                            bindingDialog.spDialogMemberShip,
                            bindingDialog.edtDialogDiscount,
                            bindingDialog.edtDialogAmount
                        )

                    } else {
                        if (binding.edtJoining.text.toString().trim().isNotEmpty()) {
                            if (value == "1 Month") {
                                calculateExpireDate(
                                    1,
                                    bindingDialog.edtDialogJoining,
                                    bindingDialog.edtDialogExpire
                                )
                                calculateTotal(
                                    bindingDialog.spDialogMemberShip,
                                    bindingDialog.edtDialogDiscount,
                                    bindingDialog.edtDialogAmount
                                )
                            } else if (value == "3 Months") {
                                calculateExpireDate(
                                    3, bindingDialog.edtDialogJoining,
                                    bindingDialog.edtDialogExpire
                                )
                                calculateTotal(
                                    bindingDialog.spDialogMemberShip,
                                    bindingDialog.edtDialogDiscount,
                                    bindingDialog.edtDialogAmount
                                )
                            } else if (value == "6 Months") {
                                calculateExpireDate(
                                    6, bindingDialog.edtDialogJoining,
                                    bindingDialog.edtDialogExpire
                                )
                                calculateTotal(
                                    bindingDialog.spDialogMemberShip,
                                    bindingDialog.edtDialogDiscount,
                                    bindingDialog.edtDialogAmount
                                )
                            } else if (value == "1 Year") {
                                calculateExpireDate(
                                    12, bindingDialog.edtDialogJoining,
                                    bindingDialog.edtDialogExpire
                                )
                                calculateTotal(
                                    bindingDialog.spDialogMemberShip,
                                    bindingDialog.edtDialogDiscount,
                                    bindingDialog.edtDialogAmount
                                )
                            } else if (value == "3 Years") {
                                calculateExpireDate(
                                    36, bindingDialog.edtDialogJoining,
                                    bindingDialog.edtDialogExpire
                                )
                                calculateTotal(
                                    bindingDialog.spDialogMemberShip,
                                    bindingDialog.edtDialogDiscount,
                                    bindingDialog.edtDialogAmount
                                )
                            }

                        } else {
                            showToast("Select Joining date first")
                            bindingDialog.spDialogMemberShip.setSelection(0)
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }

        bindingDialog.edtDialogDiscount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null) {
                    calculateTotal(
                        bindingDialog.spDialogMemberShip,
                        bindingDialog.edtDialogDiscount,
                        bindingDialog.edtDialogAmount
                    )
                }
            }

        })
        bindingDialog.btnDialogRenewSave.setOnClickListener {
            if (bindingDialog.spDialogMemberShip.selectedItem.toString().trim() != "Select") {
                try {
                    val sqlQuery =
                        "UPDATE MEMBER SET DATE_OF_JOINING='" + MyFunction.returnSQLDateFormat(
                            bindingDialog.edtDialogJoining.text.toString().trim()
                        ) + "'," +
                                "MEMBERSHIP='" + bindingDialog.spDialogMemberShip.selectedItem.toString()
                            .trim() + "'," +
                                "EXPIRE_ON='" + MyFunction.returnSQLDateFormat(
                            bindingDialog.edtDialogExpire.text.toString().trim() + "'," +
                                    "DISCOUNT='" + bindingDialog.edtDialogDiscount.text.toString()
                                .trim() + "'," +
                                    "TOTAL='" + bindingDialog.edtDialogAmount.text.toString()
                                .trim() + "' WHERE ID = '" + ID + "'"
                        )
                    db?.executeQuery(sqlQuery)
                    showToast("Members data saved successfully")
                    dialog.dismiss()
                    loadData()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                showToast("Select Membership")
            }
        }
    }
}

