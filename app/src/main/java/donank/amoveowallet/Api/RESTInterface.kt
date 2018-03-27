package donank.amoveowallet.Api

import com.squareup.moshi.Moshi
import donank.amoveowallet.Data.AppPref
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface RESTInterface {

    @POST
    fun postRequest(@Body requestBody : RequestBody, @Url url:String): Single<ResponseBody>

    companion object {
        fun create(client:OkHttpClient, moshi: Moshi): RESTInterface {
            return Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                    .client(client)
                    .baseUrl(AppPref.peerUrl)
                    .build()
                    .create(RESTInterface::class.java)

        }
    }
}