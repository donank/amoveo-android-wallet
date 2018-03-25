package donank.amoveowallet.Fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Data.Model.ViewModels.SelectedWalletViewModel
import donank.amoveowallet.Data.Model.Wallet
import donank.amoveowallet.Data.WalletDao
import donank.amoveowallet.R
import kotlinx.android.synthetic.main.fragment_receive.*
import net.glxn.qrgen.android.QRCode
import javax.inject.Inject

class Receive : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as MainApplication).component.inject(this)

        val walletModel = ViewModelProviders.of(activity!!).get(SelectedWalletViewModel::class.java)
        walletModel.getSelected().observe(this, Observer<Wallet>{
            tv_receive_address.text = it!!.address
        })
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_receive, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        qr_image.setImageBitmap(generateQr(tv_receive_address.text.toString()))
    }

    fun generateQr(string: String): Bitmap {
        return QRCode.from(string).bitmap()
    }
}