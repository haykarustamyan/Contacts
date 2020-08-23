package am.testtpkg.contacts.ui.dialog

import am.testtpkg.contacts.R
import am.testtpkg.contacts.domain.model.PhoneNumber
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.fragment.app.Fragment

class EditContactPhoneNumberDialog : BaseDialog() {

    companion object {
        private const val KEY_PHONE = "phone_number"
        private const val KEY_PHONE_POS = "phone_number_pos"
        private const val TAG = "EditContactPhoneNumberDialog"

        fun <T> show(
            parentFragment: T,
            phoneNumber: PhoneNumber,
            position: Int
        ) where T : Fragment, T : EditContactPhoneNumberDialogOperationsListener {
            val dialog = EditContactPhoneNumberDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_PHONE, phoneNumber)
                    putInt(KEY_PHONE_POS, position)
                }
            }
            dialog.show(parentFragment.childFragmentManager, TAG)
        }
    }

    override fun getLayout() =
        R.layout.dialog_edit_text_layout

    override fun getTitle(): String {
        return getString(R.string.edit_phone_number_dialog_title_text)
    }

    override fun initDialogDetails() {
        val phoneNumber = requireArguments().getParcelable<PhoneNumber>(KEY_PHONE)
        val position = requireArguments().getInt(KEY_PHONE_POS)

        dialogEt?.setText(phoneNumber?.value)
        dialogEt?.requestFocus()
        dialogEt?.inputType = InputType.TYPE_CLASS_PHONE

        val callback = parentFragment as? EditContactPhoneNumberDialogOperationsListener
            ?: throw IllegalArgumentException("Parent Fragment must implement EditContactPhoneNumberDialogOperationsListener")

        okButton.setOnClickListener {
            if (!dialogEt!!.text.trim().isEmpty()) {
                callback.onEditContactPhoneNumberDialogOkClick(
                    this,
                    phoneNumber!!,
                    dialogEt!!.text.toString(),
                    position
                )
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

interface EditContactPhoneNumberDialogOperationsListener {
    fun onEditContactPhoneNumberDialogOkClick(
        dialog: EditContactPhoneNumberDialog,
        oldPhoneNumber: PhoneNumber,
        newNumber: String,
        position: Int
    )

    fun onDialogCancelClick(dialog: BaseDialog)
}
