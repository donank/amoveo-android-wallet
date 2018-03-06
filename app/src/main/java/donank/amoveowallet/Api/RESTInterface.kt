package donank.amoveowallet.Api

import donank.amoveowallet.Data.AppPref
import donank.amoveowallet.Data.Explorer
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST

interface RESTInterface {

    @POST
    fun sendFetchExpCmd(@Body explorer: Explorer): Observable<Explorer>

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