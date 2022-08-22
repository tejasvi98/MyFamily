package com.example.myfamily

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val bottomBar = findViewById<BottomNavigationView>(R.id.bottom_bar)

        bottomBar.setOnItemSelectedListener {

            if(it.itemId == R.id.itGuard){
                inflateFragment(GuardFragment())
            }
            else if(it.itemId == R.id.itHome){
                inflateFragment(HomeFragment())
            }
            else if(it.itemId == R.id.itDashboard){
                inflateFragment(DashboardFragment())
            }
            else if(it.itemId == R.id.itProfile){
                inflateFragment(ProfileFragment())
            }

            true
        }
    }

    private fun inflateFragment(newInstance : Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,newInstance)
        transaction.commit()
    }

}