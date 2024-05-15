package com.task.contacts.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.task.contacts.R
import com.task.contacts.adapter.ContactsAdapter
import com.task.contacts.adapter.PhoneContactsAdapter
import com.task.contacts.databinding.ActivityMainBinding
import com.task.contacts.dialog.Alert
import com.task.contacts.model.ApiContactModel
import com.task.contacts.model.ContactsModal
import com.task.contacts.model.ContactsResponse
import com.task.contacts.utils.CommonClass
import com.task.contacts.utils.NetworkStatus
import com.task.contacts.utils.PaginationScrollListener
import com.task.contacts.utils.SessionManager
import com.task.contacts.utils.Status
import com.task.contacts.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    private val userViewModel: UserViewModel by viewModels()

    private var contactsAdapter: ContactsAdapter? = null

    private var phoneContactsAdapter: PhoneContactsAdapter? = null

    var contactsModalArrayList = ArrayList<ContactsModal>()
    var apiContactsModalArrayList = ArrayList<ApiContactModel>()


    private var totalPages = 1
    private var currentPage = 1
    private var data = 1

    @Inject
    lateinit var networkStatus: NetworkStatus


    @Inject
    lateinit var loader: com.task.contacts.dialog.Loader

    @Inject
    lateinit var alert: Alert

    @Inject
    lateinit var sessionManager: SessionManager


    private var isLoading = false
    private var isLastPage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        activityMainBinding.TabLayout.addTab(
            activityMainBinding.TabLayout.newTab()
                .setText(resources.getString(R.string.api_contact))
        )
        activityMainBinding.TabLayout.addTab(
            activityMainBinding.TabLayout.newTab()
                .setText(resources.getString(R.string.phone_contact))
        )
        activityMainBinding.TabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                data = tab.position + 1

                if (data == 1) {
                    activityMainBinding.auctionRecycler.visibility = View.VISIBLE
                    activityMainBinding.auctionLoadingShimmer.visibility = View.VISIBLE
                    activityMainBinding.noRecord.visibility = View.GONE
                    activityMainBinding.phoneRecycler.visibility = View.GONE
                    activityMainBinding.checkout.visibility = View.GONE
                    isLoading = false
                    currentPage = 1
                    getContacts()
                } else {
                    activityMainBinding.auctionRecycler.visibility = View.GONE
                    activityMainBinding.auctionLoadingShimmer.visibility = View.GONE
                    activityMainBinding.noRecord.visibility = View.GONE
                    activityMainBinding.phoneRecycler.visibility = View.VISIBLE
                    activityMainBinding.checkout.visibility = View.VISIBLE

                    contactsModalArrayList.clear()
                    prepareContactRV()
                    getPhoneContacts()

                }


            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        requestPermissions()


        activityMainBinding.checkout.setOnClickListener {
            val contactIntent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
                type = ContactsContract.RawContacts.CONTENT_TYPE
//                putExtra(ContactsContract.Intents.Insert.NAME, name)
//                putExtra(ContactsContract.Intents.Insert.PHONE, phone)
//                putExtra(ContactsContract.Intents.Insert.EMAIL, email)
            }
            startActivityForResult(contactIntent, 1)
        }


        contactsAdapter = ContactsAdapter(this)
        activityMainBinding.auctionRecycler.adapter = contactsAdapter
        contactsAdapter?.setRecyclerListener(object : ContactsAdapter.RecyclerListener {
            override fun onclick(
                position: Int,
                clickType: String,
                product: ApiContactModel
            ) {
                when (clickType) {
                    "loadMore" -> {
                        getContacts()
                    }


                }
            }
        })

        activityMainBinding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Call your function here
                activityMainBinding.ivSearch.performClick()
                true // Event handled
            } else {
                false // Event not handled
            }
        }

        activityMainBinding.ivSearch.setOnClickListener {
            CommonClass.closeKeyboard(it, this)
            if (data != 1) filter(activityMainBinding.etSearch.text.toString())
            else filterApi(activityMainBinding.etSearch.text.toString())
        }


        activityMainBinding.switchMode.isChecked = sessionManager.getDarkTheme()

        activityMainBinding.switchMode.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sessionManager.setDarkTheme(true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sessionManager.setDarkTheme(false)
            }
            //  loader.dismiss()
