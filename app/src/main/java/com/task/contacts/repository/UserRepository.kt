package com.task.contacts.repository




import android.app.Application
import com.google.gson.Gson
import com.task.contacts.model.ContactsResponse
import com.task.contacts.network.BuyerNetworkApi
import com.task.contacts.room.MyDatabase
import com.task.contacts.room.PhoneSevicesTable
import com.task.contacts.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class UserRepository @Inject constructor(application: Application, private val buyerNetworkApi: BuyerNetworkApi, private val sessionManager: SessionManager) {

     private val database = MyDatabase.getChatDatabase(application)

     suspend fun getContacts(url:String) = buyerNetworkApi.getContacts(url)

     fun getPhoneServicesTable() = database.myDao().getPhoneServicesTable()

     fun insertPhoneServices(workLists: List<ContactsResponse.Result>?) {
          if (workLists != null) {
               for (i in workLists.indices) {
                    CoroutineScope(Dispatchers.IO).launch {
                          val phoneSevicesTable = PhoneSevicesTable(i,workLists[i].cell,workLists[i].email,workLists[i].gender,Gson().toJson(workLists[i].id),Gson().toJson(workLists[i].name),workLists[i].phone,Gson().toJson(workLists[i].picture))
                         database.myDao().insertPhoneServices(phoneSevicesTable)
                    }
               }
          }
     }


     fun DropPhoneServices() {
          CoroutineScope(Dispatchers.IO).launch {
               database.myDao().DropPhoneServices()
          }
     }

}