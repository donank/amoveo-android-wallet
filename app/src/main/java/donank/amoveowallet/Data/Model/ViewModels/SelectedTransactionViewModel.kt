package donank.amoveowallet.Data.Model.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import donank.amoveowallet.Data.Model.TransactionModel

class SelectedTransactionViewModel: ViewModel() {
    private var selectedTransaction = MutableLiveData<TransactionModel>()
    fun select(transactionModel: TransactionModel) {
        selectedTransaction.value = transactionModel
    }

    fun getSelected(): LiveData<TransactionModel> {
        return selectedTransaction
    }
}