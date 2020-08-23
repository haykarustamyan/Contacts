package am.testtpkg.contacts.ui.dialog

import am.testtpkg.contacts.R
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.button.MaterialButton

abstract class BaseDialog : DialogFragment() {

    protected var dialogEt: EditText? = null
    protected var dialogTv: TextView? = null
    protected lateinit var okButton: MaterialButton
    protected lateinit var cancelButton: MaterialButton

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
        dialogEt = view.findViewById(R.id.dialogEt)
        dialogTv = view.findViewById(R.id.dialogTv)
        okButton = view.findViewById(R.id.okBtn)
        cancelButton = view.findViewById(R.id.cancelBtn)
        initDialogDetails()
        return dialog
    }

    abstract fun getLayout(): Int
    abstract fun getTitle(): String
    abstract fun initDialogDetails()
}

enum class DialogType {
    ADD_NEW_CONTACT_DIALOG,
    EDIT_CONTACT_NAME_DIALOG,
    ADD_CONTACT_PHONE_NUMBER_DIALOG,
    DELETE_CONTACT_PHONE_NUMBER_DIALOG,
    ADD_CONTACT_EMAIL_DIALOG,
    EDIT_CONTACT_PHONE_NUMBER_DIALOG,
    EDIT_CONTACT_EMAIL_DIALOG,
    DELETE_CONTACT_EMAIL_DIALOG
}