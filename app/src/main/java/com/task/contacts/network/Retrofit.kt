package  com.task.contacts.network


import com.task.contacts.constant.AppConstant.buyerBaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class Retrofit {
    private var buyerRetrofit: Retrofit? = null
    private val okHttpClient = OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build()

    @Provides
    @Singleton
    fun getBuyerRetrofitInstance(): BuyerNetworkApi {
        if (buyerRetrofit == null) {
            buyerRetrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(buyerBaseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return buyerRetrofit!!.create(BuyerNetworkApi::class.java)
    }
}