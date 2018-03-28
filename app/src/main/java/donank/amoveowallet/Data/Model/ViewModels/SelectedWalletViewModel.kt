package donank.amoveowallet.Data.Model.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import donank.amoveowallet.Data.Model.WalletModel

class SelectedWalletViewModel : ViewModel() {
    private var selectedWallet = MutableLiveData<WalletModel>()
    fun select(walletModel: WalletModel) {
        selectedWallet.value = walletModel
    }

    fun getSelected(): LiveData<WalletModel>{
        return selectedWallet
    }
}