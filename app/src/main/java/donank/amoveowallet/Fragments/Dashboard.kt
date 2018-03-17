package donank.amoveowallet.Fragments

import android.content.Intent
import android.databinding.ObservableArrayList
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.util.Base64.DEFAULT
import android.util.Log
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
import donank.amoveowallet.Data.Model.WalletType
import donank.amoveowallet.databinding.ItemWalletBinding
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dashboard_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_wallet.view.*
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.spec.ECGenParameterSpec
import javax.inject.Inject

class Dashboard : Fragment() {

    private val TAG = Dashboard::class.simpleName

    private val wallets = ObservableArrayList<Wallet>()
    private val lastAdapter: LastAdapter by lazy { initLastAdapter() }
    //private val watchlastAdapter: LastAdapter by lazy { initLastAdapter(watch_address_recycler) }

    @Inject
    lateinit var walletDao: WalletDao

    @Inject
    lateinit var restInterface: RESTInterface

//    val keyPairGen = KeyPairGenerator.getInstance("EC", "SunEC")

    //val ecsp = ECGenParameterSpec("secp256k1")

    private val REQUEST_PICK_FILE = 1


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
                        val addrLen = it.binding.item!!.address.length
                        val first4 = it.binding.item!!.address.substring(0,4)
                        val last4 = it.binding.item!!.address.substring(addrLen-4, addrLen)
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

        add_account_btn.setOnClickListener {
            showFragment(
                    instantiate(
                            activity,
                            DashboardBottomSheet::class.java.name
                            ),
                    true
            )
        }

        //watch wallet click events

        watch_submit_btn.setOnClickListener {
            when {
                edit_watch_account_address.text.isEmpty() -> {
                    hideWatchAddressFormView()
                    showInSnack(this.view!!,"Input Address is empty")
                }
                else -> addWatchAddressToList()
            }
        }
        watch_cancel_btn.setOnClickListener {
            hideWatchAddressFormView()
        }

        //import wallet click events

        select_priv_key_file.setOnClickListener {
            selectPrivKeyFile()
        }
        save_import_account_btn_.setOnClickListener {
            when{
                edit_import_account_password.text.isEmpty() -> {
                    hideImportWalletFormView()
                    showInSnack(this.view!!,"Input Private Key is empty")
                }
                else -> addWatchAddressToList()
            }
        }
        import_cancel_btn.setOnClickListener {
            hideImportWalletFormView()
        }

        //generate wallet click events


    }

    //watch wallet view functions

    fun showWatchAddressFormView(){
        edit_watch_account_name.setText("Wallet".plus(getWalletCountFromDb() + 1))
        add_account_btn.visibility = View.GONE
        watch_address_layout.visibility = View.VISIBLE

    }

    fun hideWatchAddressFormView(){
        watch_address_layout.visibility = View.GONE
        add_account_btn.visibility = View.VISIBLE
    }

    //import wallet view functions

    fun showImportWalletFormView(){
        edit_import_account_name.setText("Wallet".plus(getWalletCountFromDb() + 1))
        add_account_btn.visibility = View.GONE
        import_account_layout.visibility = View.GONE
    }

    fun hideImportWalletFormView(){
        import_account_layout.visibility = View.GONE
        add_account_btn.visibility = View.VISIBLE
    }

    //generate wallet view functions

    fun showGenerateWalletFormView(){
        edit_generate_account_name.setText("Wallet".plus(getWalletCountFromDb() + 1))
        add_account_btn.visibility = View.GONE
        generate_account_layout.visibility = View.VISIBLE
    }

    fun hideGenerateWalletFormView(){
        generate_account_layout.visibility = View.GONE
        add_account_btn.visibility = View.VISIBLE
    }

    fun addWatchAddressToList(){
        hideWatchAddressFormView()
        val inputName = edit_watch_account_name.text.toString()
        val walletType =  WalletType.WATCH
        val inputAddress = edit_watch_account_address.text.toString().replace("\\s+","")
        val valid = validateAddress(inputAddress)
        val inputPassword = ""
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
            //getAddressValue(address)
            //saveAddressToDb(address)
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
                        showInSnack(this.view!!,"Error loading account details!")
                    }
                })
    }


    fun validateAddress(address: String): Boolean{
        return if(!address.isEmpty()){
            try {
                Base64.decode(address,DEFAULT)
            }catch (e: Exception){
                false
            }
            true
        }else{
            false
        }
    }


/*
    fun genKeyPair(salt: String?): KeyPair{
        keyPairGen.initialize(ecsp)
        return if(!salt.isNullOrEmpty()) {
            keyPairGen.genKeyPair()
        }else{
            keyPairGen.genKeyPair()
        }
    }
*/
    fun selectPrivKeyFile(){
        try {
            startActivityForResult(
                    Intent(Intent.ACTION_GET_CONTENT)
                            .setType("*/*"),
                    REQUEST_PICK_FILE)
        }catch (e : Exception){
            showInSnack(this.view!!, "Error! No File Manager Found")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == 1){
            when(requestCode){
                REQUEST_PICK_FILE ->{
                    if(data != null){
                        val textData = data.dataString
                        Log.d(TAG,"Received data $data")
                    }
                }
            }
        }
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.fragment_container,
                fragmentManager = activity!!.supportFragmentManager,
                addToBackStack = addToBackStack)
    }

    //todo make a separate dbrepository
    fun saveAddressToDb(wallet : Wallet){
        AsyncTask.execute {
            walletDao.save(wallet)
        }
    }

    fun getWalletCountFromDb(): String{
        var count = ""
        AsyncTask.execute {
            count = walletDao.getWalletCount().toString()
        }
        return count
    }
}