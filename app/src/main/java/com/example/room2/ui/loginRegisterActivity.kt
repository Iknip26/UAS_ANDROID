package com.example.room2.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.room2.Admin.adminHomeActivity
import com.example.room2.R
import com.example.room2.sharedPreferences.prefData
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab

class loginRegisterActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: loginRegisterAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)
        tabLayout = findViewById(R.id.tl_navbar_logregis)
        viewPager2 = findViewById(R.id.vp_container_logregis)

        adapter = loginRegisterAdapter(supportFragmentManager, lifecycle)

        tabLayout.addTab(tabLayout.newTab().setText("Login"))
        tabLayout.addTab(tabLayout.newTab().setText("Register"))

        viewPager2.adapter = adapter

        tabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: Tab?) {
                if (tab != null) {
                    viewPager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: Tab?) {

            }

            override fun onTabReselected(tab: Tab?) {

            }

        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

    }

    override fun onStart() {
        super.onStart()
        val sharedPref = getSharedPreferences(prefData.SHAREDPREF, Context.MODE_PRIVATE)
        if(sharedPref.getBoolean(prefData.IS_LOGIN, false)){
            startActivity(Intent(this@loginRegisterActivity, adminHomeActivity::class.java))
            finish()
        }else{
            if(sharedPref.getString(""))
        }
    }
}