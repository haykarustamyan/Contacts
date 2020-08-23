package am.testtpkg.contacts.ui.dialog

import am.testtpkg.contacts.R
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment

class EditContactNameDialog : BaseDialog() {

    companion object {
        private const val TEXT_KEY = "text"
        private const val TAG = "EditContactNameDialog"

        fun <T> show(
            parentFragment: T,
            text: String
        ) where T : Fragment, T : EditContactNameDialogOperationsListener {
            val dialog = EditContactNameDialog().apply {
                arguments = Bundle().apply {
                    putString(TEXT_KEY, text)
                }
            }
            dialog.show(parentFragment.childFragmentManager, TAG)
        }
    }

    override fun getLayout() =
        R.layout.dialog_edit_text_layout

    override fun getTitle(): String {
        return getString(R.string.edit_contact_name_dialog_title_text)
    }

    override fun initDialogDetails() {
        val text = requireArguments().getString(TEXT_KEY)

        dialogEt?.setText(text)
        dialogEt?.requestFocus()

        val callback = parentFragment as? EditContactNameDialogOperationsListener
            ?: throw IllegalArgumentException("Parent Fragment must implement EditContactNameDialogOperationsListener")

        okButton.setOnClickListener {
            if (!dialogEt!!.text.trim().isEmpty()) {
                callback.onEditContactNameDialogOkClick(this, dialogEt!!.text.toString())
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

interface EditContactNameDialogOperationsListener {
    fun onEditContactNameDialogOkClick(dialog: EditContactNameDialog, newName: String)
    fun onDialogCancelClick(dialog: BaseDialog)
}
