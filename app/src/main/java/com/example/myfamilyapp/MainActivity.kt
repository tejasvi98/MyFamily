package com.example.myfamilyapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.myfamilyapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


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

        val currentUser = FirebaseAuth.getInstance().currentUser
        val name = currentUser?.displayName.toString()
        val mail = currentUser?.email.toString()
        val phoneNumber = currentUser?.phoneNumber.toString()
        val imageUrl = currentUser?.photoUrl.toString()

        // Access a Cloud Firestore instance from your Activity
        val db = Firebase.firestore
        // Create a new user with a first and last name
        val user = hashMapOf(
            "name" to name,
            "mail" to mail,
            "phone_number" to phoneNumber,
            "imageUrl" to imageUrl
        )

        //add custom document with custom id
        db.collection("users").document(mail).set(user)
            .addOnSuccessListener {
                Log.d("Firestore89", "DocumentSnapshot added with ID: $mail")
            }
            .addOnFailureListener {
                Log.w("Firestore89", "Error adding document", it)
            }

          // Add a new document with a generated ID
//        db.collection("users")
//            .add(user)
//            .addOnSuccessListener {
//                Log.d("Firestore89", "DocumentSnapshot added with ID: ${it.id}")
//            }
//            .addOnFailureListener {
//                Log.w("Firestore89", "Error adding document", it)
//            }

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