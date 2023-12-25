package com.example.room2.Public_page

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.room2.Admin.InputActivity
import com.example.room2.Admin.adminHomeActivity
import com.example.room2.R
import com.example.room2.data.dataFilm
import com.example.room2.swipeToDeleteCallback
import com.google.firebase.firestore.FirebaseFirestore



//INISIASI RECYCLE VIEW yang ada di PUBLICHOME
//DATA AKAN DI AMBIL DARI FIREBASE
//TAMPILAN RECYCLENYA BEDA DARI ADMIN


class dataFilmAdapter(val dataFilm: List<dataFilm>?): RecyclerView.Adapter<dataFilmAdapter.MyViewHolder>() {
    private val firestore = FirebaseFirestore.getInstance()
    private val dataFilmCollectionRef = firestore.collection("datafilm")
    private lateinit var binding: adminHomeActivity
    private var updateId = ""
    private val dataFilmListLiveData: MutableLiveData<List<dataFilm>> by lazy {
        MutableLiveData<List<dataFilm>>()
    }

    private fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tittle = view.findViewById<TextView>(R.id.txt_tittle_items)
        val image = view.findViewById<ImageView>(R.id.img_image_items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return MyViewHolder(view).listen{ data, _->
            intentDetail(parent, dataFilm!![data])
        }
    }

    override fun getItemCount(): Int {
        if (dataFilm != null) {
            return dataFilm.size
        }
        return 0
    }

    fun getDataAtPosition(position: Int): dataFilm? {
        return dataFilm?.get(position)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tittle.text = dataFilm?.get(position)?.tittle
        Glide.with(holder.itemView.context)
            .load(dataFilm?.get(position)?.image_url)
            .centerCrop()
            .into(holder.image)


//        holder.itemView.setOnClickListener {
//
//            val intentToUpdate = Intent(holder.itemView.context, filmDescription::class.java)
//            intentToUpdate.putExtra("ID", dataFilm?.get(position)?.id)
//            intentToUpdate.putExtra("JUDUL", dataFilm?.get(position)?.tittle)
//            intentToUpdate.putExtra("SUTRADARA", dataFilm?.get(position)?.director)
//            intentToUpdate.putExtra("DURASI", dataFilm?.get(position)?.duration)
//            intentToUpdate.putExtra("DESKRIPSI", dataFilm?.get(position)?.description)
//            intentToUpdate.putExtra("HARGA", dataFilm?.get(position)?.price)
//            intentToUpdate.putExtra("GENRE", dataFilm?.get(position)?.genre)
//            intentToUpdate.putExtra("IMAGE", dataFilm?.get(position)?.image_url)
//            intentToUpdate.putExtra("COMMAND", "UPDATE")
//            holder.itemView.context.startActivity(intentToUpdate)
//        }

        val swipeToDeleteCallback = object : swipeToDeleteCallback(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
            }
        }
    }

    private fun intentDetail(parrent: ViewGroup, item:dataFilm){
        val intentToDetail = Intent(parrent.context.applicationContext, filmDescription::class.java)

        intentToDetail.putExtra("ID", item.id)
        intentToDetail.putExtra("TITTLE", item.tittle)
        intentToDetail.putExtra("GENRE", item.genre)
        intentToDetail.putExtra("DIRECTOR", item.director)
        intentToDetail.putExtra("DURATION", item.duration)
        intentToDetail.putExtra("DESCRIPTION", item.description)
        intentToDetail.putExtra("IMAGE", item.image_url)

        Toast.makeText(parrent.context.applicationContext, "${item.tittle}, ${item.genre}, ${item.director}, ${item.duration}, ${item.image_url}", Toast.LENGTH_SHORT).show()

        parrent.context.startActivity(intentToDetail)
    }
}