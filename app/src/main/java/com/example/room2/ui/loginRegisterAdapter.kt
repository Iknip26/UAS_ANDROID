package com.example.room2.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class loginRegisterAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        // Mengembalikan instance fragment sesuai dengan posisi
        return when (position) {
            0 -> loginFragment()
            1 -> registerFragment()
            else -> loginFragment() // Pengembalian default atau sesuaikan dengan kebutuhan Anda
        }
    }

}