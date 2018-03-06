package donank.amoveowallet.Api

import donank.amoveowallet.Data.AppPref
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.POST

interface RESTInterface {

    @POST
    fun postRequest(command : String): Observable<String>

    companion object {
        fun create(client:OkHttpClient): RESTInterface? {
            return Retrofit.Builder()
                    .client(client)
                    .baseUrl(AppPref.baseUrl)
                    .build()
                    .create(RESTInterface::class.java)

        }
    }
}