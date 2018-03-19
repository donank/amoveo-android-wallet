package donank.amoveowallet.Api

import com.squareup.moshi.Moshi
import donank.amoveowallet.Data.AppPref
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.POST

interface RESTInterface {

    @POST
    fun postRequest(command : String): Observable<String>

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