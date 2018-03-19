package donank.amoveowallet.Fragments

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Data.AppPref
import donank.amoveowallet.Data.WalletDao
import donank.amoveowallet.R
import kotlinx.android.synthetic.main.fragment_qr.*
import net.glxn.qrgen.android.QRCode
import javax.inject.Inject

class QRAddress : Fragment() {

    @Inject lateinit var walletDao : WalletDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as MainApplication).component.inject(this)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_qr, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    fun generateQr(){
        var walletAddress = ""
        AsyncTask.execute {
            walletAddress = walletDao.getWalletByid(AppPref.currentWalletId).address
        }
        //todo qr_image.background = QRCode.from(walletAddress).bitmap()
    }
}