package donank.amoveowallet.Fragments

import android.databinding.ObservableArrayList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.nitrico.lastadapter.LastAdapter
import donank.amoveowallet.Api.RESTInterface
import donank.amoveowallet.BR
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Data.Model.Transaction
import donank.amoveowallet.Data.WalletDao
import donank.amoveowallet.R
import donank.amoveowallet.databinding.ItemTransactionBinding
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_wallet.*
import javax.inject.Inject

class Wallet : Fragment() {

    private val transactions = ObservableArrayList<Transaction>()
    private val lastAdapter: LastAdapter by lazy { initLastAdapter() }

    @Inject
    lateinit var walletDao: WalletDao

    @Inject
    lateinit var restInterface: RESTInterface

    fun initLastAdapter() : LastAdapter{
        return LastAdapter(transactions, BR.item)
                .map<Transaction, ItemTransactionBinding>(R.layout.item_transaction)
                .into(transactions_recycler)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as MainApplication).component.inject(this)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_wallet,container,false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}