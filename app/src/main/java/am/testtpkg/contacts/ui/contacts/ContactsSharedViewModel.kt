package am.testtpkg.contacts.ui.contacts

import am.testtpkg.contacts.domain.model.Contact
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContactsSharedViewModel : ViewModel() {

    private val _selectedContact = MutableLiveData<Contact>()
    val selectedContact: LiveData<Contact>
        get() = _selectedContact

    fun selectContact(contact: Contact) {
        _selectedContact.value = contact
    }
}