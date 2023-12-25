package com.example.room2.Public_page

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.room2.R
import com.example.room2.databinding.ActivityDetailProfileBinding
import com.example.room2.databinding.ActivityEditProfilesBinding
import com.example.room2.sharedPreferences.prefData

//INISIASI KODE UNTUK PAGE DETAILPROFILE
//AKAN MENAMPILKAN DATA PROFILE USER


class detailProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences(prefData.SHAREDPREF, Context.MODE_PRIVATE)
        val ID = sharedPref.getString(prefData.UID, "")
        val USERNAME = sharedPref.getString(prefData.USERNAME, "")
        val PHONE = sharedPref.getString(prefData.PHONES, "")
        val EMAIL = sharedPref.getString(prefData.EMAIL, "")

        binding.txtUsernameProfiles.text = USERNAME
        binding.txtEmailProfile.text = EMAIL
        binding.txtPhonesProfile.text = PHONE
    }
}