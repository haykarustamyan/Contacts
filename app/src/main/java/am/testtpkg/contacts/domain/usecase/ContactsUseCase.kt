package am.testtpkg.contacts.domain.usecase

import am.testtpkg.contacts.domain.model.Contact
import am.testtpkg.contacts.domain.model.Email
import am.testtpkg.contacts.domain.model.PhoneNumber
import am.testtpkg.contacts.domain.repository.IContactsRepository

class ContactsUseCase(private val contactsRepository: IContactsRepository) {
    suspend fun loadContacts() = contactsRepository.loadContacts()
    suspend fun addNewContact(contact: Contact): Contact = contactsRepository.addNewContact(contact)
    suspend fun editContactName(contact: Contact, newName: String) = contactsRepository.editContactName(contact, newName)
    suspend fun addContactPhoneNumber(contact: Contact, phoneNumber: String) = contactsRepository.addContactPhoneNumber(contact, phoneNumber)
    suspend fun editContactPhoneNumber(contact: Contact, oldPhoneNumber: PhoneNumber, newNumber: String) = contactsRepository.editContactPhoneNumber(contact, oldPhoneNumber, newNumber)
    suspend fun deleteContactPhoneNumber(contact: Contact, phoneNumber: PhoneNumber) = contactsRepository.deleteContactPhoneNumber(contact, phoneNumber)
    suspend fun addContactEmail(contact: Contact, email: String) = contactsRepository.addContactEmail(contact, email)
    suspend fun editContactEmail(contact: Contact, oldEmail: Email, newEmail: String) = contactsRepository.editContactEmail(contact, oldEmail, newEmail)
    suspend fun deleteContactEmail(contact: Contact, email: Email) = contactsRepository.deleteContactEmail(contact, email)
}