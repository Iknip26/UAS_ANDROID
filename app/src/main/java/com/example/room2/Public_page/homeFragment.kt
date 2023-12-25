package com.example.room2.Public_page

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.room2.Public_page.dataFilmAdapter
import com.example.room2.R
import com.example.room2.data.dataFilm
import com.example.room2.sharedPreferences.prefData
import com.example.room2.swipeToDeleteCallback
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [homeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

//UNTUK MEMBUAT FRAGMENT HOME YANG AKAN DI TAMPILKAN DI DASHBOARD USER
class homeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val firestore = FirebaseFirestore.getInstance()
    private val dataFilmCollectionRef = firestore.collection("datafilm")
    private val usersCollectionRef = firestore.collection("users")
//    private lateinit var sharedpref: SharedPreferences
//    val sharedPreferences = context.getSharedPreferences("sharedprefdata", Context.MODE_PRIVATE)

    private var updateId = ""
    private var username: String? = null
    private var email: String? = null
    private var roles: String? = null
    private var showToast = ""
    private var currentid = ""
    private val dataFilmListLiveData: MutableLiveData<List<dataFilm>> by lazy {
        MutableLiveData<List<dataFilm>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val fragmentHome = inflater.inflate(R.layout.fragment_home, container, false)
        val sharedPref = requireActivity().getSharedPreferences(prefData.SHAREDPREF, Context.MODE_PRIVATE)
        val ID = sharedPref.getString(prefData.UID, "")
        val USERNAME = sharedPref.getString(prefData.USERNAME, "")
        val PHONE = sharedPref.getString(prefData.PHONES, "")
        val EMAIL = sharedPref.getString(prefData.EMAIL, "")
        val username_layout = fragmentHome.findViewById<TextView>(R.id.txt_username_home)
        username_layout.setText(USERNAME)

        Log.d("UserDataDebug", "${ID}, ${USERNAME}, ${PHONE}, ${EMAIL}")

        observeDataFilmChanges()
        getAlldataFilm(fragmentHome)

        // Inflate the layout for this fragment
        return fragmentHome
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment homeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                homeFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
    private fun getAlldataFilm(fragmentHome: View) {
        observedataFilms(fragmentHome)
    }

    private fun observedataFilms(fragmentHome: View) {
        dataFilmListLiveData.observe(viewLifecycleOwner) { datafilm ->
            val rv = fragmentHome.findViewById<RecyclerView>(R.id.rv_recycle_view_home)
            if (datafilm.isNotEmpty()) { // Periksa apakah daftar catatan tidak kosong
                Log.d(ContentValues.TAG, "observedataFilms: ${datafilm.size}")
                rv.isVisible = true

                val recyclerAdapter = dataFilmAdapter(datafilm)
                rv.setHasFixedSize(true)
                rv.layoutManager = GridLayoutManager(activity, 2)
                rv.adapter = recyclerAdapter

            }else{
                rv.isVisible = false
            }
        }
    }

    private fun observeDataFilmChanges() {
        dataFilmCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("MainActivity", "Data not found: ", error)
                return@addSnapshotListener
            }
            val datafilm = snapshots?.toObjects(dataFilm::class.java)
            if (datafilm != null) {
                dataFilmListLiveData.postValue(datafilm)
            }
        }
    }
}