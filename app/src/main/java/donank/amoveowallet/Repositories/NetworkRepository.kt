package donank.amoveowallet.Repositories

import donank.amoveowallet.Api.RESTInterface
import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import javax.inject.Inject

class NetworkRepository @Inject constructor(val restInterface: RESTInterface) {

    fun request(requestBody: RequestBody,url: String): Single<ResponseBody> {
        return restInterface.postRequest(requestBody,url).subscribeOn(Schedulers.newThread())
                .doOnSuccess { it }
                .onErrorReturn { ResponseBody.create(MediaType.parse("text/plain"),"error")}
    }
}