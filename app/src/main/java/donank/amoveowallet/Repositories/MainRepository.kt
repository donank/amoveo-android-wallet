package donank.amoveowallet.Repositories

import android.util.Log
import donank.amoveowallet.Data.AppPref
import donank.amoveowallet.Data.Model.Wallet
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

class MainRepository @Inject constructor(val dbRepository: DBRepository, val networkRepository: NetworkRepository) {

    fun saveWalletToDb(wallet : Wallet){
        dbRepository.saveWalletToDb(wallet)
    }

    fun getWalletCountFromDb(): String{
        return dbRepository.getWalletCountFromDb()
    }

    fun update(wallet : Wallet){
        dbRepository.update(wallet)
    }

    fun getWallets(): Observable<List<Wallet>> {
        return  dbRepository.getWallets()
    }

    fun request(command: String,url: String = AppPref.peerUrl): Observable<String> {
        return networkRepository.request(createRequestBody(command),url).map { it.string() }.toObservable()
    }

    fun createRequestBody(command : String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), command)
    }
}