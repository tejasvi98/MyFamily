package com.example.myfamilyapp

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myfamilyapp.databinding.ItemInviteMailBinding

class InviteMailAdapter(private val listInvites : List<String>,private val onActionClick: OnActionClick) :
RecyclerView.Adapter<InviteMailAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val item = ItemInviteMailBinding.inflate(inflater,parent,false)
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listInvites[position]
        holder.name.text = item

        holder.accept.setOnClickListener {
            onActionClick.onAcceptClick(item)
        }

        holder.deny.setOnClickListener {
            onActionClick.onDenyClick(item)
        }
    }

    override fun getItemCount(): Int {
        Log.d("size89", "getItemCount: ${listInvites.size}")
        return listInvites.size
    }


    class ViewHolder(private val item : ItemInviteMailBinding) : RecyclerView.ViewHolder(item.root){
        val name = item.mail
        val accept = item.accept
        val deny = item.deny
    }

    interface OnActionClick{
        fun onAcceptClick(mail : String)
        fun onDenyClick(mail : String)
    }

}