package com.example.myfamilyapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfamilyapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    lateinit var inviteAdapter : InviteAdapter

    private val listContacts : ArrayList<ContactModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment


        //using viewbinder
        binding = FragmentHomeBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        Log.d("FetchContacts89", "onViewCreated: ")

        val listMembers = listOf<MemberModel>(
            MemberModel(
                "Tejasvi",
                "abdde",
                "100%",
                "1000") ,
            MemberModel(
                "Ojaswi",
                "sdjfdsfadsf",
                "90%",
                "900") ,
            MemberModel(
                "Yatin",
                "kjdsfbsd",
                "80%",
                "800") ,
            MemberModel(
                "Jyoti",
                "djfdsafdf",
                "70%",
                "700") ,
            MemberModel(
                "Vinod",
                "dsgagfdsfas",
                "60%",
                "600") ,
            MemberModel(
                "Nirmal",
                "dsgadsfsdfa",
                "50%",
                "500")
        )

        val adapter = MemberAdapter(listMembers)

//        val recycler = requireView().findViewById<RecyclerView>(R.id.rv_members)
//        recycler.layoutManager = LinearLayoutManager(requireContext())
//        recycler.adapter = adapter

        binding.rvMembers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMembers.adapter = adapter



//        Log.d("FetchContacts89", "FetchContacts start karne wale hai")

//        Log.d("FetchContacts89", "FetchContacts start ho gaya hai ${listContacts.size}")
        inviteAdapter = InviteAdapter(listContacts)
        fetchDatabaseContacts()
//        Log.d("FetchContacts89", "FetchContacts end ho gaya hai")

        CoroutineScope(Dispatchers.IO).launch {
//            Log.d("FetchContacts89", "FetchContacts Coroutines start")

            insertDatabaseContacts(fetchContacts())

//            Log.d("FetchContacts89", "FetchContacts Coroutines end ${listContacts.size}")
        }



//        val inviteRecycler = requireView().findViewById<RecyclerView>(R.id.rv_invite)
//        inviteRecycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
//        inviteRecycler.adapter = inviteAdapter

        binding.rvInvite.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.rvInvite.adapter = inviteAdapter

        val threeDots = requireView().findViewById<ImageView>(R.id.ivMenu)
        threeDots.setOnClickListener {

            SharedPref.putBoolean(PrefConstants.IS_USER_LOGGED_IN,false)
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(),LoginActivity::class.java))
        }

    }

    private fun fetchDatabaseContacts(){
        val database = MyFamilyDatabase.getDatabase(requireContext())
        database.contactDao().getAllContacts().observe(viewLifecycleOwner){

//            Log.d("FetchContacts89", "fetchDatabaseContacts: ")
            
            listContacts.clear()
            listContacts.addAll(it)

            inviteAdapter.notifyDataSetChanged()
        }
    }

    private suspend fun insertDatabaseContacts(listContacts: ArrayList<ContactModel>) {
        val database = MyFamilyDatabase.getDatabase(requireContext())
        database.contactDao().insertAllContacts(listContacts)
    }

    @SuppressLint("Range")
    private fun fetchContacts(): ArrayList<ContactModel> {

//        Log.d("FetchContacts89", "start")

        val cr = requireActivity().contentResolver
        val cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null)

        val listContacts : ArrayList<ContactModel> = ArrayList()

        if(cursor != null && cursor.count > 0){

            while(cursor != null && cursor.moveToNext()){

                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                if(hasPhoneNumber > 0){
                    val pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",
                    arrayOf(id),
                    "")

                    if(pCur != null && pCur.count > 0){
                        while (pCur != null && pCur.moveToNext()){
                            val phoneNum = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                            listContacts.add(ContactModel(name,phoneNum))
                        }
                        pCur.close()
                    }
                }
            }

            if(cursor != null){
                cursor.close()
            }

        }
//        Log.d("FetchContacts89", "end")
        return listContacts
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) = HomeFragment()
    }
}