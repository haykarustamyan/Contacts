package am.testtpkg.contacts.ui.contacts

import am.testtpkg.contacts.common.Constants.LOG_TAG
import am.testtpkg.contacts.common.Event
import am.testtpkg.contacts.domain.model.Contact
import am.testtpkg.contacts.domain.model.Email
import am.testtpkg.contacts.domain.model.PhoneNumber
import am.testtpkg.contacts.domain.usecase.ContactsUseCase
import am.testtpkg.contacts.ui.dialog.DialogType
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class ContactsViewModel(private val contactsUseCase: ContactsUseCase) : ViewModel() {

    private val _showAddDialogMutableLiveData = MutableLiveData<Event<String>>()
    private val _showDialogMutableLiveData = MutableLiveData<Pair<DialogType, Array<Any>>>()
    private val _notifyNewContactInsertedMutableLiveData = MutableLiveData<Event<Contact>>()
    private val _notifyContactNameEditedMutableLiveData = MutableLiveData<Contact>()
    private val _notifyContactPhoneNumberAddedMutableLiveData = MutableLiveData<PhoneNumber>()
    private val _notifyContactPhoneNumberEditedMutableLiveData = MutableLiveData<Pair<Int, PhoneNumber>>()
    private val _notifyContactPhoneNumberDeletedMutableLiveData = MutableLiveData<Int>()
    private val _notifyContactEmailAddedMutableLiveData = MutableLiveData<Email>()
    private val _notifyContactEmailEditedMutableLiveData = MutableLiveData<Pair<Int, Email>>()
    private val _notifyContactEmailDeletedMutableLiveData = MutableLiveData<Int>()


    val showAddDialogLiveData: LiveData<Event<String>>
        get() = _showAddDialogMutableLiveData

    val showDialogLiveData: LiveData<Pair<DialogType, Array<Any>>>
        get() = _showDialogMutableLiveData

    val notifyNewContactInsertedLiveData: LiveData<Event<Contact>>
        get() = _notifyNewContactInsertedMutableLiveData

    val notifyContactNameEditedLiveData: LiveData<Contact>
        get() = _notifyContactNameEditedMutableLiveData

    val notifyContactPhoneNumberAddedLiveData: LiveData<PhoneNumber>
        get() = _notifyContactPhoneNumberAddedMutableLiveData

    val notifyContactPhoneNumberEditedLiveData: LiveData<Pair<Int, PhoneNumber>>
        get() = _notifyContactPhoneNumberEditedMutableLiveData

    val notifyContactPhoneNumberDeletedLiveData: LiveData<Int>
        get() = _notifyContactPhoneNumberDeletedMutableLiveData

    val notifyContactEmailAddedLiveData: LiveData<Email>
        get() = _notifyContactEmailAddedMutableLiveData

    val notifyContactEmailEditedLiveData: LiveData<Pair<Int, Email>>
        get() = _notifyContactEmailEditedMutableLiveData

    val notifyContactEmailDeletedLiveData: LiveData<Int>
        get() = _notifyContactEmailDeletedMutableLiveData


    fun showDialog(type: DialogType, data: Array<Any>) {
        _showDialogMutableLiveData.postValue(type to data)
    }


    fun showAddDialog(itemId: String) {
        _showAddDialogMutableLiveData.value = Event(itemId)
    }

    private fun notifyNewContactInserted(newContact: Contact) {
        _notifyNewContactInsertedMutableLiveData.postValue(Event(newContact))
    }

    private fun notifyContactNameEdited(contact: Contact) {
        _notifyContactNameEditedMutableLiveData.postValue(contact)
    }

    private fun notifyContactPhoneNumberAdded(phoneNumber: PhoneNumber) {
        _notifyContactPhoneNumberAddedMutableLiveData.postValue(phoneNumber)
    }

    private fun notifyContactPhoneNumberEdited(phoneNumber: Pair<Int, PhoneNumber>) {
        _notifyContactPhoneNumberEditedMutableLiveData.postValue(phoneNumber)
    }

    private fun notifyContactPhoneNumberDeleted(position: Int) {
        _notifyContactPhoneNumberDeletedMutableLiveData.postValue(position)
    }

    private fun notifyContactEmailAdded(email: Email) {
        _notifyContactEmailAddedMutableLiveData.postValue(email)
    }

    private fun notifyContactEmailEdited(pair: Pair<Int, Email>) {
        _notifyContactEmailEditedMutableLiveData.postValue(pair)
    }

    private fun notifyContactEmailDeleted(position: Int) {
        _notifyContactEmailDeletedMutableLiveData.postValue(position)
    }


    fun loadContacts() = liveData {
        runCatching {
            emit(contactsUseCase.loadContacts())
        }.getOrElse {
            Log.d(LOG_TAG, "Failed to load contacts: ${it.message}")
            emit(emptyList<Contact>())
        }
    }

    fun addNewContact(contact: Contact) {
        viewModelScope.launch {
            val newContact = contactsUseCase.addNewContact(contact)
            notifyNewContactInserted(newContact)
        }
    }

    fun editContactName(contact: Contact, newName: String) {
        viewModelScope.launch {
            val editedContact = contactsUseCase.editContactName(contact, newName)
            notifyContactNameEdited(editedContact)
        }
    }

    fun addContactPhoneNumber(contact: Contact, phoneNumber: String) {
        viewModelScope.launch {
            val newPhoneNumber = contactsUseCase.addContactPhoneNumber(contact, phoneNumber)
            notifyContactPhoneNumberAdded(newPhoneNumber)
        }
    }

    fun editContactPhoneNumber(
        contact: Contact, oldPhoneNumber: PhoneNumber,
        newNumber: String, position: Int
    ) {
        viewModelScope.launch {
            val editedPhoneNumber =
                contactsUseCase.editContactPhoneNumber(contact, oldPhoneNumber, newNumber)
            notifyContactPhoneNumberEdited(position to editedPhoneNumber)
        }
    }

    fun deleteContactPhoneNumber(contact: Contact, phoneNumber: PhoneNumber, position: Int) {
        viewModelScope.launch {
            val isDeleted = contactsUseCase.deleteContactPhoneNumber(contact, phoneNumber)
            if (isDeleted) notifyContactPhoneNumberDeleted(position)
        }
    }

    fun addContactEmail(contact: Contact, email: String) {
        viewModelScope.launch {
            val newEmail = contactsUseCase.addContactEmail(contact, email)
            notifyContactEmailAdded(newEmail)
        }
    }

    fun editContactEmail(contact: Contact, oldEmail: Email, newEmail: String, position: Int) {
        viewModelScope.launch {
            val editedEmail = contactsUseCase.editContactEmail(contact, oldEmail, newEmail)
            notifyContactEmailEdited(position to editedEmail)
        }
    }

    fun deleteContactEmail(contact: Contact, email: Email, position: Int) {
        viewModelScope.launch {
            val isDeleted = contactsUseCase.deleteContactEmail(contact, email)
            if (isDeleted) notifyContactEmailDeleted(position)
        }
    }

}