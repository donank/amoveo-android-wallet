package donank.amoveowallet.Data.Model.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.AsyncTask
import donank.amoveowallet.Data.Model.Wallet
import donank.amoveowallet.Data.WalletDao

class WalletListViewModel : ViewModel() {
    lateinit var walletDao: WalletDao
    private var wallets = MutableLiveData<List<Wallet>>()
    fun getWallets(): LiveData<List<Wallet>>{
        if(wallets == null){
            wallets = MutableLiveData<List<Wallet>>()
            loadWallets()
        }
        return wallets
    }

    fun loadWallets(){
        AsyncTask.execute {
            walletDao.getWallets()
        }
    }
}