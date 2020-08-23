package am.testtpkg.contacts.ui.dialog

import am.testtpkg.contacts.R
import android.text.InputType
import android.widget.Toast
import androidx.fragment.app.Fragment


class AddContactEmailDialog : BaseDialog() {

    companion object {
        private const val TAG = "AddContactEmailDialog"

        fun <T> show(
            parentFragment: T
        ) where T : Fragment, T : AddContactEmailDialogOperationsListener {
            val dialog = AddContactEmailDialog()
            dialog.show(parentFragment.childFragmentManager, TAG)
        }
    }

    override fun getLayout() =
        R.layout.dialog_edit_text_layout

    override fun getTitle(): String {
        return getString(R.string.add_contact_email_dialog_title_text)
    }

    override fun initDialogDetails() {
        dialogEt?.requestFocus()
        dialogEt?.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS


        val callback = parentFragment as? AddContactEmailDialogOperationsListener
            ?: throw IllegalArgumentException("Parent Fragment must implement AddContactEmailDialogOperationsListener")

        okButton.setOnClickListener {
            if (!dialogEt!!.text.trim().isEmpty()) {
                callback.onAddContactEmailDialogOkClick(this, dialogEt!!.text.toString())
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

interface AddContactEmailDialogOperationsListener {
    fun onAddContactEmailDialogOkClick(dialog: AddContactEmailDialog, email: String)
    fun onDialogCancelClick(dialog: BaseDialog)
}
