package donank.amoveowallet.Fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import donank.amoveowallet.Api.RESTInterface
import donank.amoveowallet.Utility.showFragment
import donank.amoveowallet.Utility.showInSnack
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Data.WalletDao
import donank.amoveowallet.R
import donank.amoveowallet.Repositories.DBRepository
import donank.amoveowallet.Repositories.MainRepository
import donank.amoveowallet.Repositories.NetworkRepository
import kotlinx.android.synthetic.main.fragment_import.*
import javax.inject.Inject

class ImportWallet : Fragment() {

    @Inject lateinit var walletDao: WalletDao
    @Inject lateinit var restInterface: RESTInterface
    lateinit var mainRepository: MainRepository

    private val REQUEST_PICK_FILE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as MainApplication).component.inject(this)
        mainRepository = MainRepository(DBRepository(walletDao), NetworkRepository(restInterface))

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_import, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        edit_import_account_name.setText("Wallet".plus(mainRepository.getWalletCountFromDb() + 1))

        select_priv_key_file.setOnClickListener {
            selectPrivKeyFile()
        }

        save_import_account_btn_.setOnClickListener {
            when {
                edit_import_account_password.text.isEmpty() -> {
                    showInSnack(this.view!!, "Input Private Key is empty")
                }
                else -> {

                }
            }
        }
        import_cancel_btn.setOnClickListener {
            showFragment(
                    Fragment.instantiate(
                            activity,
                            Dashboard::class.java.name
                    ),
                    false
            )
        }
    }

    fun selectPrivKeyFile() {
        try {
            startActivityForResult(
                    Intent(Intent.ACTION_GET_CONTENT)
                            .setType("*/*"),
                    REQUEST_PICK_FILE)
        } catch (e: Exception) {
            showInSnack(this.view!!, "Error! No File Manager Found")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 1) {
            when (requestCode) {
                REQUEST_PICK_FILE -> {
                    if (data != null) {
                        val textData = data.dataString
                        Log.d("PRIV-KEY-FILE", "Received data $data")
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
}