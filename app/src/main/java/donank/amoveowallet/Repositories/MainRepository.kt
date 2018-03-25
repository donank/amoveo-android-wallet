package donank.amoveowallet.Repositories

import android.os.AsyncTask
import android.util.Log
import donank.amoveowallet.Api.RESTInterface
import donank.amoveowallet.Data.Model.Wallet
import donank.amoveowallet.Data.WalletDao
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainRepository @Inject constructor(val walletDao: WalletDao, val restInterface: RESTInterface) {

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

    fun getAddressValue(wallet : Wallet) {
        val command = """["account","${wallet.address}"]"""
        restInterface.postRequest(command)
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    val res = it.replace("\\s+","").split(",")
                    if(res[0] == """["ok""""){
                        wallet.value = res[2].toLong()
                    }else{

                        Log.d("GETADDRESSVALUE","Error loading account details!")

                    }
                },{

                    Log.d("GETADDRESSVALUE","Error loading account details!")

                })
    }

    fun getWallets(): Observable<List<Wallet>> {
        return walletDao.getWallets().toObservable()
    }
}