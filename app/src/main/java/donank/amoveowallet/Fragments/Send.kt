package donank.amoveowallet.Fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import donank.amoveowallet.Api.RESTInterface
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Utility.showFragment
import donank.amoveowallet.Data.Model.ViewModels.SelectedWalletViewModel
import donank.amoveowallet.Data.Model.WalletModel
import donank.amoveowallet.Data.WalletDao
import donank.amoveowallet.R
import donank.amoveowallet.Repositories.CryptoRepository
import donank.amoveowallet.Repositories.DBRepository
import donank.amoveowallet.Repositories.MainRepository
import donank.amoveowallet.Repositories.NetworkRepository
import donank.amoveowallet.Utility.showInSnack
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_send.*
import javax.inject.Inject

class Send : Fragment() {

    val cryptoRepository = CryptoRepository()
    @Inject
    lateinit var walletDao: WalletDao
    @Inject
    lateinit var restInterface: RESTInterface

    lateinit var walletModel: SelectedWalletViewModel
    lateinit var mainRepository: MainRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as MainApplication).component.inject(this)
        mainRepository = MainRepository(DBRepository(walletDao), NetworkRepository(restInterface))
        walletModel = ViewModelProviders.of(activity!!).get(SelectedWalletViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_send, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /*
        * ["ok",
        * ["spend",
        * "BKdkHXUeBIgzqyQ0morfNcw2AKIc/n1NAt0pK34ESnaC62mpSSMAqMsArWIqcyWWACdIL9r82UhnuUJIbueRH04=",
        * 2023,
        * 1000000,
        * "BDGmaFo6kpzQBRrrnqLjWOuzfqtpd+GqPB3kOQjkoKRdj8fCWW22MBt4zhtaCOqhQQofKL7dfZkdpbDRrb+wWhE=",
        * 100000000,0]]
        *
        * ["ok",["create_acc_tx",
        * "BDvZdZiVhaTdoCBv6lSyBv6vjhnTMj420bVFEI8slw0pCgb5fGfE5YqGJQlXFqyLVf/xZIElXIlk7TSWdGKZrL0=",
        * 1,
        * 152050,
        * "BDsdrsGQjzfqLn5id3qIOZxyjtQ5S3lzCGYKKSaccxa7U0wL7UdxNVMdswcmssG9ZQ0PZ3u+qkAx6WIWEFTgCwM=",
        * 10000000]]
        */

        edit_send_addres.setText("BKdkHXUeBIgzqyQ0morfNcw2AKIc/n1NAt0pK34ESnaC62mpSSMAqMsArWIqcyWWACdIL9r82UhnuUJIbueRH04=")
        edit_send_amount.setText("4000000")
        walletModel.getSelected().observe(this, Observer<WalletModel>{
            send_btn.setOnClickListener {v->
                val to = edit_send_addres.text.toString()
                val amount = edit_send_amount.text.toString().toLong()
                val fee = 152050L
                val commandCreateAccountTx = "[\"create_account_tx\", $amount, $fee, \"${it!!.address}\", \"$to\"]"
                val commandSpendTx = "[\"spend_tx\", $amount, $fee, \"${it.address}\", \"$to\"]"
                val existingAddressCommand = "[\"account\",\"$to\"]"
                mainRepository.request(existingAddressCommand)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe({s ->
                            if(s == "[\"ok\",\"empty\"]"){
                                mainRepository.request(commandCreateAccountTx)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(AndroidSchedulers.mainThread())
                                        .subscribe({res ->
                                            val res1 = res.split(",")
                                            if(res1[0] == "[\"ok\""){
                                                Log.d("CREATE ACC RES","$res1")
                                                val resAmount = res1[6].split("]")[0].toLong()
                                                val resToAddress = res1[5].split("\"")[1]
                                                val resFee = res1[4].toLong()
                                                val resTxHeight = res1[3].toLong()

                                                Log.d("resAmount","$resAmount")
                                                Log.d("resToAddress","$resToAddress")
                                                Log.d("resFee","$resFee")
                                                Log.d("resTxHeight","$resTxHeight")



                                                when{
                                                    resAmount != amount -> {
                                                        showInSnack(this.view!!,"Abort!Server Changed Amount")
                                                    }
                                                    resToAddress != to -> {
                                                        showInSnack(this.view!!,"Abort!Server Changed changed the Recepient Adress")
                                                    }
                                                    resFee != fee -> {
                                                        showInSnack(this.view!!,"Abort!Server Changed the Fee")
                                                    }
                                                    else ->{
                                                        spendTokens(listOf("create_acc_tx",it.address,resTxHeight,fee,to,amount),it.password)
                                                    }
                                                }
                                            }

                                        },{
                                            showInSnack(this.view!!,"Error while requesting transaction info")
                                            Log.d("CREATE ACC ERR","${it.message}")
                                        })
                            }else{
                                mainRepository.request(commandSpendTx)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(AndroidSchedulers.mainThread())
                                        .subscribe({res->
                                            Log.d("SPEND TX RES","$it")
                                            val res1 = res.split(",")
                                            if(res1[0] == "[\"ok\""){
                                                Log.d("SPEND TX RES","$res1")
                                                val resAmount = res1[6].split("]")[0].toLong()
                                                val resToAddress = res1[5].split("\"")[1]
                                                val resFee = res1[4].toLong()
                                                val resTxHeight = res1[3].toLong()

                                                Log.d("resAmount","$resAmount")
                                                Log.d("resToAddress","$resToAddress")
                                                Log.d("resFee","$resFee")
                                                Log.d("resTxHeight","$resTxHeight")

                                                when{
                                                    resAmount != amount -> {
                                                        showInSnack(this.view!!,"Abort!Server Changed the Amount")
                                                    }
                                                    resToAddress != to -> {
                                                        showInSnack(this.view!!,"Abort!Server Changed the Recepient Adress")
                                                    }
                                                    resFee != fee -> {
                                                        showInSnack(this.view!!,"Abort!Server Changed the Fee")
                                                    }
                                                    else ->{
                                                        spendTokens(listOf("spend_tx",it.address,resTxHeight,fee,to,amount),it.password)
                                                    }
                                                }
                                            }
                                        },{
                                            showInSnack(this.view!!,"Error while requesting transaction info")
                                            Log.d("SPEND TX ERR","${it.message}")

                                        })
                            }
                        },{
                            showInSnack(this.view!!,"Error while fetching account information")
                            Log.d("TEST ACC ERR","${it.message}")
                        })
            }
        })

        cance_send_btn.setOnClickListener {
            showFragment(
                    Fragment.instantiate(
                            activity,
                            Wallet::class.java.name
                    ),
                    addToBackStack = true
            )
        }

        scan_address_btn.setOnClickListener {

        }
    }

    fun spendTokens(tx: List<Any>,privateKey : String) {
        Log.d("Spend","$tx")
        Log.d("privateKey",privateKey)
        val privKey = cryptoRepository.decrypt(privateKey)
        Log.d("privKey",privKey)

        val signedTx = cryptoRepository.generateTransaction(tx,privKey)
        Log.d("SIGNED TX",signedTx)
        val publishTxCommand = "[\"txs\", [-6, $signedTx]]"
        //mainRepository.request(publishTxCommand)
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.fragment_container,
                fragmentManager = activity!!.supportFragmentManager,
                addToBackStack = addToBackStack)
    }

}