package donank.amoveowallet.Repositories

import android.os.AsyncTask
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Data.Model.Wallet
import donank.amoveowallet.Data.WalletDao
import javax.inject.Inject

class DBRepository constructor(val walletDao: WalletDao)  {

    fun saveAddressToDb(wallet : Wallet){
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

}