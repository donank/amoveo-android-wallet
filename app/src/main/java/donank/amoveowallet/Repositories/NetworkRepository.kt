package donank.amoveowallet.Repositories

import android.util.Log
import donank.amoveowallet.Api.RESTInterface
import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NetworkRepository @Inject constructor(val restInterface: RESTInterface) {

    fun getAddressValue(requestBody: RequestBody):Long {
        var value = 0L
        restInterface.postRequest(requestBody)
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    val res = it.toString().replace("\\s+","").split(",")
                    if(res[0] == """["ok""""){
                        value = res[2].toLong()
                    }else{

                            Log.d("GETADDRESSVALUE","Error loading account details!")

                    }
                },{

                        Log.d("GETADDRESSVALUE","Error loading account details!")

                })
        return value
    }

    fun validPeer(url: String, requestBody: RequestBody): Single<ResponseBody> {
        return restInterface.postRequest(requestBody,url).subscribeOn(Schedulers.newThread())
                .doOnSuccess { it }
                .onErrorReturn { ResponseBody.create(MediaType.parse("text/plain"),"error")}
    }
}