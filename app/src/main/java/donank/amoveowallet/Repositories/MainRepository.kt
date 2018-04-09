package donank.amoveowallet.Repositories

import donank.amoveowallet.Data.AppPref
import donank.amoveowallet.Data.Model.ContactsModel
import donank.amoveowallet.Data.Model.WalletModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

class MainRepository @Inject constructor(val dbRepository: DBRepository, val networkRepository: NetworkRepository) {

    val cryptoRepository = CryptoRepository()
    fun saveWalletToDb(walletModel : WalletModel){
        walletModel.password = cryptoRepository.encrypt(walletModel.password)
        dbRepository.saveWalletToDb(walletModel)
    }

    fun getWalletCountFromDb(): Observable<Int>{
        return dbRepository.getWalletCountFromDb().subscribeOn(Schedulers.newThread()).toObservable()
    }

    fun update(walletModel : WalletModel){
        dbRepository.update(walletModel)
    }

    fun getWallets(): Observable<List<WalletModel>> {
        return  dbRepository.getWallets()
    }

    fun getContacts():Observable<List<ContactsModel>>{
        return dbRepository.getContacts()
    }

    fun request(command: String,url: String = AppPref.peerUrl): Observable<String> {
        return networkRepository.request(createRequestBody(command),url).map { it.string() }.toObservable()
    }

    /*
    fun testRequest(command: Array<String>,url: String = AppPref.peerUrl): Observable<Array<Map<String,Array<String>>>>{
        return networkRepository.request(createRequestBody(command),url).map { it. }
    }
    */

    fun createRequestBody(command : String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), command)
    }

    fun saveContactToDb(contact: ContactsModel) {
        dbRepository.saveContact(contact)
    }

    fun updateContact(contact: ContactsModel) {
        dbRepository.updateContact(contact)
    }
}