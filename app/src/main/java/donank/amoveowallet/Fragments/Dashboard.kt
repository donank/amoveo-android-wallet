package donank.amoveowallet.Fragments

import android.databinding.ObservableArrayList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.nitrico.lastadapter.LastAdapter
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.R
import kotlinx.android.synthetic.main.fragment_dashboard.*
import donank.amoveowallet.Data.AddressModel
import donank.amoveowallet.databinding.ItemAddressBinding

class Dashboard : Fragment() {

    private val addresses = ObservableArrayList<AddressModel>()
    private val lastAdapter: LastAdapter by lazy { initLastAdapter() }

    fun initLastAdapter() : LastAdapter{
        return LastAdapter(addresses, BR.item)
                .map<AddressModel, ItemAddressBinding>(R.layout.item_address)
                .into(wallet_recycler)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as MainApplication).component.inject(this)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        wallet_recycler.layoutManager = LinearLayoutManager(activity)
        wallet_recycler.adapter = lastAdapter
    }
}