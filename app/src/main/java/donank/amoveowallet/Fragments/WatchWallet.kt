package donank.amoveowallet.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import donank.amoveowallet.Common.showInSnack
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Data.Model.Wallet
import donank.amoveowallet.Data.Model.WalletType
import donank.amoveowallet.Data.WalletDao
import donank.amoveowallet.R
import donank.amoveowallet.Repositories.CryptoRepository
import donank.amoveowallet.Repositories.DBRepository
import kotlinx.android.synthetic.main.fragment_watch.*
import javax.inject.Inject

class WatchWallet : Fragment() {

    @Inject lateinit var walletDao: WalletDao

    val dbRepository = DBRepository(walletDao)

    val cryptoRepository = CryptoRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as MainApplication).component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_watch, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        edit_watch_account_name.setText("Wallet".plus(dbRepository.getWalletCountFromDb() + 1))

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

        }

    }

    fun validateAndSave(inputName : String, walletType: WalletType, inputAddress : String) {
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
        } else {
            showInSnack(this.view!!, "Invalid Address format")
        }
    }
}