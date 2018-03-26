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
import donank.amoveowallet.Data.Model.Wallet
import donank.amoveowallet.Data.Model.WalletType
import donank.amoveowallet.Data.WalletDao
import donank.amoveowallet.R
import donank.amoveowallet.Repositories.CryptoRepository
import donank.amoveowallet.Repositories.DBRepository
import donank.amoveowallet.Repositories.MainRepository
import donank.amoveowallet.Repositories.NetworkRepository
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

        edit_watch_account_name.setText("Wallet".plus(mainRepository.getWalletCountFromDb() + 1))

        watch_submit_btn.setOnClickListener {
            when {
                edit_watch_account_address.text.isEmpty() -> {
                    showInSnack(this.view!!, "Input Address is empty")
                }
                else -> validateAndSave(
                        edit_watch_account_name.text.toString(),
                        WalletType.WATCH,
                        edit_watch_account_address.text.toString().replace("\\s+", "")
                )
            }
        }
        watch_cancel_btn.setOnClickListener {
            showFragment(
                    Fragment.instantiate(
                            activity,
                            Dashboard::class.java.name
                    ),
                    false
            )
        }

    }

    fun validateAndSave(inputName : String, walletType: WalletType, inputAddress : String) {
        val valid = cryptoRepository.validateAddress(inputAddress)
        val inputPassword = ""
        if (valid) {
            mainRepository.saveWalletToDb(
                    Wallet(
                    address = inputAddress,
                    value = 0,
                    name = inputName,
                    password = inputPassword,
                    type = walletType
            ))
        } else {
            showInSnack(this.view!!, "Invalid Address format")
        }
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.fragment_container,
                fragmentManager = activity!!.supportFragmentManager,
                addToBackStack = addToBackStack)
    }
}