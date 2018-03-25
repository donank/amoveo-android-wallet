package donank.amoveowallet.Data.Model.ViewModels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject

class WalletListViewModelFactory @Inject constructor(private val walletListViewModel: WalletListViewModel)
    :ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(WalletListViewModel::class.java)){
            return walletListViewModel as T
        }
        throw IllegalArgumentException("Unknown Class Name")
    }
}