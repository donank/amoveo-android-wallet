package donank.amoveowallet.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import donank.amoveowallet.Api.RESTInterface
import donank.amoveowallet.Utility.showInSnack
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Data.AppPref
import donank.amoveowallet.Data.WalletDao
import donank.amoveowallet.R
import donank.amoveowallet.Repositories.DBRepository
import donank.amoveowallet.Repositories.MainRepository
import donank.amoveowallet.Repositories.NetworkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_peer.*
import javax.inject.Inject

class Peer : Fragment() {

    @Inject
    lateinit var walletDao: WalletDao
    @Inject
    lateinit var restInterface: RESTInterface

    lateinit var mainRepository: MainRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as MainApplication).component.inject(this)
        mainRepository = MainRepository(DBRepository(walletDao), NetworkRepository(restInterface))
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_peer, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        edit_peer.setText(AppPref.peerUrl)

        edit_peer_btn.setOnClickListener {
            edit_peer.isEnabled = true
        }

        test_peer_btn.setOnClickListener {
            progressbar.visibility = View.VISIBLE
            when{
                edit_peer.text.isEmpty() -> showInSnack(this.view!!,"Empty Peer Input")
                else ->{
                    val url = edit_peer.text.toString()
                    mainRepository.validPeer(url)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                        val res = it.replace("\\s+","").split(",")
                        if(res[0] == "[\"ok\""){
                            tv_height.text = res[1].split("]").first()
                            edit_peer.isEnabled = false
                            submit_peer_btn.isEnabled = true
                        }else{
                            tv_height.text = "0"
                            showInSnack(this.view!!,"Unable to connect to peer.")
                        }
                    }

                }
            }
        }

        submit_peer_btn.setOnClickListener {
            if(!edit_peer.text.isEmpty()){
                submit_peer_btn.isEnabled = false
                AppPref.peerUrl = edit_peer.text.toString()
            }else{
                showInSnack(this.view!!,"Invalid Peer")
            }
        }
    }
}