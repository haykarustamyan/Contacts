package am.testtpkg.contacts.ui.dialog

import am.testtpkg.contacts.R
import android.text.InputType
import android.widget.Toast
import androidx.fragment.app.Fragment

class AddContactPhoneNumberDialog : BaseDialog() {

    companion object {
        private const val TAG = "AddContactPhoneNumberDialog"

        fun <T> show(
            parentFragment: T
        ) where T : Fragment, T : AddContactPhoneNumberDialogOperationsListener {
            val dialog = AddContactPhoneNumberDialog()
            dialog.show(parentFragment.childFragmentManager, TAG)
        }
    }

    override fun getLayout() =
        R.layout.dialog_edit_text_layout

    override fun getTitle(): String {
        return getString(R.string.add_contact_phone_number_dialog_title_text)
    }

    override fun initDialogDetails() {
        dialogEt?.requestFocus()
        dialogEt?.inputType = InputType.TYPE_CLASS_PHONE

        val callback = parentFragment as? AddContactPhoneNumberDialogOperationsListener
            ?: throw IllegalArgumentException("Parent Fragment must implement AddContactPhoneNumberDialogOperationsListener")

        okButton.setOnClickListener {
            if (!dialogEt!!.text.trim().isEmpty()) {
                callback.onAddContactPhoneNumberDialogOkClick(this, dialogEt!!.text.toString())
            } else {
                Toast.makeText(
                    requireActivity(),
                    R.string.fill_error_text, Toast.LENGTH_SHORT
                ).show()
            }
        }

        cancelButton.setOnClickListener {
            callback.onDialogCancelClick(this)
        }
    }
}

interface AddContactPhoneNumberDialogOperationsListener {
    fun onAddContactPhoneNumberDialogOkClick(dialog: AddContactPhoneNumberDialog, phoneNumber: String)
    fun onDialogCancelClick(dialog: BaseDialog)
}
