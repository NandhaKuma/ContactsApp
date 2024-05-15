package com.task.contacts.network



import com.task.contacts.model.ContactsResponse
import retrofit2.http.*

interface BuyerNetworkApi {

    @GET
    suspend fun getContacts(@Url url:String): ContactsResponse



}
