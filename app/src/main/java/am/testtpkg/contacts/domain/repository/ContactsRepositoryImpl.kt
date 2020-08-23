package am.testtpkg.contacts.domain.repository

import am.testtpkg.contacts.common.ContactsHelper
import am.testtpkg.contacts.domain.model.Contact
import am.testtpkg.contacts.domain.model.Email
import am.testtpkg.contacts.domain.model.PhoneNumber

class ContactsRepositoryImpl(private val contactsHelper: ContactsHelper) : IContactsRepository {

    override suspend fun loadContacts() = contactsHelper.getDeviceContacts()

    override suspend fun addNewContact(contact: Contact) = contactsHelper.insertContact(contact)

    override suspend fun editContactName(contact: Contact, newName: String) =
        contactsHelper.editContactName(contact, newName)

    override suspend fun addContactPhoneNumber(contact: Contact, phoneNumber: String) =
        contactsHelper.addContactPhoneNumber(contact, phoneNumber)

    override suspend fun editContactPhoneNumber(contact: Contact, oldPhoneNumber:PhoneNumber, newNumber: String) =
        contactsHelper.editContactPhoneNumber(contact, oldPhoneNumber,newNumber)

    override suspend fun deleteContactPhoneNumber(contact: Contact, phoneNumber: PhoneNumber) =
        contactsHelper.deleteContactPhoneNumber(contact, phoneNumber)

    override suspend fun addContactEmail(contact: Contact, email: String) =
        contactsHelper.addContactEmail(contact, email)

    override suspend fun editContactEmail(contact: Contact, oldEmail: Email, newEmail: String) =
        contactsHelper.editContactEmail(contact, oldEmail,newEmail)

    override suspend fun deleteContactEmail(contact: Contact, email: Email) =
        contactsHelper.deleteContactEmail(contact, email)

}