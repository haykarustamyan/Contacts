package am.testtpkg.contacts.ui.contacts

import am.testtpkg.contacts.R
import am.testtpkg.contacts.domain.model.Contact
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_contact_list.view.*


class ContactsAdapter : ListAdapter<Contact, ContactsAdapter.ContactViewHolder>(DiffCallback) {

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_contact_list, parent, false)
        return ContactViewHolder(view)

    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun notifyNewContactAdded(contact: Contact) {
        val itemsListSubmit = currentList.toMutableList().apply {
            add(contact)
            sortBy { it.firstName }
        }

        submitList(itemsListSubmit)
    }

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private var item: Contact? = null

        private var initials: TextView = itemView.contactPromptTv
        private var fullName: TextView = itemView.fullNameTv
        private var phoneNumbers: TextView = itemView.phoneNumbersTv

        override fun onClick(v: View) {
            onItemClickListener?.onItemClick(v, item!!, adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(contact: Contact) {
            this.item = contact
            initials.text = contact.fullName.substring(0, 1)
            fullName.text = contact.fullName
            phoneNumbers.text = contact.multiLinedPhoneNumbers
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Contact>() {

        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem === newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.id == newItem.id
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, contact: Contact, position: Int)
    }

}
