package donank.amoveowallet.Fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import donank.amoveowallet.Utility.showFragment
import donank.amoveowallet.Data.Model.ViewModels.SelectedWalletViewModel
import donank.amoveowallet.Data.Model.Wallet
import donank.amoveowallet.R
import donank.amoveowallet.Repositories.CryptoRepository
import kotlinx.android.synthetic.main.fragment_send.*

class Send : Fragment() {

    val cryptoRepository = CryptoRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val walletModel = ViewModelProviders.of(activity!!).get(SelectedWalletViewModel::class.java)
        walletModel.getSelected().observe(this, Observer<Wallet>{
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_send, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        send_btn.setOnClickListener {
            cryptoRepository.generateTransaction(edit_send_addres.text.toString(), edit_send_amount.text.toString())
        }

        cance_send_btn.setOnClickListener {
            showFragment(
                    Fragment.instantiate(
                            activity,
                            Wallet::class.java.name
                    ),
                    addToBackStack = true
            )
        }
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.fragment_container,
                fragmentManager = activity!!.supportFragmentManager,
                addToBackStack = addToBackStack)
    }

}