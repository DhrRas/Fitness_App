package com.example.myapplication.manager

import android.content.Context
import android.content.SharedPreferences

public class SharedPrefs {
     private val TAG = SharedPrefs::class.java.simpleName

     companion object{
         var user_no = "User_no"
         var user_name= "User_name"
         var user_mobile = "User_mobile"
         var user_type = "User_type"

         private val SHARED_PREFS_FILE_NAME = "MyData"
         private fun getPrefs(context: Context): SharedPreferences {
          return context.getSharedPreferences(SHARED_PREFS_FILE_NAME,Context.MODE_PRIVATE)
         }

         fun save(context:Context, key:String, value:String) {
             getPrefs(context).edit().putString(key,value).apply()
         }

         fun getString(context: Context, key: String):String? {
             return getPrefs(context).getString(key,"")
         }

         fun clearSharedPref(context: Context){
             getPrefs(context).edit().clear().apply()
         }
     }
}