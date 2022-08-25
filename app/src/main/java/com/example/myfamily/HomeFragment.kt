package com.example.myfamily

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class HomeFragment : Fragment() {


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
//        recycler.setHasFixedSize(true)
        recycler.adapter = adapter

        val inviteAdapter = InviteAdapter(fetchContacts())

        val inviteRecycler = requireView().findViewById<RecyclerView>(R.id.rv_invite)
        inviteRecycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        inviteRecycler.adapter = inviteAdapter

    }

    @SuppressLint("Range")
    private fun fetchContacts(): ArrayList<ContactModel> {

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
        return listContacts
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) = HomeFragment()
    }
}