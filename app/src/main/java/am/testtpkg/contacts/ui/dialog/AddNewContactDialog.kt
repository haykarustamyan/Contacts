package am.testtpkg.contacts.ui.dialog

import am.testtpkg.contacts.R
import am.testtpkg.contacts.domain.model.Contact
import am.testtpkg.contacts.domain.model.Email
import am.testtpkg.contacts.domain.model.PhoneNumber
import android.app.Dialog
import android.os.Bundle
import android.provider.ContactsContract
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.button.MaterialButton


class AddNewContactDialog : DialogFragment() {

    private lateinit var okButton: MaterialButton
    private lateinit var cancelButton: MaterialButton
    private var dialogContactNameEt: EditText? = null
    private var dialogContactPhoneNumberEt: EditText? = null
    private var dialogContactEmailEt: EditText? = null

    companion object {
        private const val TAG = "AddNewContactDialog"

        fun <T> show(
            parentFragment: T
        ) where T : Fragment, T : AddNewContactDialogOperationsListener {
            val dialog = AddNewContactDialog()
            dialog.show(parentFragment.childFragmentManager, TAG)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        val dialog = MaterialDialog(requireContext())
            .customView(
                getLayout(),
                noVerticalPadding = false,
                dialogWrapContent = true
            )
            .title(text = getTitle())
            .noAutoDismiss()
        val view = dialog.getCustomView()
        dialogContactNameEt = view.findViewById(R.id.dialogContactNameEt)
        dialogContactPhoneNumberEt = view.findViewById(R.id.dialogContactPhoneNumberEt)
        dialogContactEmailEt = view.findViewById(R.id.dialogContactEmailEt)
        okButton = view.findViewById(R.id.okBtn)
        cancelButton = view.findViewById(R.id.cancelBtn)
        initDialogDetails()
        return dialog
    }

    fun getLayout() =
        R.layout.dialog_new_contact_layout

    fun getTitle(): String {
        return getString(R.string.add_contact_dialog_title_text)
    }

    fun initDialogDetails() {
        dialogContactNameEt?.requestFocus()


        val callback = parentFragment as? AddNewContactDialogOperationsListener
            ?: throw IllegalArgumentException("Parent Fragment must implement AddNewContactDialogOperationsListener")

        okButton.setOnClickListener {
            if (dialogContactNameEt!!.text.trim()
                    .isNotEmpty() && dialogContactPhoneNumberEt!!.text.trim().isNotEmpty()
            ) {
                callback.onAddContactDialogOkClick(
                    this, Contact(
                        0, dialogContactNameEt!!.text.toString(), "", "", 0,
                        listOf(
                            PhoneNumber(
                                dialogContactPhoneNumberEt!!.text.toString(),
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                                "",
                                dialogContactPhoneNumberEt!!.text.toString()
                            )
                        ), "", "",
                        arrayListOf(
                            Email(
                                dialogContactEmailEt!!.text.toString(),
                                ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM,
                                ""
                            )
                        )
                    )
                )
            } else {
                Toast.makeText(
                    requireActivity(),
                    R.string.fill_error_text, Toast.LENGTH_SHORT
                ).show()
            }
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }
}

interface AddNewContactDialogOperationsListener {
    fun onAddContactDialogOkClick(dialogNew: AddNewContactDialog, contact: Contact)
}
