package am.testtpkg.contacts.ui.contacts

import am.testtpkg.contacts.R
import am.testtpkg.contacts.common.Constants.LOG_TAG
import am.testtpkg.contacts.domain.model.Contact
import am.testtpkg.contacts.domain.model.Email
import am.testtpkg.contacts.domain.model.PhoneNumber
import am.testtpkg.contacts.ui.dialog.*
import am.testtpkg.contacts.ui.main.MainActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_contact_item.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContactItemFragment : Fragment(R.layout.fragment_contact_item),
    EditContactNameDialogOperationsListener, AddContactPhoneNumberDialogOperationsListener,
    EditContactPhoneNumberDialogOperationsListener,
    DeleteContactPhoneNumberDialogOperationsListener,
    AddContactEmailDialogOperationsListener, EditContactEmailDialogOperationsListener,
    DeleteContactEmailDialogOperationsListener {

    private val contactsViewModel by viewModel<ContactsViewModel>()
    private val contactsSharedViewModel by sharedViewModel<ContactsSharedViewModel>()
    private val contactItemAdapter by inject<ContactItemAdapter>()
    private var currentContact: Contact? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        initViews()
        setListeners()
    }

    private fun setUpToolbar() = requireActivity().run {
        (this as MainActivity).setUpToolBar()
    }

    private fun initViews() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
            isNestedScrollingEnabled = false
            adapter = contactItemAdapter
        }
    }

    private fun setListeners() {
        fullNameTv.setOnClickListener {
            currentContact?.let {
                contactsViewModel.showDialog(
                    DialogType.EDIT_CONTACT_NAME_DIALOG,
                    arrayOf(it)
                )
            }
        }

        contactItemAdapter.setOnContactItemClickListener(object :
            ContactItemAdapter.OnContactItemClickListener {
            override fun onContactItemAddPhoneNumberClick() {
                currentContact?.let {
                    contactsViewModel.showDialog(
                        DialogType.ADD_CONTACT_PHONE_NUMBER_DIALOG,
                        arrayOf(it)
                    )
                }
            }

            override fun onContactItemAddEmailClick() {
                currentContact?.let {
                    contactsViewModel.showDialog(
                        DialogType.ADD_CONTACT_EMAIL_DIALOG,
                        arrayOf(it)
                    )
                }
            }
        })

        contactItemAdapter.setOnPhoneNumberItemClickListener(object :
            ContactItemAdapter.OnPhoneNumberItemClickListener {
            override fun onPhoneItemActionCallClick(
                view: ContactItemAdapter.PhoneViewHolder,
                phoneNumber: PhoneNumber,
                position: Int
            ) {
                makeCall(phoneNumber)
            }

            override fun onPhoneItemActionSmsClick(
                view: ContactItemAdapter.PhoneViewHolder,
                phoneNumber: PhoneNumber,
                position: Int
            ) {
                sendSms(phoneNumber)
            }

            override fun onPhoneItemActionEditClick(
                view: ContactItemAdapter.PhoneViewHolder,
                phoneNumber: PhoneNumber,
                position: Int
            ) {
                currentContact?.let {
                    contactsViewModel.showDialog(
                        DialogType.EDIT_CONTACT_PHONE_NUMBER_DIALOG,
                        arrayOf(it, phoneNumber, position)
                    )
                }
            }

            override fun onPhoneItemActionDeleteClick(
                view: ContactItemAdapter.PhoneViewHolder,
                phoneNumber: PhoneNumber,
                position: Int
            ) {
                currentContact?.let {
                    contactsViewModel.showDialog(
                        DialogType.DELETE_CONTACT_PHONE_NUMBER_DIALOG,
                        arrayOf(it, phoneNumber, position)
                    )
                }
            }
        })

        contactItemAdapter.setOnEmailItemClickListener(object :
            ContactItemAdapter.OnEmailItemClickListener {
            override fun onEmailItemActionWriteEmailClick(
                view: ContactItemAdapter.EmailViewHolder,
                email: Email,
                position: Int
            ) {
                writeEmail(email)
            }

            override fun onEmailItemActionEditClick(
                view: ContactItemAdapter.EmailViewHolder,
                email: Email,
                position: Int
            ) {
                currentContact?.let {
                    contactsViewModel.showDialog(
                        DialogType.EDIT_CONTACT_EMAIL_DIALOG,
                        arrayOf(it, email, position)
                    )
                }
            }

            override fun onEmailItemActionDeleteClick(
                view: ContactItemAdapter.EmailViewHolder,
                email: Email,
                position: Int
            ) {
                currentContact?.let {
                    contactsViewModel.showDialog(
                        DialogType.DELETE_CONTACT_EMAIL_DIALOG,
                        arrayOf(it, email, position)
                    )
                }
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeContact()
        observeEvents()
        observeData()
    }

    private fun observeContact() {
        contactsSharedViewModel.selectedContact.observe(viewLifecycleOwner, Observer {
            initContactItem(it)
        })
    }

    private fun observeEvents() {
        contactsViewModel.showDialogLiveData.observe(
            viewLifecycleOwner,
            Observer {
                when (it.first) {
                    DialogType.EDIT_CONTACT_NAME_DIALOG -> {
                        if (it.second[0] is Contact) {
                            showEditContactNameDialog(it.second[0] as Contact)
                        }
                    }
                    DialogType.ADD_CONTACT_PHONE_NUMBER_DIALOG -> {
                        showAddContactPhoneNumberDialog()
                    }
                    DialogType.EDIT_CONTACT_PHONE_NUMBER_DIALOG -> {
                        showEditContactPhoneNumberDialog(
                            it.second[1] as PhoneNumber,
                            it.second[2] as Int
                        )
                    }
                    DialogType.DELETE_CONTACT_PHONE_NUMBER_DIALOG -> {
                        showDeleteContactPhoneNumberDialog(
                            it.second[1] as PhoneNumber,
                            it.second[2] as Int
                        )
                    }
                    DialogType.ADD_CONTACT_EMAIL_DIALOG -> {
                        showAddContactEmailDialog()
                    }
                    DialogType.EDIT_CONTACT_EMAIL_DIALOG -> {
                        showEditContactEmailDialog(it.second[1] as Email, it.second[2] as Int)
                    }

                    DialogType.DELETE_CONTACT_EMAIL_DIALOG -> {
                        showDeleteContactEmailDialog(
                            it.second[1] as Email,
                            it.second[2] as Int
                        )
                    }
                }
            })
    }

    private fun observeData() {
        contactsViewModel.notifyContactNameEditedLiveData.observe(viewLifecycleOwner, Observer {
            fullNameTv.text = it.fullName
            contactPromptTv.text = it.fullName.substring(0, 1)
        })

        contactsViewModel.notifyContactPhoneNumberAddedLiveData.observe(
            viewLifecycleOwner,
            Observer {
                if (it.value.isNotEmpty()) {
                    contactItemAdapter.notifyContactItemPhoneNumberAdded(it)
                }
            })

        contactsViewModel.notifyContactPhoneNumberEditedLiveData.observe(
            viewLifecycleOwner,
            Observer {
                if (it.second.value.isNotEmpty()) {
                    contactItemAdapter.notifyContactItemPhoneNumberEdited(it.first, it.second)
                }
            })

        contactsViewModel.notifyContactPhoneNumberDeletedLiveData.observe(
            viewLifecycleOwner,
            Observer {
                contactItemAdapter.notifyContactItemPhoneNumberDeleted(it)
            })

        contactsViewModel.notifyContactEmailAddedLiveData.observe(
            viewLifecycleOwner,
            Observer {
                contactItemAdapter.notifyContactItemEmailAdded(it)
            })

        contactsViewModel.notifyContactEmailEditedLiveData.observe(
            viewLifecycleOwner,
            Observer {
                contactItemAdapter.notifyContactItemEmailEdited(it.first, it.second)
            })

        contactsViewModel.notifyContactEmailDeletedLiveData.observe(
            viewLifecycleOwner,
            Observer {
                contactItemAdapter.notifyContactItemEmailDeleted(it)
            })

    }

    private fun initContactItem(contact: Contact) {
        this.currentContact = contact
        contactPromptTv.text = contact.fullName.substring(0, 1)
        fullNameTv.text = contact.fullName

        val phoneNumbersList = contact.phoneNumbers
        val emailsList = contact.emails
        val itemsList = mutableListOf<Any>()

        itemsList.add("Phone numbers")
        if (phoneNumbersList.isNotEmpty()) {
            itemsList.addAll(phoneNumbersList)
        }
        itemsList.add("Emails")
        emailsList?.let {
            if (it.isNotEmpty()) {
                itemsList.addAll(it)
            }
        }

        contactItemAdapter.submitList(itemsList)
    }


    private fun makeCall(phoneNumber: PhoneNumber) {
        val uri = "tel:" + phoneNumber.value
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse(uri)
        startActivity(intent)
    }

    private fun sendSms(phoneNumber: PhoneNumber) {
        val smsNumber = "smsto:" + phoneNumber.value
        val sms = ""
        val smsIntent = Intent(Intent.ACTION_SENDTO)
        smsIntent.data = Uri.parse(smsNumber)
        smsIntent.putExtra("sms_body", sms)

        if (smsIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(smsIntent)
        } else {
            Log.e(LOG_TAG, "Can't resolve app for ACTION_SENDTO Intent.")
        }
    }

    private fun writeEmail(email: Email) {
        val to = email.value
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(to ?: ""))
        intent.type = "message/rfc822"
        startActivity(
            Intent.createChooser(
                intent,
                "Choose an Email client :"
            )
        )
    }


    private fun showEditContactNameDialog(contact: Contact) {
        EditContactNameDialog.show(this, fullNameTv.text.toString())
    }

    private fun showAddContactPhoneNumberDialog() {
        AddContactPhoneNumberDialog.show(this)
    }

    private fun showEditContactPhoneNumberDialog(phoneNumber: PhoneNumber, position: Int) {
        EditContactPhoneNumberDialog.show(this, phoneNumber, position)
    }

    private fun showDeleteContactPhoneNumberDialog(phoneNumber: PhoneNumber, position: Int) {
        DeleteContactPhoneNumberDialog.show(this, phoneNumber, position)
    }

    private fun showAddContactEmailDialog() {
        AddContactEmailDialog.show(this)
    }

    private fun showEditContactEmailDialog(email: Email, position: Int) {
        EditContactEmailDialog.show(this, email, position)
    }

    private fun showDeleteContactEmailDialog(email: Email, position: Int) {
        DeleteContactEmailDialog.show(this, email, position)
    }


    override fun onEditContactNameDialogOkClick(dialog: EditContactNameDialog, newName: String) {
        dialog.dismiss()
        currentContact?.let {
            contactsViewModel.editContactName(it, newName)
        }
    }

    override fun onAddContactPhoneNumberDialogOkClick(
        dialog: AddContactPhoneNumberDialog,
        phoneNumber: String
    ) {
        dialog.dismiss()
        currentContact?.let { contactsViewModel.addContactPhoneNumber(it, phoneNumber) }
    }

    override fun onEditContactPhoneNumberDialogOkClick(
        dialog: EditContactPhoneNumberDialog,
        oldPhoneNumber: PhoneNumber,
        newNumber: String,
        position: Int
    ) {
        dialog.dismiss()
        currentContact?.let {
            contactsViewModel.editContactPhoneNumber(
                it,
                oldPhoneNumber,
                newNumber,
                position
            )
        }
    }

    override fun onDeleteContactPhoneNumberDialogDeleteClick(
        dialog: DeleteContactPhoneNumberDialog,
        phoneNumber: PhoneNumber,
        position: Int
    ) {
        dialog.dismiss()
        currentContact?.let { it ->
            contactsViewModel.deleteContactPhoneNumber(
                it,
                phoneNumber,
                position
            )
        }
    }

    override fun onAddContactEmailDialogOkClick(dialog: AddContactEmailDialog, email: String) {
        dialog.dismiss()
        currentContact?.let { contactsViewModel.addContactEmail(it, email) }
    }

    override fun onEditContactEmailDialogOkClick(
        dialog: EditContactEmailDialog,
        oldEmail: Email,
        newEmail: String,
        position: Int
    ) {
        dialog.dismiss()
        currentContact?.let {
            contactsViewModel.editContactEmail(
                it,
                oldEmail,
                newEmail,
                position
            )
        }
    }

    override fun onDeleteContactEmailDialogDeleteClick(
        dialog: DeleteContactEmailDialog,
        email: Email,
        position: Int
    ) {
        dialog.dismiss()
        currentContact?.let { it ->
            contactsViewModel.deleteContactEmail(
                it,
                email,
                position
            )
        }
    }

    override fun onDialogCancelClick(dialog: BaseDialog) {
        dialog.dismiss()
    }
}