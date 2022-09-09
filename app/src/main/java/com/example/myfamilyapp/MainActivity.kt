package com.example.myfamilyapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.myfamilyapp.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.item_invite_mail.*


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


        if(isAllPermissionGranted()){
            if(isLocationEnabled(this)){
                setupLocationListener()
            }else{
                showGPSNotEnabledDialog(this)
            }
        }else{
            askForPermission()
        }


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

    private fun setupLocationListener() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {
                        Log.d("Location89", "onLocationResult: ${location.latitude}")
                        Log.d("Location89", "onLocationResult: ${location.longitude}")

                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val mail = currentUser?.email.toString()

                        // Access a Cloud Firestore instance from your Activity
                        val db = Firebase.firestore
                        // Create a new user with a first and last name
                        val locationData = mutableMapOf<String,Any>(
                            "lat" to location.latitude.toString(),
                            "long" to location.longitude.toString()
                        )

                        //add custom document with custom id
                        db.collection("users").document(mail).update(locationData)
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener {
                            }
                    }
                }
            },
            Looper.myLooper()
        )
    }

    /**
     * Function to check if location of the device is enabled or not
     */
    fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Function to show the "enable GPS" Dialog box
     */
    fun showGPSNotEnabledDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Enable_GPS")
            .setMessage("required_for_this_app")
            .setCancelable(false)
            .setPositiveButton("enable_now") { _, _ ->
                context.startActivity(Intent(ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .show()
    }

    /**
     * Function to check if are granted or not
     */
    fun isAllPermissionGranted(): Boolean {
        for(item in permissions){
            if( ContextCompat
                .checkSelfPermission(
                    this,
                    item
                ) != PackageManager.PERMISSION_GRANTED){
                return false
            }
        }
        return true
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
                setupLocationListener()
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