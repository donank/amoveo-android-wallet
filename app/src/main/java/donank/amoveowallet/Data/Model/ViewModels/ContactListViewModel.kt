package donank.amoveowallet.Data.Model.ViewModels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import donank.amoveowallet.Data.Model.ContactsModel
import donank.amoveowallet.Repositories.MainRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ContactListViewModel @Inject constructor(val mainRepository: MainRepository) : ViewModel() {
    private val contactsSuccess = MutableLiveData<List<ContactsModel>>()
    private val contactsError : MutableLiveData<String> = MutableLiveData()

    lateinit var disposableObserver: DisposableObserver<List<ContactsModel>>

    fun contactsResult(): MutableLiveData<List<ContactsModel>> {
        return contactsSuccess
    }

    fun contactsError(): MutableLiveData<String> {
        return contactsError
    }

    fun loadContacts(){
        disposableObserver = object : DisposableObserver<List<ContactsModel>>() {
            override fun onComplete() {

            }

            override fun onNext(contacts: List<ContactsModel>) {
                contactsSuccess.postValue(contacts)
            }

            override fun onError(e: Throwable) {
                contactsError.postValue(e.message)
            }

        }
        mainRepository.getContacts()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(400, TimeUnit.MILLISECONDS)
                .subscribe(disposableObserver)
    }
    fun disposeElements(){
        if(!disposableObserver.isDisposed) disposableObserver.dispose()
    }

}