package com.example.room2.Admin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.room2.R
import com.example.room2.data.Users
import com.example.room2.data.dataFilm
import com.example.room2.databinding.ActivityEditProfilesBinding
import com.example.room2.databinding.ActivityInputBinding
import com.example.room2.sharedPreferences.prefData
import com.example.room2.sharedPreferences.prefData.Companion.EMAIL
import com.example.room2.sharedPreferences.prefData.Companion.PHONES
import com.example.room2.sharedPreferences.prefData.Companion.SHAREDPREF
import com.example.room2.sharedPreferences.prefData.Companion.UID
import com.example.room2.sharedPreferences.prefData.Companion.USERNAME
import com.example.room2.ui.loginFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView


//KODE UNTUK EDIT PROFILE
//AKAN MENGUPDATE Data PROFILE DI DATABASE

class editProfilesActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditProfilesBinding
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var storageRef : StorageReference
    private val dataFilmCollectionRef = firestore.collection("datafilm")
    private val UserFilmCollectionRef = firestore.collection("users")
    private var updateId = ""
    private val dataFilmListLiveData: MutableLiveData<List<dataFilm>> by lazy {
        MutableLiveData<List<dataFilm>>()
    }

//    companion object {
//        const val SHAREDPREF = "shared_pref"
//        const val UID = "uid"
//        const val USERNAME = "username"
//        const val EMAIL = "email"
//        const val ROLES = "roles"
//        const val PHONES = "phones"
//        const val IMAGES = "image"
//    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEditProfilesBinding.inflate(layoutInflater)
        storageRef = FirebaseStorage.getInstance().reference.child("datafilm")
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences(SHAREDPREF, Context.MODE_PRIVATE)

        val ID = sharedPref.getString(UID, "")
        val USERNAME = sharedPref.getString(USERNAME, "")
        val PHONE = sharedPref.getString(PHONES, "")
        val EMAIL = sharedPref.getString(EMAIL, "")

        Log.d("UserDataDebug", "${ID}, ${USERNAME}, ${PHONE}, ${EMAIL}")



        with(binding){
                binding.btnUpdateProfiles.isVisible = true
                binding.etUsernameProfiles.setText(USERNAME.toString())
                binding.etPhonesProfiles.setText(PHONE.toString())
                binding.etEmailProfiles.setText(EMAIL.toString())


            btnUpdateProfiles.setOnClickListener {
                if (validateInput()) {
                    val new_username = binding.etUsernameProfiles.text.toString()
                    val new_email = binding.etEmailProfiles.text.toString()
                    val new_phone = binding.etPhonesProfiles.text.toString()

                    if (ID != null) {
                        updateDataUser(ID, new_username, new_phone, new_email)
                    }
                        updateId = ""
                        setEmptyField()
                        val IntentToHome = Intent(this@editProfilesActivity, adminHomeActivity::class.java)
                        IntentToHome.putExtra("NOTIF", "update")
                        startActivity(IntentToHome)
                        finish()
                    }
                }
            }
            binding.btBackInput.setOnClickListener{
                startActivity(Intent(this@editProfilesActivity, adminHomeActivity::class.java))
                finish()
            }
        }

    private fun updateDataUser(id: String, new_username:String, new_phone:String, new_email:String) {
        val userDocRef = UserFilmCollectionRef.document(id)

        val updateData = hashMapOf(
            "username" to new_username,
            "phone" to new_phone,
            "email" to new_email
        )

        userDocRef.update(updateData as Map<String, Any>)
            .addOnSuccessListener {
                val sharedPref = this?.getSharedPreferences(prefData.SHAREDPREF, Context.MODE_PRIVATE)
                if (sharedPref != null) {
                    with(sharedPref.edit()) {
                        putString(com.example.room2.sharedPreferences.prefData.UID, id)
                        putString(com.example.room2.sharedPreferences.prefData.USERNAME, new_username)
                        putString(com.example.room2.sharedPreferences.prefData.EMAIL, new_email)
                        putString(com.example.room2.sharedPreferences.prefData.PHONES, new_phone)
                        apply()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@editProfilesActivity, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setEmptyField() {
        binding.etUsernameProfiles.setText("")
        binding.etUsernameProfiles.setText("")
        binding.etPhonesProfiles.setText("")
    }

    private fun validateInput(): Boolean {
        with(binding) {
            if(etEmailProfiles.text.toString()!="" && etPhonesProfiles.text.toString()!="" && etUsernameProfiles.text.toString()!=""){
                return true
            }else{
                return false
            }
        }

    }

    private fun getFileName(uri: Uri): String {
        return uri.path?.lastIndexOf('/').let { uri.path?.substring(it!!)!! }
    }
}