package am.testtpkg.contacts.common

import am.testtpkg.contacts.common.Constants.LOG_TAG
import am.testtpkg.contacts.domain.model.Contact
import am.testtpkg.contacts.domain.model.ContactSource
import am.testtpkg.contacts.domain.model.Email
import am.testtpkg.contacts.domain.model.PhoneNumber
import android.accounts.Account
import android.accounts.AccountManager
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.*
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import android.util.Log
import android.util.SparseArray
import java.util.*
import kotlin.collections.ArrayList


class ContactsHelper(private val context: Context) {

    private var displayContactSources = ArrayList<String>()

    private val contactsProjection = arrayOf(
        Data.CONTACT_ID,
        Data.RAW_CONTACT_ID,
        CommonDataKinds.StructuredName.PREFIX,
        CommonDataKinds.StructuredName.GIVEN_NAME,
        CommonDataKinds.StructuredName.MIDDLE_NAME,
        CommonDataKinds.StructuredName.FAMILY_NAME,
        CommonDataKinds.StructuredName.SUFFIX,
        CommonDataKinds.StructuredName.PHOTO_URI,
        CommonDataKinds.StructuredName.PHOTO_THUMBNAIL_URI,
        CommonDataKinds.StructuredName.STARRED,
        RawContacts.ACCOUNT_NAME,
        RawContacts.ACCOUNT_TYPE
    )

