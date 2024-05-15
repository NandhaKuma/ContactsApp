package com.task.contacts.viewmodel

import android.app.Application
import android.content.Context
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.task.contacts.R
import com.task.contacts.constant.AppConstant
import com.task.contacts.model.ContactsResponse
import com.task.contacts.repository.UserRepository
import com.task.contacts.room.PhoneSevicesTable
import com.task.contacts.utils.NetworkStatus
import com.task.contacts.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.collections.HashMap

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val networkStatus: NetworkStatus,
    private val application: Application
) : ViewModel() {

    val progressbar = MutableLiveData<Boolean>()


    fun getContacts(page: Int, context: Context) = liveData(Dispatchers.IO) {
        try {
            if (NetworkStatus(application).isConnectedInternet()) {
                progressbar.postValue(true)
                var pages = page * 25
                emit(Resource.success(data = userRepository.getContacts("${AppConstant.buyerBaseUrl}?results=${pages}&inc=gender,name,picture,phone,cell,id,email")))
            } else {
                val roomPhoneDetails: List<PhoneSevicesTable> =
                    userRepository.getPhoneServicesTable()

                var phone = ArrayList<ContactsResponse.Result>()

                phone.clear()

                for (i in roomPhoneDetails.indices) {

                    //    val ratingResponse: NewServicesPendingResponse.WorkLists.Ratings? = Gson().fromJson(roomDetailsToday[i].ratings, object : TypeToken<NewServicesPendingResponse.WorkLists.Ratings>() {}.type)

                    val nameResponse: ContactsResponse.Result.Name = Gson().fromJson(
                        roomPhoneDetails[i].name,
                        object : TypeToken<ContactsResponse.Result.Name>() {}.type
                    )
                    val idResponse: ContactsResponse.Result.Id =
                        Gson().fromJson(roomPhoneDetails[i].id,
                            object : TypeToken<ContactsResponse.Result.Id>() {}.type)
                    val picResponse: ContactsResponse.Result.Picture = Gson().fromJson(
                        roomPhoneDetails[i].picture,
                        object : TypeToken<ContactsResponse.Result.Picture>() {}.type
                    )
                    val phoneResponse = ContactsResponse.Result(
                        roomPhoneDetails[i].cell,
                        roomPhoneDetails[i].email,
                        roomPhoneDetails[i].gender,
                        idResponse,
                        nameResponse,
                        roomPhoneDetails[i].phone,
                        picResponse
                    )

                    phone.add(phoneResponse)

                }
                emit(
                    Resource.success(
                        data = ContactsResponse(
                            phone,
                            ContactsResponse.Info(1, 1, "dummydata", "1.4")
                        )
                    )
                )
            }


        } catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null,
                    message = exception.message
                        ?: application.resources.getString(R.string.something_went_wrong)
                )
            )
        }
    }


    fun insertPhoneDetails(workLists: List<ContactsResponse.Result>) {
        userRepository.insertPhoneServices(workLists)
    }

    fun DropPhoneServices() {
        userRepository.DropPhoneServices()
    }


}