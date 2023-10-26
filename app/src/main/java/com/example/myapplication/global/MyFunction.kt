package com.example.myapplication.global

import android.database.Cursor
import android.util.Log

class MyFunction {

    companion object {
        fun getValue(cursor: Cursor, columnName: String): String {
            var value: String = ""

            try {
                val col = cursor.getColumnIndex(columnName)
                value = cursor.getString(col)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("MyFunction", "getValue ${e.printStackTrace()}")
                value = ""
            }
            return value
        }
    }
}