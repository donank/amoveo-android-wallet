package donank.amoveowallet.Data.Model.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import donank.amoveowallet.Data.Model.ContactsModel

class SelectedContactsViewModel : ViewModel() {
    private var selectedContact = MutableLiveData<ContactsModel>()

    fun select(contact: ContactsModel){
        selectedContact.value = contact
    }

    fun getSelected(): LiveData<ContactsModel> {
        return selectedContact
    }
}