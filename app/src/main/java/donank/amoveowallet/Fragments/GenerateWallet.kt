package donank.amoveowallet.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Data.WalletDao
import donank.amoveowallet.R
import donank.amoveowallet.Repositories.DBRepository
import kotlinx.android.synthetic.main.fragment_generate.*
import javax.inject.Inject

class GenerateWallet : Fragment() {

    @Inject lateinit var walletDao: WalletDao

    val dbRepository = DBRepository(walletDao)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as MainApplication).component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_generate, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        edit_generate_account_name.setText("Wallet".plus(dbRepository.getWalletCountFromDb() + 1))
    }
}