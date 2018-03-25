package donank.amoveowallet.Data.Model.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import donank.amoveowallet.Data.Model.Transaction
import donank.amoveowallet.Data.Model.Wallet
import donank.amoveowallet.Data.WalletDao

class SelectedTransactionViewModel: ViewModel() {
    lateinit var walletDao: WalletDao
    private var selectedTransaction = MutableLiveData<Transaction>()
    fun select(transaction: Transaction) {
        selectedTransaction.value = transaction
    }

    fun getSelected(): LiveData<Transaction> {
        return selectedTransaction
    }
}