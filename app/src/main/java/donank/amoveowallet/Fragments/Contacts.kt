package donank.amoveowallet.Fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableArrayList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.nitrico.lastadapter.LastAdapter
import donank.amoveowallet.BR
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Data.Model.ContactsModel
import donank.amoveowallet.Data.Model.ViewModels.ContactListViewModel
import donank.amoveowallet.Data.Model.ViewModels.ContactListViewModelFactory
import donank.amoveowallet.Data.Model.ViewModels.SelectedContactsViewModel
import donank.amoveowallet.R
import donank.amoveowallet.Utility.showFragment
import donank.amoveowallet.Utility.showInSnack
import donank.amoveowallet.databinding.ItemContactBinding
import kotlinx.android.synthetic.main.fragment_contacts.*
import javax.inject.Inject

class Contacts : Fragment() {

    private val contacts = ObservableArrayList<ContactsModel>()
    private val lastAdapter: LastAdapter by lazy { initLastAdapter() }

    @Inject
    lateinit var contactListViewModelFactory: ContactListViewModelFactory
    lateinit var contactListViewModel: ContactListViewModel
    lateinit var selectedContactsModel: SelectedContactsViewModel

    fun initLastAdapter():LastAdapter{
        return LastAdapter(contacts, BR.item)
                .map<ContactsModel, ItemContactBinding>(R.layout.item_contact)
                {
                    onBind {
                        it.itemView.setOnClickListener {_->
                            selectedContactsModel.select(it.binding.item)
                            showFragment(
                                    Fragment.instantiate(
                                            activity,
                                            SelectedContact::class.java.name
                                    ),
                                    addToBackStack = true
                            )
                        }
                    }
                }
                .into(contacts_recycler)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as MainApplication).component.inject(this)

        contactListViewModel = ViewModelProviders.of(activity!!,contactListViewModelFactory).get(ContactListViewModel::class.java)
        contactListViewModel.loadContacts()

        contactListViewModel.contactsResult().observe(this@Contacts,Observer<List<ContactsModel>>{
            contacts.clear()
            contacts.addAll(it!!)
            lastAdapter.notifyDataSetChanged()
        })

        contactListViewModel.contactsError().observe(this@Contacts,Observer<String>{
            showInSnack(this.view!!,"Error while retrieving contacts from db")
        })

        selectedContactsModel = ViewModelProviders.of(activity!!).get(SelectedContactsViewModel::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_contacts,container,false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        contacts_recycler.adapter = lastAdapter
        contacts_recycler.layoutManager = LinearLayoutManager(activity)

        fab_add_contact.setOnClickListener {
            selectedContactsModel.select(ContactsModel("",""))
            showFragment(
                    Fragment.instantiate(
                            activity,
                            SelectedContact::class.java.name
                    ),
                    addToBackStack = true
            )
        }
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.fragment_container,
                fragmentManager = activity!!.supportFragmentManager,
                addToBackStack = addToBackStack)
    }

    override fun onDestroy() {
        contactListViewModel.disposeElements()
        super.onDestroy()
    }
}