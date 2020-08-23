package am.testtpkg.contacts.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contact(
    val id: Int,
    var firstName: String,
    val surname: String,
    val middleName: String,
    val contactId: Int,
    var phoneNumbers: List<PhoneNumber>,
    var accountName: String,
    var accountType: String,
    var emails: ArrayList<Email>?
) : Parcelable {

    val fullName: String
        get() = "$firstName $middleName $surname"

    val multiLinedPhoneNumbers: String
        get() {
            val phoneNumbersBuilder = StringBuilder()
            phoneNumbers.forEachIndexed { index, phoneNumber ->
                phoneNumbersBuilder.append(phoneNumber.value)
                if (index != phoneNumbers.size - 1) phoneNumbersBuilder.append("\n")
            }
            return phoneNumbersBuilder.toString()
        }

    override fun toString(): String {
        return "\nContact(name: $firstName $surname, phoneNumbers: $phoneNumbers, emails: $emails)"
    }

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + firstName.hashCode()
        result = 31 * result + surname.hashCode()
        result = 31 * result + middleName.hashCode()
        result = 31 * result + contactId
        result = 31 * result + phoneNumbers.hashCode()
        result = 31 * result + accountName.hashCode()
        result = 31 * result + accountType.hashCode()
        result = 31 * result + (emails?.hashCode() ?: 0)
        return result
    }

}