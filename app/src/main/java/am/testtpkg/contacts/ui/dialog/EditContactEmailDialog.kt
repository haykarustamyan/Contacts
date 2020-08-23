package am.testtpkg.contacts.ui.dialog

import am.testtpkg.contacts.R
import am.testtpkg.contacts.domain.model.Email
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.fragment.app.Fragment

class EditContactEmailDialog : BaseDialog() {

    companion object {
        private const val KEY_EMAIL = "email"
        private const val KEY_EMAIL_POS = "email_pos"
        private const val TAG = "EditContactEmailDialog"

        fun <T> show(
            parentFragment: T,
            email: Email,
            position: Int
        ) where T : Fragment, T : EditContactEmailDialogOperationsListener {
            val dialog = EditContactEmailDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_EMAIL, email)
                    putInt(KEY_EMAIL_POS, position)
                }
            }
            dialog.show(parentFragment.childFragmentManager, TAG)
        }
    }

    override fun getLayout() =
        R.layout.dialog_edit_text_layout

    override fun getTitle(): String {
        return getString(R.string.edit_contact_email_dialog_title_text)
    }

    override fun initDialogDetails() {
        val email = requireArguments().getParcelable<Email>(KEY_EMAIL)
        val position = requireArguments().getInt(KEY_EMAIL_POS)

        dialogEt?.setText(email?.value)
        dialogEt?.requestFocus()
        dialogEt?.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        val callback = parentFragment as? EditContactEmailDialogOperationsListener
            ?: throw IllegalArgumentException("Parent Fragment must implement EditContactEmailDialogOperationsListener")

        okButton.setOnClickListener {
            if (!dialogEt!!.text.trim().isEmpty()) {
                callback.onEditContactEmailDialogOkClick(this, email!!, dialogEt!!.text.toString(),position)
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

interface EditContactEmailDialogOperationsListener {
    fun onEditContactEmailDialogOkClick(
        dialog: EditContactEmailDialog,
        oldEmail: Email,
        newEmail: String,
        position: Int
    )

    fun onDialogCancelClick(dialog: BaseDialog)
}
