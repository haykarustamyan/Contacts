package am.testtpkg.contacts.ui.contacts

import am.testtpkg.contacts.R
import am.testtpkg.contacts.domain.model.Email
import am.testtpkg.contacts.domain.model.PhoneNumber
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_email.view.*
import kotlinx.android.synthetic.main.item_group_label.view.*
import kotlinx.android.synthetic.main.item_phone_number.view.*

class ContactItemAdapter : ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback) {

    private var onContactItemClickListener: OnContactItemClickListener? = null
    private var onPhoneNumberItemClickListener: OnPhoneNumberItemClickListener? = null
    private var onEmailItemClickListener: OnEmailItemClickListener? = null

    fun setOnContactItemClickListener(contactItemClickListener: OnContactItemClickListener?) {
        this.onContactItemClickListener = contactItemClickListener
    }

    fun setOnPhoneNumberItemClickListener(phoneNumberItemClickListener: OnPhoneNumberItemClickListener?) {
        this.onPhoneNumberItemClickListener = phoneNumberItemClickListener
    }

    fun setOnEmailItemClickListener(emailItemClickListener: OnEmailItemClickListener?) {
        this.onEmailItemClickListener = emailItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_PHONE -> PhoneViewHolder(
                inflater.inflate(R.layout.item_phone_number, parent, false)
            )
            TYPE_EMAIL -> EmailViewHolder(
                inflater.inflate(R.layout.item_email, parent, false)
            )
            else -> LabelViewHolder(
                inflater.inflate(R.layout.item_group_label, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder.itemViewType) {
            TYPE_PHONE -> (holder as PhoneViewHolder).bind(item as PhoneNumber)
            TYPE_EMAIL -> (holder as EmailViewHolder).bind(item as Email)
            TYPE_LABEL -> (holder as LabelViewHolder).bind(item as String)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PhoneNumber -> TYPE_PHONE
            is Email -> TYPE_EMAIL
            else -> TYPE_LABEL
        }
    }

    fun notifyContactItemPhoneNumberAdded(phoneNumber: PhoneNumber) {
        val itemsList = mutableListOf<Any>()

        val labelsList = currentList.filterIsInstance<String>()
        val phoneNumbersList = currentList.filterIsInstance<PhoneNumber>().toMutableList().apply {
            add(phoneNumber)
            sortBy { it.value }
        }
        val emailsList = currentList.filterIsInstance<Email>()


        itemsList.add(labelsList[0])
        itemsList.addAll(phoneNumbersList)
        itemsList.add(labelsList[1])
        itemsList.addAll(emailsList)

        submitList(itemsList)
    }

    fun notifyContactItemPhoneNumberEdited(position: Int, phoneNumber: PhoneNumber) {
        val itemsList = currentList.toMutableList()
        itemsList.forEachIndexed { index, _ ->
            if (index == position) {
                itemsList[index] = phoneNumber
                return@forEachIndexed
            }
        }
        notifyItemChanged(position)
    }

    fun notifyContactItemPhoneNumberDeleted(position: Int) {
        val itemsList = currentList.toMutableList().apply {
            removeAt(position)
        }

        val itemsListSubmit = mutableListOf<Any>()

        val labelsList = itemsList.filterIsInstance<String>()
        val phoneNumbersList = itemsList.filterIsInstance<PhoneNumber>().toMutableList()
        val emailsList = itemsList.filterIsInstance<Email>()

        itemsListSubmit.add(labelsList[0])
        itemsListSubmit.addAll(phoneNumbersList)
        itemsListSubmit.add(labelsList[1])
        itemsListSubmit.addAll(emailsList)

        submitList(itemsListSubmit)
    }

    fun notifyContactItemEmailAdded(email: Email) {
        val itemsList = mutableListOf<Any>()

        val labelsList = currentList.filterIsInstance<String>()
        val phoneNumbersList = currentList.filterIsInstance<PhoneNumber>()
        val emailsList = currentList.filterIsInstance<Email>().toMutableList().apply {
            add(email)
            sortBy { it.value }
        }


        itemsList.add(labelsList[0])
        itemsList.addAll(phoneNumbersList)
        itemsList.add(labelsList[1])
        itemsList.addAll(emailsList)

        submitList(itemsList)
    }

    fun notifyContactItemEmailEdited(position: Int, email: Email) {
        val itemsList = currentList.toMutableList()
        itemsList.forEachIndexed { index, _ ->
            if (index == position) {
                itemsList[index] = email
                return@forEachIndexed
            }
        }
        notifyItemChanged(position)
    }

    fun notifyContactItemEmailDeleted(position: Int) {
        val itemsList = currentList.toMutableList().apply {
            removeAt(position)
        }

        val itemsListSubmit = mutableListOf<Any>()

        val labelsList = itemsList.filterIsInstance<String>()
        val phoneNumbersList = itemsList.filterIsInstance<PhoneNumber>().toMutableList()
        val emailsList = itemsList.filterIsInstance<Email>()

        itemsListSubmit.add(labelsList[0])
        itemsListSubmit.addAll(phoneNumbersList)
        itemsListSubmit.add(labelsList[1])
        itemsListSubmit.addAll(emailsList)

        submitList(itemsListSubmit)
    }

    inner class LabelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var addNewBtn: ImageView = itemView.addNewItemIv
        private var labelTv: TextView = itemView.label

        init {
            addNewBtn.setOnClickListener {
                when (adapterPosition) {
                    0 -> {
                        onContactItemClickListener?.onContactItemAddPhoneNumberClick()
                    }
                    else -> {
                        onContactItemClickListener?.onContactItemAddEmailClick()
                    }
                }
            }
        }

        fun bind(label: String) {
            labelTv.text = label

        }
    }

