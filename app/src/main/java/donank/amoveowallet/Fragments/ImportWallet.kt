package donank.amoveowallet.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import donank.amoveowallet.Data.Model.WalletModel
import donank.amoveowallet.Data.Model.WalletType
import donank.amoveowallet.Data.WalletDao
import donank.amoveowallet.R
import donank.amoveowallet.Repositories.CryptoRepository
import donank.amoveowallet.Repositories.DBRepository
import donank.amoveowallet.Repositories.MainRepository
import donank.amoveowallet.Repositories.NetworkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_import.*
import javax.inject.Inject
import java.io.BufferedReader
import java.io.InputStreamReader


class ImportWallet : Fragment() {

    @Inject
    lateinit var walletDao: WalletDao
    @Inject
    lateinit var restInterface: RESTInterface
    lateinit var mainRepository: MainRepository

    val cryptoRepository = CryptoRepository()

    private val REQUEST_PICK_FILE = 1

    var walletValue = 0L
    var walletPrivKey = ""
    var walletPubKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as MainApplication).component.inject(this)
        mainRepository = MainRepository(DBRepository(walletDao), NetworkRepository(restInterface))

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_import, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        mainRepository.getWalletCountFromDb()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    edit_import_account_name.setText("WalletModel".plus(it + 1))
                }

        select_priv_key_file.setOnClickListener {
            selectPrivKeyFile()
        }

        import_account_btn.setOnClickListener {
            when {
                edit_import_account_password.text.isEmpty() -> {
                    showInSnack(this.view!!, "Input Private Key is empty")
                }
                else -> {
                    import_save_btn.isEnabled = true
                    walletPubKey = cryptoRepository.generatePubKey(walletPrivKey)
                    val command = "[\"account\",\"$walletPubKey\"]"
                    mainRepository.request(command)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                val res = it.replace("\\s+", "").split(",")
                                when {
                                    res[0] == "[\"ok\"" && res[1] != "\"empty\"]" -> {
                                        walletValue = res[2].toLong()
                                        tv_import_address.text = walletPubKey
                                        tv_import_value.text = (walletValue / 100000000).toString()
                                    }
                                    res[1] == "\"empty\"]" -> {
                                        walletValue = 0
                                        tv_import_address.text = walletPubKey
                                        tv_import_value.text = 0.toString()
                                    }
                                    else -> {
                                        showInSnack(this.view!!, "Error during verification.")
                                    }
                                }
                            }
                }
            }
        }
        import_save_btn.setOnClickListener {
            import_save_btn.isEnabled = false
            mainRepository.saveWalletToDb(
                    WalletModel(
                            walletPubKey,
                            walletValue,
                            edit_import_account_name.text.toString(),
                            WalletType.SINGLE,
                            walletPrivKey
                    ))
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
                            .addCategory(Intent.CATEGORY_OPENABLE)
                            .setType("*/*"),
                    REQUEST_PICK_FILE)
        } catch (e: Exception) {
            showInSnack(this.view!!, "Error! No File Manager Found")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PICK_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                walletPrivKey = readTextFromUri(data.data)
                edit_import_account_password.setText(walletPrivKey)
                if (edit_import_account_password.text.isEmpty()) {
                    showInSnack(this.view!!, "Unable to retrieve Private Key")
                }
            } else {
                showInSnack(this.view!!, "No Valid File Selected")
            }
        }
    }

    private fun readTextFromUri(uri: Uri): String {
        val inputStream = activity!!.contentResolver.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))
        var outputData = ""
        reader.readLines().map {
            outputData = it.replace("\\s+", "")
        }
        inputStream.close()
        Log.d("File Output", outputData)
        return if (outputData.isEmpty()) "" else outputData
    }


    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.fragment_container,
                fragmentManager = activity!!.supportFragmentManager,
                addToBackStack = addToBackStack)
    }
}