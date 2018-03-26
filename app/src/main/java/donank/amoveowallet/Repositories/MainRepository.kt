package donank.amoveowallet.Repositories

import donank.amoveowallet.Data.Model.Wallet
import io.reactivex.Observable
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

    fun validPeer():Pair<Boolean,String>{
        return networkRepository.validPeer()
    }
}