    inner class PhoneViewHolder(view: View) : RecyclerView.ViewHolder(view),
        PopupMenu.OnMenuItemClickListener {
        private var item: PhoneNumber? = null

        private var phoneTv: TextView = itemView.phoneNumberTv

        init {
            itemView.setOnClickListener {
                val popup = PopupMenu(it.context, it)
                popup.setOnMenuItemClickListener(this)
                popup.inflate(R.menu.menu_phone_number)
                popup.show()
            }

            itemView.setOnLongClickListener {
                onPhoneNumberItemClickListener?.onPhoneItemActionDeleteClick(
                    this,
                    this.item!!,
                    adapterPosition
                )
                true
            }
        }

        fun bind(phoneNumber: PhoneNumber) {
            this.item = phoneNumber
            phoneTv.text = phoneNumber.value

        }

        override fun onMenuItemClick(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.action_call -> {
                    onPhoneNumberItemClickListener?.onPhoneItemActionCallClick(
                        this,
                        this.item!!,
                        adapterPosition
                    )
                }
                R.id.action_sms -> {
                    onPhoneNumberItemClickListener?.onPhoneItemActionSmsClick(
                        this,
                        this.item!!,
                        adapterPosition
                    )
                }
                R.id.action_edit -> {
                    onPhoneNumberItemClickListener?.onPhoneItemActionEditClick(
                        this,
                        this.item!!,
                        adapterPosition
                    )
                }
            }
            return true
        }
    }

    inner class EmailViewHolder(view: View) : RecyclerView.ViewHolder(view),
        PopupMenu.OnMenuItemClickListener {
        private var item: Email? = null

        private var emailTv: TextView = itemView.emailTv

        init {
            itemView.setOnClickListener {
                val popup = PopupMenu(it.context, it)
                popup.setOnMenuItemClickListener(this)
                popup.inflate(R.menu.menu_email)
                popup.show()
            }
            itemView.setOnLongClickListener {
                onEmailItemClickListener?.onEmailItemActionDeleteClick(
                    this,
                    this.item!!,
                    adapterPosition
                )
                true
            }
        }

        fun bind(email: Email) {
            this.item = email
            emailTv.text = email.value
        }

        override fun onMenuItemClick(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.action_email -> {
                    onEmailItemClickListener?.onEmailItemActionWriteEmailClick(
                        this,
                        this.item!!,
                        adapterPosition
                    )
                }
                R.id.action_edit -> {
                    onEmailItemClickListener?.onEmailItemActionEditClick(
                        this,
                        this.item!!,
                        adapterPosition
                    )
                }
            }
            return true
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Any>() {
        private const val TYPE_LABEL = 1
        private const val TYPE_PHONE = 2
        private const val TYPE_EMAIL = 3

        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem === newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            if (oldItem is PhoneNumber && newItem is PhoneNumber) return oldItem.value == newItem.value
            if (oldItem is Email && newItem is Email) return oldItem.value == newItem.value
            if (oldItem is String && newItem is String) return oldItem == newItem
            return false
        }
    }

    interface OnContactItemClickListener {
        fun onContactItemAddPhoneNumberClick()
        fun onContactItemAddEmailClick()
    }

    interface OnPhoneNumberItemClickListener {
        fun onPhoneItemActionCallClick(
            view: PhoneViewHolder,
            phoneNumber: PhoneNumber,
            position: Int
        )

        fun onPhoneItemActionSmsClick(
            view: PhoneViewHolder,
            phoneNumber: PhoneNumber,
            position: Int
        )

        fun onPhoneItemActionEditClick(
            view: PhoneViewHolder,
            phoneNumber: PhoneNumber,
            position: Int
        )

        fun onPhoneItemActionDeleteClick(
            view: PhoneViewHolder,
            phoneNumber: PhoneNumber,
            position: Int
        )
    }

    interface OnEmailItemClickListener {
        fun onEmailItemActionWriteEmailClick(view: EmailViewHolder, email: Email, position: Int)
        fun onEmailItemActionEditClick(view: EmailViewHolder, email: Email, position: Int)
        fun onEmailItemActionDeleteClick(view: EmailViewHolder, email: Email, position: Int)
    }

}
