package donank.amoveowallet.Fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import donank.amoveowallet.Api.RESTInterface
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Data.Model.ContactsModel
import donank.amoveowallet.Data.Model.ViewModels.SelectedContactsViewModel
import donank.amoveowallet.Data.WalletDao
import donank.amoveowallet.R
import donank.amoveowallet.Repositories.DBRepository
import donank.amoveowallet.Repositories.MainRepository
import donank.amoveowallet.Repositories.NetworkRepository
import donank.amoveowallet.Utility.showInSnack
import kotlinx.android.synthetic.main.fragment_selected_contact.*
import kotlinx.android.synthetic.main.fragment_watch.*
import javax.inject.Inject

class SelectedContact : Fragment() {

    @Inject
    lateinit var walletDao: WalletDao
    @Inject
    lateinit var restInterface: RESTInterface
    lateinit var mainRepository: MainRepository

    lateinit var selectedContactsViewModel: SelectedContactsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as MainApplication).component.inject(this)
        mainRepository = MainRepository(DBRepository(walletDao), NetworkRepository(restInterface))
        selectedContactsViewModel = ViewModelProviders.of(activity!!).get(SelectedContactsViewModel::class.java)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_selected_contact,container,false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        selectedContactsViewModel.getSelected().observe(this@SelectedContact,Observer<ContactsModel>{
            edit_selected_contact_address.setText(it!!.pubkey)
            edit_selected_contact_name.setText(it.name)
        })

        save_contact_btn.setOnClickListener {
            when{
                edit_selected_contact_name.text.isEmpty() -> showInSnack(this.view!!,"Contact Name is Empty")
                edit_selected_contact_address.text.isEmpty() -> showInSnack(this.view!!,"Contact Address is Empty")
                else ->{
                    val name = edit_selected_contact_name.text.toString()
                    val address = edit_watch_account_address.text.toString()
                    mainRepository.saveContactToDb(ContactsModel(name,address))
                }
            }
        }
    }
}