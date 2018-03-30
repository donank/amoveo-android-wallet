package donank.amoveowallet.Repositories

import android.os.AsyncTask
import donank.amoveowallet.Data.Model.ContactsModel
import donank.amoveowallet.Data.Model.WalletModel
import donank.amoveowallet.Data.WalletDao
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DBRepository @Inject constructor(val walletDao: WalletDao)  {

    fun saveWalletToDb(walletModel : WalletModel){
        AsyncTask.execute {
            walletDao.save(walletModel)
        }
    }

    fun getWalletCountFromDb(): Single<Int> {
        return walletDao.getWalletCount().subscribeOn(Schedulers.newThread())
    }

    fun update(walletModel : WalletModel){
        AsyncTask.execute{
            walletDao.update(walletModel)
        }
    }

    fun getWallets(): Observable<List<WalletModel>> {
        return walletDao.getWallets().toObservable()
    }

    fun getContacts(): Observable<List<ContactsModel>> {
        return walletDao.getContacs().toObservable()
    }

    fun saveContact(contact: ContactsModel) {
        AsyncTask.execute{
            walletDao.saveContact(contact)
        }
    }

}