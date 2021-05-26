package com.example.android.courseworkapp.model

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import com.example.android.courseworkapp.R

class DialogEventTitle(context: Context) : Dialog(context) {
    var gameTitle: String = "Default"
     lateinit var etGameTitle: EditText
     lateinit var btnAccept: Button
     lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_gametitle)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(false)

        etGameTitle = findViewById(R.id.etGameTitle)
        btnAccept = findViewById(R.id.btnAccept)
        btnCancel = findViewById(R.id.btnCancel)

//        btnAccept.setOnClickListener {
//            gameTitle = etGameTitle.text.toString()
//            dismiss()
//        }
        btnCancel.setOnClickListener {
            dismiss()
        }
    }


}