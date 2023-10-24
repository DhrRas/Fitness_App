package com.example.myapplication.global

object SqlTable {
    const val admin = "CREATE TABLE ADMIN(ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_NAME TEXT DEFAULT '', " +
            "PASSWORD TEXT DEFAULT '' ,MOBILE TEXT DEFAULT '' )"
}