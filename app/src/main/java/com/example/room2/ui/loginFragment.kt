package com.example.room2.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.room2.Admin.adminHomeActivity
import com.example.room2.Public_page.publicHomeActivity
import com.example.room2.data.Users
import com.example.room2.R
import com.example.room2.sharedPreferences.prefData.Companion.EMAIL
import com.example.room2.sharedPreferences.prefData.Companion.IS_LOGIN
import com.example.room2.sharedPreferences.prefData.Companion.PHONES
import com.example.room2.sharedPreferences.prefData.Companion.SHAREDPREF
import com.example.room2.sharedPreferences.prefData.Companion.UID
import com.example.room2.sharedPreferences.prefData.Companion.USERNAME
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.firestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [loginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class loginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: CollectionReference
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    private var uid: String? = null
    private var roles: String? = null
    private lateinit var btLog: Button
//    private lateinit var sharedPref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentLogin = inflater.inflate(R.layout.fragment_login, container, false)
//        sharedPref = requireActivity().getSharedPreferences(SHAREDPREF, Context.MODE_PRIVATE)
//        uid = sharedpref.getString(UID, null)
        etEmail = fragmentLogin.findViewById(R.id.et_email_login)
        etPassword = fragmentLogin.findViewById(R.id.et_password_login)
        btLog = fragmentLogin.findViewById(R.id.login_btn)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = Firebase.firestore.collection("users")
        btLog.setOnClickListener {
            val signUpEmail = etEmail.text.toString()
            val signUpPassword = etPassword.text.toString()
            if( signUpPassword.isNotEmpty() && signUpEmail.isNotEmpty()) {
                login(signUpEmail, signUpPassword)
            }else{
                Toast.makeText(activity, "All field cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Inflate the layout for this fragment
        return fragmentLogin
    }

    @SuppressLint("SuspiciousIndentation")
    private fun login(email:String, password:String ){
        databaseReference.whereEqualTo("email", email).get().addOnSuccessListener { snapshot ->
            if (!snapshot.isEmpty) {
                for (document in snapshot.documents) {
                    val userData = document.toObject(Users::class.java)
                        Log.d("UserDataDebug", "userData: $userData")
                        if(userData!=null && userData.password == password){
                            Log.d("UserDataDebug", "Role: ${userData.role}")
                            Toast.makeText(activity, userData.role, Toast.LENGTH_SHORT).show()
                            if(userData.role == "PUBLIC"){
                                saveDataToSharedPreferences(userData)
                                startActivity(Intent(activity, publicHomeActivity::class.java))
                                requireActivity().finish()
                            }else if(userData.role == "ADMIN"){
                                saveDataToSharedPreferences(userData)
                                startActivity(Intent(activity, adminHomeActivity::class.java))
                                requireActivity().finish()
                            } else {
                                Toast.makeText(activity, "", Toast.LENGTH_SHORT).show()
                            }
                        }
                        Toast.makeText(activity, "Failed, username or password invalid", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { exception ->
        }
    }

    private fun saveDataToSharedPreferences(userData: Users) {
        val sharedPref = activity?.getSharedPreferences(SHAREDPREF, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(UID, userData.id)
            putString(USERNAME, userData.username)
            putString(EMAIL, userData.email)
            putString(PHONES, userData.phone)
            putBoolean(IS_LOGIN, true)
            apply()
        }
    }
}