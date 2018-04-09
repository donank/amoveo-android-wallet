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
import donank.amoveowallet.Data.Model.WalletModel
import donank.amoveowallet.R
import donank.amoveowallet.Utility.copyToClipBoard
import donank.amoveowallet.Utility.showInSnack
import kotlinx.android.synthetic.main.fragment_receive.*
import net.glxn.qrgen.android.QRCode

class Receive : Fragment() {

    lateinit var walletModel : SelectedWalletViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as MainApplication).component.inject(this)

        walletModel = ViewModelProviders.of(activity!!).get(SelectedWalletViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_receive, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        walletModel.getSelected().observe(this, Observer<WalletModel>{
            tv_receive_address.text = it!!.address
            qr_image.setImageBitmap(generateQr(it.address))
        })

        tv_receive_address.setOnClickListener {
            copyToClipBoard(tv_receive_address.text.toString(),activity!!)
            showInSnack(this.view!!,"Copied To Clipboard")
        }

    }

    fun generateQr(string: String): Bitmap {
        val qrcode = QRCode.from(string).withSize(600,600).bitmap()
        return qrcode
    }
}