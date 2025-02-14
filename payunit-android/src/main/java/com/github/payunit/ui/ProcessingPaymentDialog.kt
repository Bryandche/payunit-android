package com.github.payunit.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import com.example.payunit_android.databinding.DialogLoadingBinding

class ProcessingPaymentDialog(context: Context) : AppCompatDialog(context) {

    private lateinit var binding: DialogLoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using data binding
        binding = DialogLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set dialog properties
        setCancelable(true) // Prevent dismissal on back press
        setCanceledOnTouchOutside(true) // Prevent dismissal on outside touch

        // Set up the cancel button listener
//        binding.btnCancel.setOnClickListener {
//            dismiss() // Close the dialog
//        }
    }

}