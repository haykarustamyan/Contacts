package am.testtpkg.contacts.common

import am.testtpkg.contacts.R
import android.Manifest

object Constants {

    const val LOG_TAG = "testt"

    val requiredPermissionMap = mapOf(
        Pair(Manifest.permission.READ_CONTACTS, R.string.read_contacts_permissions_explanation),
        Pair(Manifest.permission.WRITE_CONTACTS, R.string.write_contacts_permissions_explanation),
        Pair(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.external_storage_permissions_explanation),
        Pair(Manifest.permission.CALL_PHONE, R.string.call_contacts_permissions_explanation),
        Pair(Manifest.permission.SEND_SMS, R.string.sms_contacts_permissions_explanation)
    )

}