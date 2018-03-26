package donank.amoveowallet.Repositories

import android.os.AsyncTask
import donank.amoveowallet.Data.Model.Wallet
import donank.amoveowallet.Data.WalletDao
import io.reactivex.Observable
import javax.inject.Inject

class DBRepository @Inject constructor(val walletDao: WalletDao)  {

    fun saveWalletToDb(wallet : Wallet){
        AsyncTask.execute {
            walletDao.save(wallet)
        }
    }

    fun getWalletCountFromDb(): String{
        var count = ""
        AsyncTask.execute {
            count = walletDao.getWalletCount().toString()
        }
        return count
    }

    fun update(wallet : Wallet){
        AsyncTask.execute{
            walletDao.update(wallet)
        }
    }

    fun getWallets(): Observable<List<Wallet>> {
        return walletDao.getWallets().toObservable()
    }

}