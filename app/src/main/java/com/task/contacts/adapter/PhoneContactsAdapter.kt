package com.task.contacts.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.task.contacts.databinding.ContactsLayoutsBinding
import com.task.contacts.model.ContactsModal
import com.task.contacts.view.activity.MainActivity

class PhoneContactsAdapter(mainActivity: MainActivity, private var contactsModalArrayList: ArrayList<ContactsModal>
) : RecyclerView.Adapter<PhoneContactsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(ContactsLayoutsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.name.text = contactsModalArrayList[position].name
        holder.binding.phone.text = contactsModalArrayList[position].number
    }

    override fun getItemCount(): Int {
       return contactsModalArrayList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filteredlist: ArrayList<ContactsModal>) {
        contactsModalArrayList = filteredlist
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: ContactsLayoutsBinding) : RecyclerView.ViewHolder(binding.root)

}