//            recreate(requireActivity()) // Recreate the activity to apply the new theme
            //   (activity as? MainActivity)?.connectSocket()
        }


        activityMainBinding.auctionLoadingShimmer.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        activityMainBinding.auctionRecycler.layoutManager = layoutManager
        activityMainBinding.auctionRecycler.addOnScrollListener(object :
            PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                ++currentPage
                if (NetworkStatus(application).isConnectedInternet()) getContacts()
            }

            override fun getTotalPageCount(): Int {
                return totalPages
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })

        activityMainBinding.refreshLayout.setOnRefreshListener {
            activityMainBinding.refreshLayout.isRefreshing = false
            if (data == 1) {
                contactsAdapter?.removeLoadingFooter()
                isLastPage = false
                currentPage = 1
                getContacts()
            } else {
                contactsModalArrayList.clear()
                prepareContactRV()
                getPhoneContacts()
            }


        }


        getContacts()

    }

    private fun filterApi(text: String) {
        val filteredlist1 = ArrayList<ApiContactModel>()
        // on below line we are running a loop for checking if the item is present in array list.
        for (item in apiContactsModalArrayList) {
            if (item.name?.lowercase(Locale.getDefault())?.contains(text.lowercase(Locale.getDefault())) == true) {
                // on below line we are adding item to our filtered array list.
                filteredlist1.add(item)
            }
        }
        // on below line we are checking if the filtered list is empty or not.
        if (filteredlist1.isEmpty()) {
            Toast.makeText(this, "No Contact Found", Toast.LENGTH_SHORT).show()
        } else {
            // passing this filtered list to our adapter with filter list method.
            contactsAdapter?.filterList(filteredlist1)
        }
    }


    private fun getContacts() {
        if (currentPage == 1) {
            contactsAdapter?.clearList()
            activityMainBinding.noRecord.visibility = View.GONE
            activityMainBinding.auctionRecycler.visibility = View.VISIBLE
        }
        userViewModel.getContacts(currentPage, this).observe(this) {
            activityMainBinding.auctionLoadingShimmer.visibility = View.GONE
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data != null) {
                        if (it.data.results != null) {
                            /*** calculating total pages */
                            if (currentPage == 1) {
                                activityMainBinding.auctionRecycler.layoutAnimation =
                                    AnimationUtils.loadLayoutAnimation(
                                        this,
                                        R.anim.layout_anim_dowm_to_top
                                    )
                                activityMainBinding.auctionRecycler.scheduleLayoutAnimation()
                                totalPages = 500
                            }


                            if (it.data.results != null) {
                                activityMainBinding.noRecord.visibility =
                                    if (currentPage == 1 && it.data.results.isEmpty()) View.VISIBLE
                                    else View.GONE

                                if (currentPage != 1) {
                                    contactsAdapter?.removeLoadingFooter()
                                    isLoading = false
                                }
                                //loading all the list to adapter
                                //  contactsAdapter?.addAll(it.data.results as java.util.ArrayList)

                                if (NetworkStatus(application).isConnectedInternet()) {
                                    userViewModel.DropPhoneServices()
                                    userViewModel.insertPhoneDetails(it.data.results)
                                }   // Room DB

                                for (i in it.data.results.indices) {
                                    apiContactsModalArrayList.add(
                                        ApiContactModel(
                                            "${it.data.results[i].name?.first} ${it.data.results[i].name?.last}",
                                            it.data.results[i].phone,
                                            it.data.results[i].picture?.medium
                                        )
                                    )
                                }

                                contactsAdapter?.addAll(apiContactsModalArrayList)


                                if (currentPage < totalPages) contactsAdapter?.addLoadingFooter()
                                else isLastPage = true

                            } else handleRecentAuctionError("")
                        } else activityMainBinding.noRecord.visibility =
                            if (currentPage == 1) View.VISIBLE else View.GONE
                    }
                }

                Status.ERROR -> handleRecentAuctionError(it.message ?: "")
                else -> alert.makeToastDefault(resources.getString(R.string.something_went_wrong))
            }
        }
    }

    private fun handleRecentAuctionError(error: String) {
        //  isApiInProgress = true
        if (currentPage == 1) activityMainBinding.noRecord.visibility = View.VISIBLE
        val errorMsg =
            if (!NetworkStatus(this).isConnectedInternet()) resources.getString(R.string.no_internet_connection)
            else if (error.contains("timeout")) resources.getString(R.string.error_msg_timeout)
            else if (error.isEmpty()) resources.getString(R.string.error_msg_unknown)
            else error
        contactsAdapter?.showRetry(true, errorMsg)
    }


    private fun showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        val builder: android.app.AlertDialog.Builder =
            android.app.AlertDialog.Builder(this@MainActivity)


        // below line is the title
        // for our alert dialog.
        builder.setTitle("Need Permissions")


        // below line is our message for our dialog
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS",
            DialogInterface.OnClickListener { dialog, _ -> // this method is called on click on positive
                // button and on clicking shit button we
                // are redirecting our user from our app to the
                // settings page of our app.
                dialog.cancel()
                // below is the intent from which we
                // are redirecting our user.
                val intent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.setData(uri)
                startActivityForResult(intent, 101)
            })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, _ -> // this method is called when
                // user click on negative button.
                dialog.cancel()
            })
        // below line is used
        // to display our dialog
        builder.show()
    }


    private fun requestPermissions() {
        // below line is use to request
        // permission in the current activity.
        Dexter.withActivity(this) // below line is use to request the number of
            // permissions which are required in our app.
            .withPermissions(
                Manifest.permission.READ_CONTACTS,  // below is the list of permissions
                Manifest.permission.WRITE_CONTACTS
            ) // after adding permissions we are
            // calling and with listener method.
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    // this method is called when all permissions are granted
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        // do you work now
                        getContacts()
                        Toast.makeText(
                            this@MainActivity,
                            "All the permissions are granted..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    // check for permanent denial of any permission
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        // permission is denied permanently,
                        // we will show user a dialog message.
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    permissionToken: PermissionToken?
                ) {
                    permissionToken?.continuePermissionRequest()
                }

            }).withErrorListener { // we are displaying a toast message for error message.
                Toast.makeText(applicationContext, "Error occurred! ", Toast.LENGTH_SHORT)
                    .show()
            } // below line is use to run the permissions
            // on same thread and to check the permissions
            .onSameThread().check()
    }


    @SuppressLint("Range")
    private fun getPhoneContacts() {
        // this method is use to read contact from users device.
        // on below line we are creating a string variables for
        // our contact id and display name.
        var contactId = ""
        var displayName = ""
        // on below line we are calling our content resolver for getting contacts
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        // on blow line we are checking the count for our cursor.
        if (cursor!!.count > 0) {
            // if the count is greater than 0 then we are running a loop to move our cursor to next.
            while (cursor.moveToNext()) {
                // on below line we are getting the phone number.
                val hasPhoneNumber =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        .toInt()
                if (hasPhoneNumber > 0) {
                    // we are checking if the has phone number is > 0
                    // on below line we are getting our contact id and user name for that contact
                    contactId =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    displayName =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    // on below line we are calling a content resolver and making a query
                    val phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(contactId),
                        null
                    )
                    // on below line we are moving our cursor to next position.
                    if (phoneCursor!!.moveToNext()) {
                        // on below line we are getting the phone number for our users and then adding the name along with phone number in array list.
                        val phoneNumber =
                            phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        contactsModalArrayList.add(ContactsModal(displayName, phoneNumber))
                    }
                    // on below line we are closing our phone cursor.
                    phoneCursor.close()
                }
            }
        }
        // on below line we are closing our cursor.
        cursor.close()
        // on below line we are hiding our progress bar and notifying our adapter class.
        phoneContactsAdapter?.notifyDataSetChanged()
    }


    private fun prepareContactRV() {
        // in this method we are preparing our recycler view with adapter.
        phoneContactsAdapter = PhoneContactsAdapter(this, contactsModalArrayList)
        // on below line we are setting layout manager.
        activityMainBinding.phoneRecycler.setLayoutManager(LinearLayoutManager(this))
        // on below line we are setting adapter to our recycler view.
        activityMainBinding.phoneRecycler.setAdapter(phoneContactsAdapter)
    }

    private fun filter(text: String) {
        // in this method we are filtering our array list.
        // on below line we are creating a new filtered array list.
        val filteredlist = ArrayList<ContactsModal>()
        // on below line we are running a loop for checking if the item is present in array list.
        for (item in contactsModalArrayList) {
            if (item.name.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))) {
                // on below line we are adding item to our filtered array list.
                filteredlist.add(item)
            }
        }
        // on below line we are checking if the filtered list is empty or not.
        if (filteredlist.isEmpty()) {
            Toast.makeText(this, "No Contact Found", Toast.LENGTH_SHORT).show()
        } else {
            // passing this filtered list to our adapter with filter list method.
            phoneContactsAdapter?.filterList(filteredlist)
        }
    }
}