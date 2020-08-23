package am.testtpkg.contacts.domain.repository

import am.testtpkg.contacts.domain.model.Contact
import am.testtpkg.contacts.domain.model.Email
import am.testtpkg.contacts.domain.model.PhoneNumber

interface IContactsRepository {
    suspend fun loadContacts(): List<Contact>
    suspend fun addNewContact(contact: Contact): Contact
    suspend fun editContactName(contact: Contact, newName: String): Contact
    suspend fun addContactPhoneNumber(contact: Contact, phoneNumber: String): PhoneNumber
    suspend fun editContactPhoneNumber(contact: Contact, oldPhoneNumber: PhoneNumber, newNumber: String): PhoneNumber
    suspend fun deleteContactPhoneNumber(contact: Contact, phoneNumber: PhoneNumber): Boolean
    suspend fun addContactEmail(contact: Contact, email: String): Email
    suspend fun editContactEmail(contact: Contact, oldEmail: Email, newEmail: String): Email
    suspend fun deleteContactEmail(contact: Contact, email: Email): Boolean
}