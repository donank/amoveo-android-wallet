package donank.amoveowallet.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
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

        test_peer_btn.setOnClickListener {
            when{
                edit_peer.text.isEmpty() -> showInSnack(this.view!!,"Empty Peer Input")
                else ->{
                    val oldUrl = AppPref.peerUrl
                    AppPref.peerUrl = edit_peer.text.toString()
                    val test = mainRepository.validPeer()
                    if(test.first){
                        AppPref.validPeer = true
                        change_peer_btn.isEnabled
                        tv_height.text = test.second
                    }else{
                        AppPref.peerUrl = oldUrl
                        showInSnack(this.view!!,"Invalid Peer")
                    }
                }
            }
        }

        change_peer_btn.setOnClickListener {
            if(!edit_peer.text.isEmpty() && AppPref.validPeer){
                AppPref.peerUrl = edit_peer.text.toString()
            }else{
                showInSnack(this.view!!,"Invalid Peer")
            }
        }
    }
}