package donank.amoveowallet.Fragments

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableArrayList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.nitrico.lastadapter.LastAdapter
import donank.amoveowallet.Api.RESTInterface
import donank.amoveowallet.BR
import donank.amoveowallet.Repositories.CryptoRepository
import donank.amoveowallet.Common.showFragment
import donank.amoveowallet.Common.showInSnack
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Data.AppPref
import donank.amoveowallet.Data.WalletDao
import donank.amoveowallet.R
import kotlinx.android.synthetic.main.fragment_dashboard.*
import donank.amoveowallet.Data.Model.Wallet
import donank.amoveowallet.Data.Model.WalletType
import donank.amoveowallet.Data.Model.ViewModels.WalletListViewModel
import donank.amoveowallet.databinding.ItemWalletBinding
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_watch.*
import javax.inject.Inject

class Dashboard : Fragment() {

    private val TAG = Dashboard::class.simpleName

    private val wallets = ObservableArrayList<Wallet>()
    private val lastAdapter: LastAdapter by lazy { initLastAdapter() }
    //private val watchlastAdapter: LastAdapter by lazy { initLastAdapter(watch_address_recycler) }

    @Inject
    lateinit var restInterface: RESTInterface

    @Inject lateinit var walletDao: WalletDao

    //val dbRepository =  DBRepository(walletDao)

    //val networkRepository = NetworkRepository(restInterface)

    val cryptoRepository = CryptoRepository()

    private val walletViewModel = ViewModelProviders.of(this@Dashboard).get(WalletListViewModel::class.java)

    fun initLastAdapter(): LastAdapter {
        return LastAdapter(wallets, BR.item)
                .map<Wallet, ItemWalletBinding>(R.layout.item_wallet) {
                    onBind {
                        it.itemView.setOnClickListener { _ ->
                            AppPref.currentWalletId = it.binding.item!!.id
                            showFragment(
                                    Fragment.instantiate(
                                            activity,
                                            Wallet::class.java.name
                                    ),
                                    addToBackStack = false
                            )
                        }
                        val addrLen = it.binding.item!!.address.length
                        val first4 = it.binding.item!!.address.substring(0, 4)
                        val last4 = it.binding.item!!.address.substring(addrLen - 4, addrLen)
                        // it.itemView.tv_wallet_address.text = first4.plus("...").plus(last4)
                    }
                }
                .into(wallet_recycler)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as MainApplication).component.inject(this)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        wallet_recycler.layoutManager = LinearLayoutManager(activity)
        wallet_recycler.adapter = lastAdapter

        //watch_address_recycler.adapter = watchlastAdapter
        //watch_address_recycler.layoutManager = LinearLayoutManager(activity)

        /*add_account_btn.setOnClickListener {
            DashboardBottomSheet().show(fragmentManager, "Menu")
        } */

        bottom_navigation.setOnNavigationItemSelectedListener(
                { item ->
                    when (item.itemId) {
                        R.id.action_generate->showFragment(
                                Fragment.instantiate(
                                        activity,
                                        GenerateWallet::class.java.name
                                ),
                                addToBackStack = true
                        )
                        R.id.action_import->showFragment(
                                Fragment.instantiate(
                                        activity,
                                        ImportWallet::class.java.name
                                ),
                                addToBackStack = true
                        )
                        R.id.action_watch->showFragment(
                                Fragment.instantiate(
                                        activity,
                                        WatchWallet::class.java.name
                                ),
                                addToBackStack = true
                        )
                    }
                    true
                })

    }


    fun addWatchAddressToList() {
        val inputName = edit_watch_account_name.text.toString()
        val walletType = WalletType.WATCH
        val inputAddress = edit_watch_account_address.text.toString().replace("\\s+", "")
        val valid = cryptoRepository.validateAddress(inputAddress)
        val inputPassword = ""
        if (valid) {

            val address = Wallet(
                    address = inputAddress,
                    value = 0,
                    name = inputName,
                    password = inputPassword,
                    type = walletType
            )
            wallets.add(address)
            lastAdapter.notifyDataSetChanged()
            //getAddressValue(address)
            //saveAddressToDb(address)
        } else {
            showInSnack(this.view!!, "Invalid Address format")
        }
    }

    fun getAddressValue(wallet: Wallet) {
        val command = """["account","${wallet.address}"]"""
        restInterface.postRequest(command)
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    val res = it.replace("\\s+", "").split(",")
                    if (res[0] == """["ok"""") {
                        wallet.value = res[2].toLong()
                        //dbRepository.update(wallet)
                        activity!!.runOnUiThread {
                            wallets.filter { it.address == wallet.address }.forEach { it.value = wallet.value }
                            lastAdapter.notifyDataSetChanged()
                        }
                    } else {
                        activity!!.runOnUiThread {
                            showInSnack(this.view!!, "Error loading account details!")
                        }
                    }
                }, {
                    activity!!.runOnUiThread {
                        showInSnack(this.view!!, "Error loading account details!")
                    }
                })
    }


    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.fragment_container,
                fragmentManager = activity!!.supportFragmentManager,
                addToBackStack = addToBackStack)
    }
}