package donank.amoveowallet.Fragments

import android.databinding.ObservableArrayList
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Base64
import android.util.Base64.DEFAULT
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.github.nitrico.lastadapter.LastAdapter
import donank.amoveowallet.Api.RESTInterface
import donank.amoveowallet.BR
import donank.amoveowallet.Common.showFragment
import donank.amoveowallet.Common.showInSnack
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Data.WalletDao
import donank.amoveowallet.R
import kotlinx.android.synthetic.main.fragment_dashboard.*
import donank.amoveowallet.Data.Model.Wallet
import donank.amoveowallet.Data.WalletType
import donank.amoveowallet.databinding.ItemWalletBinding
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class Dashboard : Fragment() {

    private val wallets = ObservableArrayList<Wallet>()
    private val lastAdapter: LastAdapter by lazy { initLastAdapter() }

    @Inject
    lateinit var walletDao: WalletDao

    @Inject
    lateinit var restInterface: RESTInterface


    fun initLastAdapter() : LastAdapter{
        return LastAdapter(wallets, BR.item)
                .map<Wallet, ItemWalletBinding>(R.layout.item_wallet){
                    onBind {
                        it.itemView.setOnClickListener {
                            showFragment(
                                    Fragment.instantiate(
                                            activity,
                                            Wallet::class.java.name
                                    ),
                                    addToBackStack = false
                            )
                        }
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

        add_account_btn.setOnClickListener {
            showAddressInputView()
        }
        submit_account_btn.setOnClickListener {
            when {
                edit_account_address.text.isEmpty() -> {
                    hideAddressInputView()
                    showInSnack(this.view!!,"Input Address is empty")
                }
                edit_account_address.text.length % 4 != 0 -> {
                    hideAddressInputView()
                    showInSnack(this.view!!,"Input Address is invalid")
                }
                else -> addAddressToList()
            }
        }
        account_cancel_btn.setOnClickListener {
            hideAddressInputView()
        }

        choose_account_type.setOnCheckedChangeListener{ compoundButton: CompoundButton, b: Boolean ->
            if(b){
                edit_account_password.visibility = View.GONE
            }else{
                edit_account_password.visibility = View.VISIBLE
            }
        }
    }

    fun showAddressInputView(){
        var count = ""
        AsyncTask.execute {
            count = walletDao.getWalletCount().toString()
        }
        edit_account_name.setText("Wallet".plus(count + 1))
        add_account_btn.visibility = View.GONE
        add_account_layout.visibility = View.VISIBLE

    }

    fun hideAddressInputView(){
        add_account_layout.visibility = View.GONE
        add_account_btn.visibility = View.VISIBLE
    }

    fun addAddressToList(){
        hideAddressInputView()
        val inputName = edit_account_name.text.toString()
        val walletType = if(choose_account_type.isChecked) WalletType.WATCH
                            else WalletType.SINGLE
        val inputAddress = edit_account_address.text.toString().replace("\\s+","")
        val inputPassword = edit_account_password.text.toString().replace("\\s+","")
        val valid = validateAddress(inputAddress)
        if(valid){

            val address = Wallet(
                    address = inputAddress,
                    value = 0,
                    name = inputName,
                    password = inputPassword,
                    type = walletType
            )
            wallets.add(address)
            lastAdapter.notifyDataSetChanged()
            getAddressValue(address)
            saveAddressToDb(address)
        }else{
            showInSnack(this.view!!,"Invalid Address format")
        }
    }

    fun getAddressValue(wallet : Wallet) {
        val command = """["account","${wallet.address}"]"""
        restInterface.postRequest(command)
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    val res = it.replace("\\s+","").split(",")
                    if(res[0] == """["ok""""){
                        wallet.value = res[2].toLong()
                        walletDao.update(wallet)
                        activity!!.runOnUiThread {
                            wallets.filter { it.address == wallet.address }.forEach { it.value = wallet.value }
                            lastAdapter.notifyDataSetChanged()
                        }
                    }else{
                        activity!!.runOnUiThread {
                            showInSnack(this.view!!,"Error loading account details!")
                        }
                    }
                },{
                    activity!!.runOnUiThread {
                        //showInSnack("Error loading account details!")
                    }
                })
    }


    fun validateAddress(address: String): Boolean{

        var addressIsValid = false
        return if(!address.isEmpty()){
            try {
                Base64.decode(address,DEFAULT)
            }catch (e: Exception){
                addressIsValid = false
            }
            addressIsValid
        }else{
            false
        }
    }

    fun saveAddressToDb(wallet : Wallet){
        AsyncTask.execute {
            walletDao.save(wallet)
        }
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.fragment_container,
                fragmentManager = activity!!.supportFragmentManager,
                addToBackStack = addToBackStack)
    }
}