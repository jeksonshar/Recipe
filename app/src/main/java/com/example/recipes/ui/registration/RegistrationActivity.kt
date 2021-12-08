package com.example.recipes.ui.registration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.recipes.R
import com.example.recipes.datasouce.TestData

class RegistrationActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val test = TestData.DATA_1
        when(test) {
            TestData.DATA_1 -> {}
            TestData.DATA_2-> {}
            TestData.DATA_3 -> {}
            TestData.DATA_4 -> {}
        }.exclusive
    }
}

val <T> T.exclusive: T
    get() = this