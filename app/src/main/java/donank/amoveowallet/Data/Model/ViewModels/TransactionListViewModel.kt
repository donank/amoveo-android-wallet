package donank.amoveowallet.Data.Model.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import donank.amoveowallet.Data.Model.TransactionModel
import donank.amoveowallet.Repositories.MainRepository
import javax.inject.Inject

class TransactionListViewModel @Inject constructor(val mainRepository: MainRepository): ViewModel() {
    private var transactions = MutableLiveData<List<TransactionModel>>()
    fun getTransactions(pubKey : String): LiveData<List<TransactionModel>> {
        if(transactions == null){
            transactions = MutableLiveData<List<TransactionModel>>()
            loadWallets(pubKey)
        }
        return transactions
    }

    fun loadWallets(pubKey: String) {
    }
}