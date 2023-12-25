package com.example.room2.Admin

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
import com.example.room2.data.dataFilm
import com.example.room2.databinding.ActivityInputBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

//KODE UNTUK INPUT DATAFILM
//AKAN MENAMBAH DAN MENGUPDATE DATA FILM

class InputActivity : AppCompatActivity() {
    private lateinit var binding : ActivityInputBinding
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var storageRef : StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private val dataFilmCollectionRef = firestore.collection("datafilm")
    private var updateId = ""
    private var imageURI : Uri? = null
    private lateinit var image : String
    private var imagecurent: Uri? = null
    private val dataFilmListLiveData: MutableLiveData<List<dataFilm>> by lazy {
        MutableLiveData<List<dataFilm>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInputBinding.inflate(layoutInflater)
        storageRef = FirebaseStorage.getInstance().reference
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        var command = intent.getStringExtra("COMMAND")
        registerClickEvents()

        with(binding){

            if(command=="UPDATE"){
                binding.btnUpdate.isVisible = true
                binding.btnAdd.isVisible = false

                updateId = intent.getStringExtra("ID").toString()
                var item_tittle = intent.getStringExtra("JUDUL")
                var item_director = intent.getStringExtra("SUTRADARA")
                var item_duration = intent.getStringExtra("DURASI")
                var item_genre = intent.getStringExtra("GENRE")
                var item_price = intent.getStringExtra("HARGA")
                var item_description = intent.getStringExtra("DESKRIPSI")
                image = intent.getStringExtra("IMAGE").toString()
                imagecurent = Uri.parse(image)

                binding.etTittleInput.setText(item_tittle.toString())
                binding.etDirectorInput.setText(item_director.toString())
                binding.etDurationInput.setText(item_duration.toString())
                binding.etGenreInput.setText(item_genre.toString())
                binding.etPriceInput.setText(item_price.toString())
                binding.etDescriptionInput.setText(item_description.toString())
                Glide.with(this@InputActivity)
                    .load(image)
                    .centerCrop()
                    .into(binding.imgImageInput)


            }else{
                binding.btnUpdate.isVisible = false
                binding.btnAdd.isVisible = true
            }

            btnAdd.setOnClickListener {
                if (validateInput()) {
                    val new_tittle = etTittleInput.text.toString()
                    val new_director = etDirectorInput.text.toString()
                    val new_duration = etDurationInput.text.toString()
                    val new_genre = etGenreInput.text.toString()
                    val new_price = etPriceInput.text.toString()
                    val new_description = etDescriptionInput.text.toString()
                    binding.barProgressInput.visibility = View.VISIBLE
                    val name = System.currentTimeMillis().toString()+""+getFileName(imageURI!!)
                    imageURI?.let { storageRef.child(name).putFile(imageURI!!).addOnCompleteListener{ task->
                        if(task.isSuccessful){
                            Toast.makeText(this@InputActivity, "berhasil", Toast.LENGTH_LONG).show()
                            storageRef.child(name).downloadUrl.addOnSuccessListener { uri->
//                                val map = HashMap<String, Any>()
//                                map["pic"] = uri.toString()
                                val newData= dataFilm(
                                    tittle = new_tittle,
                                    image_url = uri.toString(),
                                    director = new_director,
                                    duration = new_duration,
                                    genre = new_genre,
                                    price = new_price,
                                    description = new_description
                                )
                                addFilm(newData)
                                val IntentToHome = Intent(this@InputActivity, adminHomeActivity::class.java)
                                IntentToHome.putExtra("NOTIF", "create")
                                startActivity(IntentToHome)
                                binding.barProgressInput.visibility = View.GONE
                                binding.imgImageInput.setImageResource(R.drawable.upload_image)
                                finish()
                            }
                        }else{
                            Toast.makeText(this@InputActivity,
                                "${ task.isSuccessful }", Toast.LENGTH_SHORT).show()
                            Toast.makeText(this@InputActivity, task.exception?.message, Toast.LENGTH_SHORT).show()
                            binding.barProgressInput.visibility = View.GONE
                            binding.imgImageInput.setImageResource(R.drawable.upload_image)
                        }
                    } }
                }else{
                    Toast.makeText(this@InputActivity, "Kolom Tidak Boleh Kosong !!", Toast.LENGTH_SHORT).show()
                }
            }

            btnUpdate.setOnClickListener {
                if (validateInput()) {
                    val new_tittle = etTittleInput.text.toString()
                    val new_director = etDirectorInput.text.toString()
                    val new_duration = etDurationInput.text.toString()
                    val new_genre = etGenreInput.text.toString()
                    val new_price = etPriceInput.text.toString()
                    val new_description = etDescriptionInput.text.toString()
                    binding.barProgressInput.visibility = View.VISIBLE
                    Toast.makeText(this@InputActivity, "", Toast.LENGTH_SHORT).show()

                    if(imagecurent == Uri.parse(image)) {
                        val dataToUpdate = dataFilm(
                            tittle = new_tittle,
                            director = new_director,
                            image_url = image,
                            duration = new_duration,
                            genre = new_genre,
                            price = new_price,
                            description = new_description
                        )

                        updateDataFilm(dataToUpdate)
                        updateId = ""
                        setEmptyField()
                        val IntentToHome = Intent(this@InputActivity, adminHomeActivity::class.java)
                        IntentToHome.putExtra("NOTIF", "update")
                        startActivity(IntentToHome)
                        binding.barProgressInput.visibility = View.GONE
                        binding.imgImageInput.setImageResource(R.drawable.upload_image)
                        finish()
                    }else{
                        val name = System.currentTimeMillis().toString()+""+getFileName(imageURI!!)
                        imageURI?.let { storageRef.child(name).putFile(imageURI!!).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this@InputActivity, "berhasil", Toast.LENGTH_LONG).show()
                                storageRef.child(name).downloadUrl.addOnSuccessListener { uri ->
                                    val dataToUpdate = dataFilm(
                                        tittle = new_tittle,
                                        director = new_director,
                                        image_url = uri.toString(),
                                        duration = new_duration,
                                        genre = new_genre,
                                        price = new_price,
                                        description = new_description
                                    )

                                    updateDataFilm(dataToUpdate)
                                    updateId = ""
                                    setEmptyField()
                                    val IntentToHome = Intent(this@InputActivity, adminHomeActivity::class.java)
                                    IntentToHome.putExtra("NOTIF", "update")
                                    startActivity(IntentToHome)
                                    binding.barProgressInput.visibility = View.GONE
                                    binding.imgImageInput.setImageResource(R.drawable.upload_image)
                                    finish()
                                }
                            } else {
                                Toast.makeText(
                                    this@InputActivity,
                                    task.exception?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.barProgressInput.visibility = View.GONE
                                binding.imgImageInput.setImageResource(R.drawable.upload_image)
                            }
                        }}
                    }
                }else{
                    Toast.makeText(this@InputActivity, "Kolom Tidak Boleh Kosong !!", Toast.LENGTH_SHORT).show()
                }
            }
            btBackInput.setOnClickListener{
                startActivity(Intent(this@InputActivity, adminHomeActivity::class.java))
                finish()
            }
        }
    }

