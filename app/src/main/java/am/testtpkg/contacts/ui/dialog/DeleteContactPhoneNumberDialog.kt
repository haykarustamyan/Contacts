package am.testtpkg.contacts.ui.dialog

import am.testtpkg.contacts.R
import am.testtpkg.contacts.domain.model.PhoneNumber
import android.os.Bundle
import androidx.fragment.app.Fragment

class DeleteContactPhoneNumberDialog : BaseDialog() {

    companion object {
        private const val KEY_PHONE = "phone_number"
        private const val KEY_PHONE_POS = "phone_number_pos"
        private const val TAG = "DeleteContactPhoneNumberDialog"

        fun <T> show(
            parentFragment: T,
            phoneNumber: PhoneNumber,
            position: Int
        ) where T : Fragment, T : DeleteContactPhoneNumberDialogOperationsListener {
            val dialog = DeleteContactPhoneNumberDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_PHONE, phoneNumber)
                    putInt(KEY_PHONE_POS, position)
                }
            }
            dialog.show(parentFragment.childFragmentManager, TAG)
        }
    }

    override fun getLayout() =
        R.layout.dialog_delete_layout

    override fun getTitle(): String {
        return getString(R.string.delete_phone_number_dialog_title_text)
    }

    override fun initDialogDetails() {
        val phoneNumber = requireArguments().getParcelable<PhoneNumber>(KEY_PHONE)
        val position = requireArguments().getInt(KEY_PHONE_POS)

        val callback = parentFragment as? DeleteContactPhoneNumberDialogOperationsListener
            ?: throw IllegalArgumentException("Parent Fragment must implement DeleteContactPhoneNumberDialogOperationsListener")

        okButton.setOnClickListener {
            callback.onDeleteContactPhoneNumberDialogDeleteClick(this, phoneNumber!!,position)
        }

        cancelButton.setOnClickListener {
            callback.onDialogCancelClick(this)
        }
    }
}

interface DeleteContactPhoneNumberDialogOperationsListener {
    fun onDeleteContactPhoneNumberDialogDeleteClick(
        dialog: DeleteContactPhoneNumberDialog,
        phoneNumber: PhoneNumber,
        position:Int
    )

    fun onDialogCancelClick(dialog: BaseDialog)
}
