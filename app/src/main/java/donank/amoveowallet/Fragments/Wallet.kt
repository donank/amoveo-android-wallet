package donank.amoveowallet.Fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableArrayList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.nitrico.lastadapter.LastAdapter
import donank.amoveowallet.BR
import donank.amoveowallet.Utility.showFragment
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Data.Model.TransactionModel
import donank.amoveowallet.Data.Model.ViewModels.SelectedWalletViewModel
import donank.amoveowallet.Data.Model.WalletModel
import donank.amoveowallet.Data.Model.WalletType
import donank.amoveowallet.R
import donank.amoveowallet.databinding.ItemTransactionBinding
import kotlinx.android.synthetic.main.fragment_wallet.*

class Wallet : Fragment() {

    private val transactions = ObservableArrayList<TransactionModel>()
    private val lastAdapter: LastAdapter by lazy { initLastAdapter() }
    lateinit var selectedWalletViewModel: SelectedWalletViewModel

    fun initLastAdapter() : LastAdapter{
        return LastAdapter(transactions, BR.item)
                .map<TransactionModel, ItemTransactionBinding>(R.layout.item_transaction)
                .into(transactions_recycler)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as MainApplication).component.inject(this)

        selectedWalletViewModel = ViewModelProviders.of(activity!!).get(SelectedWalletViewModel::class.java)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_wallet,container,false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        selectedWalletViewModel.getSelected().observe(this,Observer<WalletModel>{
            if(it!!.type == WalletType.WATCH){
                bottom_navigation_wallet.visibility = View.GONE
            }
            tv_wallet_address.text = it.address
            tv_wallet_value.text = it.value.toString()
        })

        bottom_navigation_wallet.setOnNavigationItemSelectedListener(
                { item ->
                    when (item.itemId) {
                        R.id.action_send->showFragment(
                                Fragment.instantiate(
                                        activity,
                                        Send::class.java.name
                                ),
                                addToBackStack = true
                        )
                        R.id.action_receive->showFragment(
                                Fragment.instantiate(
                                        activity,
                                        Receive::class.java.name
                                ),
                                addToBackStack = true
                        )
                    }
                    true
                })
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.fragment_container,
                fragmentManager = activity!!.supportFragmentManager,
                addToBackStack = addToBackStack)
    }

}