    fun getDeviceContacts(): MutableList<Contact> {
        displayContactSources =
            getDeviceContactSources().map { it.name }.toMutableList() as ArrayList

        val devicePhoneNumbers = getPhoneNumbers()
        val emails = getEmails()

        val contactsArray = mutableListOf<Contact>()

        val uri = Data.CONTENT_URI
        val selection = "${Data.MIMETYPE} = ?"
        val selectionArgs = arrayOf(CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
        val sortOrder = null
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(
                uri,
                contactsProjection,
                selection,
                selectionArgs,
                sortOrder
            )
            if (cursor?.moveToFirst() == true) {
                loop@ do {
                    val id = cursor.getIntValue(Data.RAW_CONTACT_ID)
                    var contactNumbers = devicePhoneNumbers.get(id)
                    val firstName =
                        cursor.getStringValue(CommonDataKinds.StructuredName.GIVEN_NAME) ?: ""
                    val middleName =
                        cursor.getStringValue(CommonDataKinds.StructuredName.MIDDLE_NAME) ?: ""
                    val surname =
                        cursor.getStringValue(CommonDataKinds.StructuredName.FAMILY_NAME) ?: ""

                    if (contactNumbers == null) continue@loop
                    if ("$firstName $middleName $surname".isBlank()) continue@loop

                    val accountName =
                        cursor.getStringValue(RawContacts.ACCOUNT_NAME) ?: ""
                    val accountType =
                        cursor.getStringValue(RawContacts.ACCOUNT_TYPE) ?: ""
                    val contactId = cursor.getIntValue(Data.CONTACT_ID)

                    contactNumbers = contactNumbers ?: ArrayList()
                    val contactEmails = emails.get(id)

                    val contact = Contact(
                        id,
                        firstName,
                        surname,
                        middleName,
                        contactId,
                        contactNumbers,
                        accountName,
                        accountType,
                        contactEmails
                    )

                    contactsArray.add(contact)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message.toString())
        } finally {
            cursor?.close()
        }
        return contactsArray
    }

    private fun getPhoneNumbers(contactId: Int? = null): SparseArray<ArrayList<PhoneNumber>> {
        val phoneNumbers = SparseArray<ArrayList<PhoneNumber>>()
        val uri = CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            Data.RAW_CONTACT_ID,
            CommonDataKinds.Phone.NUMBER,
            CommonDataKinds.Phone.NORMALIZED_NUMBER,
            CommonDataKinds.Phone.TYPE,
            CommonDataKinds.Phone.LABEL
        )
        val selection =
            if (contactId == null) getSourcesSelection() else "${Data.RAW_CONTACT_ID} = ?"
        val selectionArgs =
            if (contactId == null) getSourcesSelectionArgs() else arrayOf(contactId.toString())
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor?.moveToFirst() == true) {
                do {
                    val id = cursor.getIntValue(Data.RAW_CONTACT_ID)
                    val number = cursor.getStringValue(CommonDataKinds.Phone.NUMBER) ?: continue
                    val normalizedNumber =
                        cursor.getStringValue(CommonDataKinds.Phone.NORMALIZED_NUMBER)
                            ?: PhoneNumberUtils.normalizeNumber(number)

                    val type = cursor.getIntValue(CommonDataKinds.Phone.TYPE)
                    val label = cursor.getStringValue(CommonDataKinds.Phone.LABEL) ?: ""

                    if (phoneNumbers[id] == null) {
                        phoneNumbers.put(id, ArrayList())
                    }

                    val phoneNumber = PhoneNumber(number, type, label, normalizedNumber)
                    phoneNumbers[id].add(phoneNumber)
                } while (cursor.moveToNext())
            }
        } catch (ignored: Exception) {
            Log.e(LOG_TAG, ignored.toString())
        } finally {
            cursor?.close()
        }
        return phoneNumbers
    }

    private fun getEmails(contactId: Int? = null): SparseArray<ArrayList<Email>> {
        val emails = SparseArray<ArrayList<Email>>()
        val uri = CommonDataKinds.Email.CONTENT_URI
        val projection = arrayOf(
            Data.RAW_CONTACT_ID,
            CommonDataKinds.Email.DATA,
            CommonDataKinds.Email.TYPE,
            CommonDataKinds.Email.LABEL
        )

        val selection =
            if (contactId == null) getSourcesSelection() else "${Data.RAW_CONTACT_ID} = ?"
        val selectionArgs =
            if (contactId == null) getSourcesSelectionArgs() else arrayOf(contactId.toString())

        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor?.moveToFirst() == true) {
                do {
                    val id = cursor.getIntValue(Data.RAW_CONTACT_ID)
                    val email = cursor.getStringValue(CommonDataKinds.Email.DATA) ?: continue
                    val type = cursor.getIntValue(CommonDataKinds.Email.TYPE)
                    val label = cursor.getStringValue(CommonDataKinds.Email.LABEL) ?: ""

                    if (emails[id] == null) {
                        emails.put(id, ArrayList())
                    }

                    emails[id]!!.add(Email(email, type, label))
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message.toString())
        } finally {
            cursor?.close()
        }

        return emails
    }

    private fun getQuestionMarks() =
        "?,".times(displayContactSources.filter { it.isNotEmpty() }.size).trimEnd(',')

    private fun getSourcesSelection(
        addMimeType: Boolean = false,
        addContactId: Boolean = false,
        useRawContactId: Boolean = true
    ): String {
        val strings = ArrayList<String>()
        if (addMimeType) {
            strings.add("${Data.MIMETYPE} = ?")
        }

        if (addContactId) {
            strings.add("${if (useRawContactId) Data.RAW_CONTACT_ID else Data.CONTACT_ID} = ?")
        } else {
            // sometimes local device storage has null account_name, handle it properly
            val accountnameString = StringBuilder()
            if (displayContactSources.contains("")) {
                accountnameString.append("(")
            }
            accountnameString.append("${RawContacts.ACCOUNT_NAME} IN (${getQuestionMarks()})")
            if (displayContactSources.contains("")) {
                accountnameString.append(" OR ${RawContacts.ACCOUNT_NAME} IS NULL)")
            }
            strings.add(accountnameString.toString())
        }

        return TextUtils.join(" AND ", strings)
    }

    private fun getSourcesSelectionArgs(
        mimetype: String? = null,
        contactId: Int? = null
    ): Array<String> {
        val args = ArrayList<String>()

        if (mimetype != null) {
            args.add(mimetype)
        }

        if (contactId != null) {
            args.add(contactId.toString())
        } else {
            args.addAll(displayContactSources.filter { it.isNotEmpty() })
        }
        return args.toTypedArray()
    }

    fun getDeviceContactSources(): List<ContactSource> {
        val sources = LinkedHashSet<ContactSource>()
        val accounts = AccountManager.get(context).accounts
        accounts.forEach {
            if (ContentResolver.getIsSyncable(it, AUTHORITY) == 1) {
                var publicName = it.name

                val contactSource = ContactSource(
                    it.name,
                    it.type
                )
                sources.add(contactSource)
            }
        }

        val contentResolverAccounts = getContentResolverAccounts().filter {
            it.name.isNotEmpty() && it.type.isNotEmpty() && !accounts.contains(
                Account(
                    it.name,
                    it.type
                )
            )
        }
        sources.addAll(contentResolverAccounts)
        return sources.toMutableList()
    }

    private fun getContentResolverAccounts(): HashSet<ContactSource> {
        val sources = HashSet<ContactSource>()
        arrayOf(
            Groups.CONTENT_URI, Settings.CONTENT_URI,
            RawContacts.CONTENT_URI
        ).forEach {
            fillSourcesFromUri(it, sources)
        }
        return sources
    }

    private fun fillSourcesFromUri(uri: Uri, sources: HashSet<ContactSource>) {
        val projection = arrayOf(
            RawContacts.ACCOUNT_NAME,
            RawContacts.ACCOUNT_TYPE
        )
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor?.moveToFirst() == true) {
                do {
                    val name =
                        cursor.getStringValue(RawContacts.ACCOUNT_NAME) ?: ""
                    val type =
                        cursor.getStringValue(RawContacts.ACCOUNT_TYPE) ?: ""

                    val source = ContactSource(
                        name,
                        type
                    )
                    sources.add(source)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
        } finally {
            cursor?.close()
        }
    }

    fun insertContact(contact: Contact): Contact {
        val operations: ArrayList<ContentProviderOperation> = ArrayList()

        val rawContactID: Int = operations.size

        operations.add(
            ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_TYPE, "")
                .withValue(RawContacts.ACCOUNT_NAME, "")
                .build()
        )

        operations.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                )
                .withValue(CommonDataKinds.StructuredName.DISPLAY_NAME, contact.firstName)
