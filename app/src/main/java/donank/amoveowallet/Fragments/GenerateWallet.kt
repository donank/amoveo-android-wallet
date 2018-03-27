package donank.amoveowallet.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import donank.amoveowallet.Api.RESTInterface
import donank.amoveowallet.Utility.showFragment
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Data.Model.Wallet
import donank.amoveowallet.Data.Model.WalletType
import donank.amoveowallet.Data.WalletDao
import donank.amoveowallet.R
import donank.amoveowallet.Repositories.CryptoRepository
import donank.amoveowallet.Repositories.DBRepository
import donank.amoveowallet.Repositories.MainRepository
import donank.amoveowallet.Repositories.NetworkRepository
import kotlinx.android.synthetic.main.fragment_generate.*
import kotlinx.android.synthetic.main.fragment_generate.view.*
import org.spongycastle.util.encoders.Base64
import javax.inject.Inject

class GenerateWallet : Fragment() {

    val cryptoHelper = CryptoRepository()
    @Inject lateinit var walletDao: WalletDao
    @Inject lateinit var restInterface: RESTInterface

    lateinit var mainRepository: MainRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as MainApplication).component.inject(this)
        mainRepository = MainRepository(DBRepository(walletDao), NetworkRepository(restInterface))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_generate, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        edit_generate_account_name.setText("Wallet".plus(mainRepository.getWalletCountFromDb() + 1))

        generate_new_btn.setOnClickListener {

            val keyPair = cryptoHelper.genKeyPair()
            tv_generate_account_privkey.text = cryptoHelper.toHex(keyPair.first.d.toByteArray())
            var pub64 = ""
            try{
                pub64 = Base64.toBase64String(keyPair.second.q.getEncoded(false))
            }catch (e: Exception){
                Log.d("toBase64",e.message)
            }
            tv_generate_account_pubkey.text = pub64
            generate_submit_btn.isEnabled = true

        }

        generate_submit_btn.setOnClickListener {
            generate_submit_btn.isEnabled = false
            mainRepository.saveWalletToDb(Wallet(
                    tv_generate_account_pubkey.text.toString().replace("\\s",""),
                    0,
                    edit_generate_account_name.text.toString(),
                    WalletType.SINGLE,
                    tv_generate_account_privkey.text.toString().replace("\\s","")
            ))
        }
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.fragment_container,
                fragmentManager = activity!!.supportFragmentManager,
                addToBackStack = addToBackStack)
    }
}