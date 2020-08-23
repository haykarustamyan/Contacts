package am.testtpkg.contacts.ui.dialog

import am.testtpkg.contacts.R
import am.testtpkg.contacts.domain.model.Email
import android.os.Bundle
import androidx.fragment.app.Fragment

class DeleteContactEmailDialog : BaseDialog() {

    companion object {
        private const val KEY_EMAIL = "email"
        private const val KEY_EMAIL_POS = "email_pos"
        private const val TAG = "DeleteContactEmailDialog"

        fun <T> show(
            parentFragment: T,
            email: Email,
            position: Int
        ) where T : Fragment, T : DeleteContactEmailDialogOperationsListener {
            val dialog = DeleteContactEmailDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_EMAIL, email)
                    putInt(KEY_EMAIL_POS, position)
                }
            }
            dialog.show(parentFragment.childFragmentManager, TAG)
        }
    }

    override fun getLayout() =
        R.layout.dialog_delete_layout

    override fun getTitle(): String {
        return getString(R.string.delete_contact_email_dialog_title_text)
    }

    override fun initDialogDetails() {
        val email = requireArguments().getParcelable<Email>(KEY_EMAIL)
        val position = requireArguments().getInt(KEY_EMAIL_POS)

        val callback = parentFragment as? DeleteContactEmailDialogOperationsListener
            ?: throw IllegalArgumentException("Parent Fragment must implement DeleteContactEmailDialogOperationsListener")

        okButton.setOnClickListener {
            callback.onDeleteContactEmailDialogDeleteClick(this, email!!,position)
        }

        cancelButton.setOnClickListener {
            callback.onDialogCancelClick(this)
        }
    }
}

interface DeleteContactEmailDialogOperationsListener {
    fun onDeleteContactEmailDialogDeleteClick(dialog: DeleteContactEmailDialog, email: Email, position:Int)
    fun onDialogCancelClick(dialog: BaseDialog)
}
