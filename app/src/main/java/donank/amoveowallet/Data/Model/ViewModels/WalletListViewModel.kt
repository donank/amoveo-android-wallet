package donank.amoveowallet.Data.Model.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import donank.amoveowallet.Data.Model.WalletModel
import donank.amoveowallet.Repositories.MainRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WalletListViewModel @Inject constructor(val mainRepository: MainRepository) : ViewModel() {
    private val walletsSuccess = MutableLiveData<List<WalletModel>>()
    private val walletsError: MutableLiveData<String> = MutableLiveData()

    lateinit var disposableObserver: DisposableObserver<List<WalletModel>>

    fun walletsResult(): LiveData<List<WalletModel>>{
        return walletsSuccess
    }

    fun walletsError(): LiveData<String> {
        return walletsError
    }

    fun loadWallets(){
        disposableObserver = object : DisposableObserver<List<WalletModel>>() {
            override fun onComplete() {

            }

            override fun onNext(wallets: List<WalletModel>) {
                walletsSuccess.postValue(wallets)
            }

            override fun onError(e: Throwable) {
                walletsError.postValue(e.message)
            }
        }

        mainRepository.getWallets()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(400,TimeUnit.MILLISECONDS)
                .subscribe(disposableObserver)

    }

    fun disposeElements(){
        if(!disposableObserver.isDisposed) disposableObserver.dispose()
    }

}