package com.example.myfamilyapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.myfamilyapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val permissionCode = 78

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //using view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askForPermission()

//        val bottomBar = findViewById<BottomNavigationView>(R.id.bottom_bar)




        binding.bottomBar.setOnItemSelectedListener { it ->

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

        binding.bottomBar.selectedItemId = R.id.itHome  // it makes home option in bottom navigation always selected
    }

    private fun askForPermission() {
        ActivityCompat.requestPermissions(this,permissions,permissionCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == permissionCode){

            if(allPermissionGranted()){
                //openCamera()
            }else{
                Toast.makeText(this,"please grant location and camera permission",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun openCamera() {
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        startActivity(intent)
    }

    private fun allPermissionGranted(): Boolean {
        for(item in permissions){
            if(ContextCompat.checkSelfPermission(this,item) != PackageManager.PERMISSION_GRANTED){
                return false
            }
        }
        return true
    }

    private fun inflateFragment(newInstance : Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,newInstance)
        transaction.commit()
    }

}