package com.example.myfamily

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {

    lateinit var inviteAdapter : InviteAdapter

    private val listContacts : ArrayList<ContactModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
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

        val recycler = requireView().findViewById<RecyclerView>(R.id.rv_members)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter



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



        val inviteRecycler = requireView().findViewById<RecyclerView>(R.id.rv_invite)
        inviteRecycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        inviteRecycler.adapter = inviteAdapter

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