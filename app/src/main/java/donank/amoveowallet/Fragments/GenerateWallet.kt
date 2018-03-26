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
import donank.amoveowallet.Data.Model.WalletType
import donank.amoveowallet.Data.WalletDao
import donank.amoveowallet.R
import donank.amoveowallet.Repositories.CryptoRepository
import donank.amoveowallet.Repositories.DBRepository
import donank.amoveowallet.Repositories.MainRepository
import donank.amoveowallet.Repositories.NetworkRepository
import kotlinx.android.synthetic.main.fragment_generate.*
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
            val pub64 = Base64.toBase64String(keyPair.second.q.getEncoded(false),0,88)
            tv_generate_account_pubkey.text = pub64.toString()
            generate_new_btn.visibility = View.GONE
            generate_submit_btn.visibility = View.VISIBLE

            Log.d("PrivKeyHex",tv_generate_account_privkey.text.toString())
            Log.d("PrivKeyBA","${keyPair.first.d}")
            Log.d("Pubkey","${keyPair.second.q.getEncoded(false)}")
            Log.d("Pubkey64","$pub64")

        }

        generate_submit_btn.setOnClickListener {
            mainRepository.saveWalletToDb(donank.amoveowallet.Data.Model.Wallet(
                    tv_generate_account_pubkey.toString().replace("\\s",""),
                    0,
                    edit_generate_account_name.toString(),
                    WalletType.SINGLE,
                    tv_generate_account_privkey.toString().replace("\\s","")
            ))
        }

        generate_cancel_btn.setOnClickListener {
            showFragment(
                    Fragment.instantiate(
                            activity,
                        Dashboard::class.java.name
                    ),
                    false
            )
        }
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.fragment_container,
                fragmentManager = activity!!.supportFragmentManager,
                addToBackStack = addToBackStack)
    }
}