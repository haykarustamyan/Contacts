package am.testtpkg.contacts.common

import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import androidx.core.content.ContextCompat

fun String.times(x: Int): String {
    val stringBuilder = StringBuilder()
    for (i in 1..x) {
        stringBuilder.append(this)
    }
    return stringBuilder.toString()
}

fun Cursor.getStringValue(key: String) = getString(getColumnIndex(key))

fun Cursor.getIntValue(key: String) = getInt(getColumnIndex(key))

fun Context.hasPermissions(permissionList: Set<String>): Boolean {
    for (permission in permissionList) {
        if (!hasPermission(permission)) return false
    }
    return true
}

fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Context.confirmPermissionResults(results: IntArray): Boolean {
    results.forEach {
        if (it != PackageManager.PERMISSION_GRANTED) return false
    }
    return true
}