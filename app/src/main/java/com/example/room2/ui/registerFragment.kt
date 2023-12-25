package com.example.room2.ui

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.room2.Public_page.publicHomeActivity
import com.example.room2.R
import com.example.room2.data.Users
import com.example.room2.databinding.FragmentRegisterBinding
import com.example.room2.sharedPreferences.prefData
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [registerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class registerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentRegisterBinding
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var etUsername: TextInputLayout
    private lateinit var etPhone: TextInputLayout
    private lateinit var etEmail: TextInputLayout
    private lateinit var etPassword: TextInputLayout
    private lateinit var btCreate: AppCompatButton
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private val firestore = FirebaseFirestore.getInstance()
    //    private lateinit var sharedPref : sharedPreference
    private val userCollectionRef = firestore.collection("users")
    private lateinit var database: FirebaseFirestore
    private var updateId = ""
    private val userLivelistData: MutableLiveData<List<Users>> by lazy {
        MutableLiveData<List<Users>>()
    }
    private val channelid = "com.example.room2.RegistrationChannel"


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
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")
        etUsername = view.findViewById(R.id.et_name_regis)
        etPhone = view.findViewById(R.id.et_phone_regis)
        etEmail = view.findViewById(R.id.et_email_regis)
        etPassword = view.findViewById(R.id.et_password_regis)
        btCreate = view.findViewById(R.id.bt_create_regis)

        with(binding) {
            btCreateRegis.setOnClickListener {
                val signUpUsername = etUsername.editText?.text.toString()
                val signUpPhone = etPhone.editText?.text.toString()
                val signUpEmail = etEmail.editText?.text.toString()
                val signUpPassword = etPassword.editText?.text.toString()
                if(signUpUsername.isNotEmpty() && signUpPassword.isNotEmpty()&& signUpPhone.isNotEmpty() && signUpEmail.isNotEmpty()) {
                    signUp(signUpUsername, signUpPhone, "PUBLIC", signUpEmail, signUpPassword)
                }else{
                    Toast.makeText(activity, "All field cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment registerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            registerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun signUp(username:String, phone:String, role:String, email:String, password:String){
        val data = hashMapOf(
            "id" to "",
            "username" to username,
            "phone" to phone,
            "role" to role,
            "email" to email,
            "password" to password
        )
        userCollectionRef.add(data)
            .addOnSuccessListener { documentReference ->
                val createdDataFilmId = documentReference.id
                data["id"] = createdDataFilmId
                documentReference.set(data)
                    .addOnSuccessListener {
                        showRegistrationNotification()
                        val sharedPref = activity?.getSharedPreferences(prefData.SHAREDPREF, Context.MODE_PRIVATE)
                        if (sharedPref != null) {
                            with(sharedPref.edit()) {
                                putString(prefData.UID, createdDataFilmId)
                                putString(prefData.USERNAME, username)
                                putString(prefData.EMAIL, email)
                                putString(prefData.PHONES, phone)
                                putBoolean(prefData.IS_LOGIN, true)
                                apply()
                            }
                        }
                    }.addOnFailureListener {
                                    Log.d("MainActivity", "Error updating data ID: ", it)
                    }
            }
            .addOnFailureListener {
                Log.d("MainActivity", "Error adding data: ", it)
            }
        startActivity(Intent(activity, publicHomeActivity::class.java))
        requireActivity().finish()

//        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
//            ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (!snapshot.exists()) {
//                    Log.d("SignUp", "User does not exist")
//                    val id = databaseReference.push().key
//                    val userData = id?.let { Users(it, username, phone, role, email, password) }
//                    databaseReference.child(id!!).setValue(userData)
//                    Toast.makeText(
//                        activity,
//                        "Account succesfully registered",
//                        Toast.LENGTH_SHORT
//                    ).show()
//
//                } else {
//                    Log.d("SignUp", "User does not exist")
//                    Toast.makeText(
//                        activity,
//                        "Account failed created",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(activity, "Database error !", Toast.LENGTH_SHORT).show()
//            }
        }

    private fun saveDataToSharedPreferences(userData: Users) {
        val sharedPref = activity?.getSharedPreferences(prefData.SHAREDPREF, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(prefData.UID, userData.id)
            putString(prefData.USERNAME, userData.username)
            putString(prefData.EMAIL, userData.email)
            putString(prefData.PHONES, userData.phone)
            putBoolean(prefData.IS_LOGIN, true)
            apply()
        }
    }

    @SuppressLint("ServiceCast")
    private fun showRegistrationNotification() {
        val notificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelid,
                "Registration Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(requireContext(), channelid)
            .setSmallIcon(R.drawable.notifications)
            .setContentTitle("Registrasi berhasil ")
            .setContentText("Silahkan menikmati list film yang tersedia")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(1, notificationBuilder.build())
        }
}