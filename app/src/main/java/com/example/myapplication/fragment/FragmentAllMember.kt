package com.example.myapplication.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.AdapterLoadMember
import com.example.myapplication.databinding.FragmentAllMemberBinding
import com.example.myapplication.global.DB
import com.example.myapplication.global.MyFunction
import com.example.myapplication.model.AllMember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FragmentAllMember : BaseFragment() {
    private val TAG = "FragmentAllMember"
    private var db: DB? = null
    var adapter: AdapterLoadMember? = null
    var arrayList: ArrayList<AllMember> = ArrayList()

    private lateinit var binding: FragmentAllMemberBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = activity?.let { DB(it) }

        binding.rdGroupMember.setOnCheckedChangeListener { radioGroup, i ->
            when (id) {
                R.id.rdActiveMember -> {

                }

                R.id.rdInActiveMember -> {

                }
            }

        }
    }


    override fun onResume() {
        super.onResume()
        loadData("A")
    }

    private fun <R> CoroutineScope.executeAsyncTask(
        onPreExecute: () -> Unit,
        doInBackground: () -> R,
        onPostExecute: (R) -> Unit
    ) = launch {
        onPreExecute()
        val result = withContext(Dispatchers.IO) {
            doInBackground()
        }
        onPostExecute(result)
    }

    private fun loadData(memberStatus: String) {
        lifecycleScope.executeAsyncTask(onPreExecute = {
            showDialog("Processing....")
        }, doInBackground = {
            val sqlQuery = "SELECT * FROM MEMBER WHERE STATUS = '$memberStatus'"
            db?.fireQuery(sqlQuery)?.use {
                if (it.count > 0) {
                    it.moveToFirst()
                    do {
                        val list = AllMember(
                            id = MyFunction.getValue(it, "ID"),
                            firstName = MyFunction.getValue(it, "FIRST_NAME"),
                            lastName = MyFunction.getValue(it, "LAST_NAME"),
                            age = MyFunction.getValue(it, "AGE"),
                            gender = MyFunction.getValue(it, "GENDER"),
                            weight = MyFunction.getValue(it, "WEIGHT"),
                            mobile = MyFunction.getValue(it, "MOBILE"),
                            address = MyFunction.getValue(it, "ADDRESS"),
                            image = MyFunction.getValue(it, "IMAGE_PATH"),
                            dateOfJoining = MyFunction.returnUserDateFormat(
                                MyFunction.getValue(it, "DATE_OF_JOINING")
                            ),
                            expiryDate = MyFunction.returnUserDateFormat(
                                MyFunction.getValue(
                                    it,
                                    "EXPIRE_ON"
                                )
                            )
                        )
                        arrayList.add(list)
                    } while (it.moveToNext())

                }
            }
        }, onPostExecute = {
            if (arrayList.size > 0) {
                binding.recylerViewMember.visibility = View.VISIBLE
                binding.txtAllMemberNDF.visibility = View.GONE
                adapter = AdapterLoadMember(arrayList)
                binding.recylerViewMember.layoutManager = LinearLayoutManager(activity)
                binding.recylerViewMember.adapter = adapter
            } else {
                binding.recylerViewMember.visibility = View.VISIBLE
                binding.txtAllMemberNDF.visibility = View.GONE
            }
            CloseDialog()
        })
    }
}