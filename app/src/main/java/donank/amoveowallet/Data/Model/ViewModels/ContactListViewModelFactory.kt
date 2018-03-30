package donank.amoveowallet.Data.Model.ViewModels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject

class ContactListViewModelFactory @Inject constructor(val contactListViewModel: ContactListViewModel)
    :ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ContactListViewModel::class.java)){
            return contactListViewModel as T
        }
        throw IllegalArgumentException("Unknown Class Name")
    }
}