//    private fun UploadImage(){
//        binding.barProgressInput.visibility = View.VISIBLE
//        storageRef = storageRef.child(System.currentTimeMillis().toString())
//        imageURI?.let { storageRef.putFile(it).addOnCompleteListener{ task->
//            if(task.isSuccessful){
//                storageRef.downloadUrl.addOnSuccessListener {uri->
//                    val map = HashMap<String, Any>()
//                    map["pic"] = uri.toString()
//
//                    firestore.collection("images").add(map).addOnCompleteListener{ firestoretask->
//                        if(firestoretask.isSuccessful){
//                            Toast.makeText(this, "Data succesfully added", Toast.LENGTH_SHORT).show()
//                        }else{
//                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
//                        }
//                        binding.barProgressInput.visibility = View.GONE
//                        binding.imgImageInput.setImageResource(R.drawable.upload_image)
//                    }
//
//                }
//            }else{
//                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
//                binding.barProgressInput.visibility = View.GONE
//                binding.imgImageInput.setImageResource(R.drawable.upload_image)
//            }
//        } }
//    }

    private fun registerClickEvents(){
        binding.imgImageInput.setOnClickListener {
            resultLauncher.launch("image/*")
        }
    }


    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { result->
        result?.let { uri ->
            imageURI = uri
            binding.imgImageInput.setImageURI(uri)
            imagecurent = uri
        }
    }

    private fun addFilm(datafilm: dataFilm) {
        dataFilmCollectionRef.add(datafilm)
            .addOnSuccessListener { documentReference ->
                val createdDataFilmId = documentReference.id
                datafilm.id = createdDataFilmId
                documentReference.set(datafilm)
                    .addOnFailureListener {
                        Log.d("MainActivity", "Error updating data ID: ", it)
                    }
            }
            .addOnFailureListener {
                Log.d("MainActivity", "Error adding data: ", it)
            }
    }

//    private fun updateDataFilm(datafilm: dataFilm) {
//        datafilm.id = updateId
//        dataFilmCollectionRef.document(updateId).set(datafilm)
//            .addOnFailureListener {
//                Log.d("MainActivity", "Error updating data: ", it)
//            }
//    }

    private fun updateDataFilm(datafilm: dataFilm) {
        datafilm.id = updateId
        val dataToUpdate = hashMapOf(
            "tittle" to datafilm.tittle,
            "director" to datafilm.director,
            "duration" to datafilm.duration,
            "genre" to datafilm.genre,
            "price" to datafilm.price,
            "description" to datafilm.description,
            "image_url" to datafilm.image_url
        )

        val mutableDataToUpdate: MutableMap<String, Any> = dataToUpdate as MutableMap<String, Any>

        dataFilmCollectionRef.document(updateId).update(mutableDataToUpdate)
            .addOnFailureListener {
                Log.d("MainActivity", "Error updating data: ", it)
            }
    }

    private fun setEmptyField() {
        binding.etTittleInput.setText("")
        binding.etDirectorInput.setText("")
        binding.etDurationInput.setText("")
        binding.etGenreInput.setText("")
        binding.etPriceInput.setText("")
        binding.etDescriptionInput.setText("")
    }

    private fun validateInput(): Boolean {
        with(binding) {
            if(etTittleInput.text.toString()!="" && etDirectorInput.text.toString()!="" && etDurationInput.text.toString()!=""
                && etGenreInput.text.toString()!="" && etPriceInput.text.toString()!="" && etDescriptionInput.text.toString()!=""){
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