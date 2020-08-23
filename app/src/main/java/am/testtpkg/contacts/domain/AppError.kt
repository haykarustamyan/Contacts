package am.testtpkg.contacts.domain

sealed class AppError : Error() {
    object GetContactsError : AppError()
}