package com.example.myfamily

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MemberAdapter(private val listMembers: List<MemberModel>) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val item = inflater.inflate(R.layout.item_member,parent,false)

        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = listMembers[position]
        holder.userName.text = item.name
        holder.address.text = item.address
        holder.batteryPercentage.text = item.batteryPercentage
        holder.distance.text = item.distance

    }

    override fun getItemCount(): Int {
        return listMembers.size
    }

    class ViewHolder(val item : View) : RecyclerView.ViewHolder(item) {
        val imageUser = item.findViewById<ImageView>(R.id.iv_img_user)
        val userName = item.findViewById<TextView>(R.id.tv_user_name)
        val address = item.findViewById<TextView>(R.id.tv_address)
        val batteryPercentage = item.findViewById<TextView>(R.id.tv_battery)
        val distance = item.findViewById<TextView>(R.id.tv_distance)


    }

}