//            .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contact.firstName)
                .build()
        );

        contact.phoneNumbers.forEach {
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).apply {
                withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                withValue(
                    ContactsContract.Data.MIMETYPE,
                    CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                )
                withValue(CommonDataKinds.Phone.NUMBER, it.value)
                withValue(CommonDataKinds.Phone.NORMALIZED_NUMBER, it.normalizedNumber)
                withValue(CommonDataKinds.Phone.TYPE, it.type)
                withValue(CommonDataKinds.Phone.LABEL, it.label)
                operations.add(build())
            }
        }

        contact.emails?.forEach {
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).apply {
                withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                withValue(
                    ContactsContract.Data.MIMETYPE,
                    CommonDataKinds.Email.CONTENT_ITEM_TYPE
                )
                withValue(CommonDataKinds.Email.DATA, it.value)
                withValue(CommonDataKinds.Email.TYPE, it.type)
                withValue(CommonDataKinds.Email.LABEL, it.label)
                operations.add(build())
            }
        }

        try {
            context.contentResolver.applyBatch(AUTHORITY, operations)
            operations.clear()
            Log.d(LOG_TAG, "success")

            return contact
        } catch (e: Exception) {
            Log.d(LOG_TAG, "failed : ${e.message}")
            e.printStackTrace()
        }

        return contact
    }

    fun editContactName(contact: Contact, newName: String): Contact {
        val operations = ArrayList<ContentProviderOperation>()
        val selection = getSourcesSelection(true, contact.id != null)
        val selectionArgs = getSourcesSelectionArgs(
            CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
            contact.id
        )

        operations.add(
            ContentProviderOperation.newUpdate(Data.CONTENT_URI)
                .withSelection(selection, selectionArgs)
                .withValue(CommonDataKinds.StructuredName.GIVEN_NAME, newName)
                .build()
        )

        try {
            context.contentResolver.applyBatch(AUTHORITY, operations)
            operations.clear()
            Log.d(LOG_TAG, "update success")

            return contact.apply {
                firstName = newName
            }
        } catch (e: Exception) {
            Log.d(LOG_TAG, "update failed : ${e.message}")
            e.printStackTrace()
        }

        return contact
    }

    fun addContactPhoneNumber(contact: Contact, phoneNumber: String): PhoneNumber {
        val operations: ArrayList<ContentProviderOperation> = ArrayList()

        operations.add(
            ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValue(Data.MIMETYPE, CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(Data.RAW_CONTACT_ID, contact.id)
                .withValue(CommonDataKinds.Phone.NUMBER, phoneNumber)
                .build()
        )

        try {
            context.contentResolver.applyBatch(AUTHORITY, operations)
            operations.clear()
            Log.d(LOG_TAG, "add success")

            return PhoneNumber(phoneNumber, CommonDataKinds.Phone.TYPE_MOBILE, "", phoneNumber)
        } catch (e: Exception) {
            Log.d(LOG_TAG, "add failed : ${e.message}")
            e.printStackTrace()
        }

        return PhoneNumber("", 0, "", null)
    }

    fun editContactPhoneNumber(
        contact: Contact,
        oldPhoneNumber: PhoneNumber,
        newNumber: String
    ): PhoneNumber {
        val operations: ArrayList<ContentProviderOperation> = ArrayList()

        val selection =
            "${Data.CONTACT_ID} = ? AND ${Data.MIMETYPE} = ? AND ${Data.DATA1} = ?"
        val selectionArgs =
            arrayOf(
                contact.id.toString(),
                CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                oldPhoneNumber.value
            )

        operations.add(
            ContentProviderOperation.newUpdate(Data.CONTENT_URI)
                .withSelection(selection, selectionArgs)
                .withValue(Data.DATA1, newNumber)
                .build()
        )

        try {
            context.contentResolver.applyBatch(AUTHORITY, operations)
            operations.clear()
            Log.d(LOG_TAG, "update success")

            return oldPhoneNumber.apply {
                value = newNumber
                normalizedNumber = newNumber
            }
        } catch (e: Exception) {
            Log.d(LOG_TAG, "update failed : ${e.message}")
            e.printStackTrace()
        }

        return oldPhoneNumber
    }

    fun deleteContactPhoneNumber(contact: Contact, phoneNumber: PhoneNumber): Boolean {
        val operations = ArrayList<ContentProviderOperation>()

        val selection =
            "${Data.RAW_CONTACT_ID} = ? AND ${Data.DATA1} = ? AND ${Data.MIMETYPE} = ? "
        val selectionArgs =
            arrayOf(
                contact.id.toString(),
                phoneNumber.value,
                CommonDataKinds.Phone.CONTENT_ITEM_TYPE
            )

        operations.add(
            ContentProviderOperation.newDelete(Data.CONTENT_URI)
                .withSelection(selection, selectionArgs)
                .build()
        )

        try {
            context.contentResolver.applyBatch(AUTHORITY, operations)
            operations.clear()
            Log.d(LOG_TAG, "delete success")

            return true
        } catch (e: Exception) {
            Log.d(LOG_TAG, "delete failed : ${e.message}")
            e.printStackTrace()
        }

        return false
    }

    fun addContactEmail(contact: Contact, email: String): Email {
        val operations: ArrayList<ContentProviderOperation> = ArrayList()

        operations.add(
            ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValue(Data.MIMETYPE, CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(Data.RAW_CONTACT_ID, contact.id)
                .withValue(CommonDataKinds.Email.DATA, email)
                .build()
        )

        try {
            context.contentResolver.applyBatch(AUTHORITY, operations)
            operations.clear()
            Log.d(LOG_TAG, "add success")

            return Email(email, CommonDataKinds.Email.TYPE_CUSTOM, "")
        } catch (e: Exception) {
            Log.d(LOG_TAG, "add failed : ${e.message}")
            e.printStackTrace()
        }

        return Email("", 0, "")
    }

    fun editContactEmail(contact: Contact, oldEmail: Email, newEmail: String): Email {
        val operations: ArrayList<ContentProviderOperation> = ArrayList()

        val selection =
            "${Data.CONTACT_ID} = ? AND ${Data.MIMETYPE} = ? AND ${CommonDataKinds.Email.DATA} = ? "
        val selectionArgs =
            arrayOf(
                contact.id.toString(),
                CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                oldEmail.value
            )

        operations.add(
            ContentProviderOperation.newUpdate(Data.CONTENT_URI)
                .withSelection(selection, selectionArgs)
                .withValue(Data.DATA1, newEmail)
                .build()
        )

        try {
            context.contentResolver.applyBatch(AUTHORITY, operations)
            operations.clear()
            Log.d(LOG_TAG, "update success")

            return oldEmail.apply {
                value = newEmail
            }
        } catch (e: Exception) {
            Log.d(LOG_TAG, "update failed : ${e.message}")
            e.printStackTrace()
        }

        return oldEmail
    }

    fun deleteContactEmail(contact: Contact, email: Email): Boolean {
        val operations = ArrayList<ContentProviderOperation>()

        val selection =
            "${Data.RAW_CONTACT_ID} = ? AND ${Data.DATA1} = ? AND ${Data.MIMETYPE} = ? "
        val selectionArgs =
            arrayOf(contact.id.toString(), email.value, CommonDataKinds.Email.CONTENT_ITEM_TYPE)

        operations.add(
            ContentProviderOperation.newDelete(Data.CONTENT_URI)
                .withSelection(selection, selectionArgs)
                .build()
        )

        try {
            context.contentResolver.applyBatch(AUTHORITY, operations)
            operations.clear()
            Log.d(LOG_TAG, "delete success")

            return true
        } catch (e: Exception) {
            Log.d(LOG_TAG, "delete failed : ${e.message}")
            e.printStackTrace()
        }

        return false
    }

}