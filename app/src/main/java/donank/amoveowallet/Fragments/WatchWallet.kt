package donank.amoveowallet.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import donank.amoveowallet.Api.RESTInterface
import donank.amoveowallet.Utility.showFragment
import donank.amoveowallet.Utility.showInSnack
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Data.Model.WalletModel
import donank.amoveowallet.Data.Model.WalletType
import donank.amoveowallet.Data.WalletDao
import donank.amoveowallet.R
import donank.amoveowallet.Repositories.CryptoRepository
import donank.amoveowallet.Repositories.DBRepository
import donank.amoveowallet.Repositories.MainRepository
import donank.amoveowallet.Repositories.NetworkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_watch.*
import javax.inject.Inject

class WatchWallet : Fragment() {

    @Inject
    lateinit var walletDao: WalletDao
    @Inject
    lateinit var restInterface: RESTInterface
    lateinit var mainRepository: MainRepository

    val cryptoRepository = CryptoRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as MainApplication).component.inject(this)
        mainRepository = MainRepository(DBRepository(walletDao), NetworkRepository(restInterface))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_watch, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainRepository.getWalletCountFromDb()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    edit_watch_account_name.setText("Wallet".plus(it + 1))
                }

        watch_verify_btn.setOnClickListener {
            when {
                edit_watch_account_address.text.isEmpty() -> {
                    showInSnack(this.view!!, "Input Address is empty")
                }
                else -> {
                    val address = edit_watch_account_address.text.toString()
                    val valid = cryptoRepository.validateAddress(address)
                    if(valid){
                        getWalletDetails(address)
                        watch_save_btn.isEnabled = true
                    }else{
                        showInSnack(this.view!!, "Invalid Address.")
                    }
                }
            }
        }

        watch_save_btn.setOnClickListener {
            watch_save_btn.isEnabled = false
            save(
                    WalletModel(
                            tv_watch_address.text.toString(),
                            (tv_watch_value.text.toString().toDouble() * 100000000).toLong(),
                            edit_watch_account_name.text.toString(),
                            WalletType.WATCH,
                            ""
                    )
            )
        }

    }

    fun getWalletDetails(inputAddress : String){
        val command = "[\"account\",\"$inputAddress\"]"
        mainRepository.request(command).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val res = it.replace("\\s+","").split(",")
                    if(res[0] == "[\"ok\""){
                        tv_watch_address.text = inputAddress
                        tv_watch_valid.text = "true"
                        tv_watch_value.text = (res[2].toLong()/100000000.0).toString()
                    }else{
                        showInSnack(this.view!!,"Invalid Address.")
                    }
                }
    }

    fun save(walletModel: WalletModel){
        mainRepository.saveWalletToDb(walletModel)
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.fragment_container,
                fragmentManager = activity!!.supportFragmentManager,
                addToBackStack = addToBackStack)
    }
}