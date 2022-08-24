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

            when (it.itemId) {
                R.id.itGuard -> {
                    inflateFragment(GuardFragment())
                }
                R.id.itHome -> {
                    inflateFragment(HomeFragment())
                }
                R.id.itDashboard -> {
                    inflateFragment(MapsFragment())
                }
                R.id.itProfile -> {
                    inflateFragment(ProfileFragment())
                }
            }

            true
        }

        bottomBar.selectedItemId = R.id.itHome  // it makes home option in bottom navigation always selected
    }

    private fun inflateFragment(newInstance : Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,newInstance)
        transaction.commit()
    }

}