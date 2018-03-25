package donank.amoveowallet.Data.Model.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import donank.amoveowallet.Data.Model.Transaction
import donank.amoveowallet.Repositories.MainRepository
import javax.inject.Inject

class TransactionListViewModel @Inject constructor(val mainRepository: MainRepository): ViewModel() {
    private var transactions = MutableLiveData<List<Transaction>>()
    fun getTransactions(pubKey : String): LiveData<List<Transaction>> {
        if(transactions == null){
            transactions = MutableLiveData<List<Transaction>>()
            loadWallets(pubKey)
        }
        return transactions
    }

    fun loadWallets(pubKey: String) {
    }
}