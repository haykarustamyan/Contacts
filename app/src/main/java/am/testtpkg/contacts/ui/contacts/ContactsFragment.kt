package am.testtpkg.contacts.ui.contacts

import am.testtpkg.contacts.R
import am.testtpkg.contacts.common.Constants
import am.testtpkg.contacts.common.Constants.requiredPermissionMap
import am.testtpkg.contacts.common.confirmPermissionResults
import am.testtpkg.contacts.common.hasPermission
import am.testtpkg.contacts.common.hasPermissions
import am.testtpkg.contacts.domain.model.Contact
import am.testtpkg.contacts.ui.dialog.AddNewContactDialog
import am.testtpkg.contacts.ui.dialog.AddNewContactDialogOperationsListener
import am.testtpkg.contacts.ui.main.MainActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.empty_layout.*
import kotlinx.android.synthetic.main.fragment_contacts.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContactsFragment : Fragment(R.layout.fragment_contacts),
    AddNewContactDialogOperationsListener {

    private val contactsViewModel by viewModel<ContactsViewModel>()
    private val contactsSharedViewModel by sharedViewModel<ContactsSharedViewModel>()
    private val contactsAdapter by inject<ContactsAdapter>()

    private val requestAppSettings = 600
    private val permissionCode = 500

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        initViews()
        setListeners()
    }

    private fun setUpToolbar() = requireActivity().run {
        (this as MainActivity).setUpToolBar(isRootPage = true)
    }

    private fun initViews() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
            isNestedScrollingEnabled = false
            adapter = contactsAdapter
        }
    }

    private fun setListeners() {
        addContactFab.setOnClickListener {
            contactsViewModel.showAddDialog("")
        }

        contactsAdapter.setOnItemClickListener(object : ContactsAdapter.OnItemClickListener {
            override fun onItemClick(view: View, contact: Contact, position: Int) {
                Log.d(Constants.LOG_TAG, "click $position")

                contactsSharedViewModel.selectContact(contact)
                findNavController().navigate(ContactsFragmentDirections.actionContactsFragmentToContactItemFragment())
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkPermissions()
        observeData()
    }

    private fun checkPermissions() {
        if (!requireActivity().hasPermissions(requiredPermissionMap.keys)) {
            requestPermissions(requiredPermissionMap.keys.toTypedArray(), permissionCode)
        } else {
            onPermissionGranted()
        }
    }

    private fun observeData() {
        contactsViewModel.showAddDialogLiveData.observe(
            viewLifecycleOwner,
            Observer {
                it.getContentIfNotHandled()?.let {
                    showAddNewContactDialog()
                }
            })

        contactsViewModel.notifyNewContactInsertedLiveData.observe(
            viewLifecycleOwner, Observer { event ->
                event.getContentIfNotHandled()?.let {
                    contactsAdapter.notifyNewContactAdded(it)
                }
            })
    }

    private fun observeContacts() {
        // TODO change to Event()
        contactsViewModel.loadContacts().observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                Log.d(Constants.LOG_TAG, "$it")
                contactsAdapter.submitList(it)
            } else {
                showEmptyView()
            }
        })
    }

    private fun showEmptyView() {
        if (emptyLayout.visibility == View.GONE) {
            emptyLayout.visibility = View.VISIBLE
        }
    }

    private fun shouldShowPermissionExplanation(permissionString: String): Boolean =
        ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permissionString)

    private fun onPermissionGranted() {
        observeContacts()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (permissionCode == requestCode) {
            if (requireActivity().confirmPermissionResults(grantResults)) {
                onPermissionGranted()
            } else {
                for (permission in permissions) {
                    if (requireActivity().hasPermission(permission)) continue
                    if (shouldShowPermissionExplanation(permission)) {
                        Snackbar.make(
                            requireActivity().window.decorView.rootView,
                            requiredPermissionMap[permission]!!, Snackbar.LENGTH_INDEFINITE
                        ).apply {
                            setAction(R.string.grant) {
                                checkPermissions()
                            }
                            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                                .maxLines = 5
                            show()
                        }
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            R.string.permission_denied, Toast.LENGTH_SHORT
                        ).show()
                        MaterialDialog(requireActivity()).show {
                            title(R.string.enable_permission)
                            message(R.string.manual_permission_grant_instruction)
                            positiveButton(R.string.settings) {
                                openAppSettings()
                                requireActivity().finish()
                            }
                            negativeButton(R.string.cancel) {
                                Toast.makeText(
                                    requireActivity(),
                                    R.string.permission_denied, Toast.LENGTH_SHORT
                                ).show()
                                requireActivity().finish()
                            }
                        }
                    }
                    break
                }
            }
        }
    }

    private fun showAddNewContactDialog() {
        AddNewContactDialog.show(this)
    }

    private fun openAppSettings() {
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${requireActivity().packageName}")
        ).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivityForResult(appSettingsIntent, requestAppSettings)
    }

    override fun onAddContactDialogOkClick(dialogNew: AddNewContactDialog, contact: Contact) {
        dialogNew.dismiss()
        contactsViewModel.addNewContact(contact)
    }

}