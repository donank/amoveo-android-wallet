package donank.amoveowallet.Data.Model.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import donank.amoveowallet.Data.Model.Wallet
import donank.amoveowallet.Data.WalletDao

class SelectedWalletViewModel : ViewModel() {
    lateinit var walletDao: WalletDao
    private var selectedWallet = MutableLiveData<Wallet>()
    fun select(wallet: Wallet) {
        selectedWallet.value = wallet
    }

    fun getSelected(): LiveData<Wallet>{
        return selectedWallet
    }
}