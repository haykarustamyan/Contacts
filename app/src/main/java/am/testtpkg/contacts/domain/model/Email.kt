package am.testtpkg.contacts.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Email(var value: String, var type: Int, var label: String) : Parcelable