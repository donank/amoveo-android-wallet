package donank.amoveowallet.Fragments

import android.app.AlertDialog
import android.databinding.ObservableArrayList
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.nitrico.lastadapter.LastAdapter
import donank.amoveowallet.Api.RESTInterface
import donank.amoveowallet.BR
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Data.AddressDao
import donank.amoveowallet.R
import kotlinx.android.synthetic.main.fragment_dashboard.*
import donank.amoveowallet.Data.AddressModel
import donank.amoveowallet.databinding.ItemAddressBinding
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class Dashboard : Fragment() {

    private val addresses = ObservableArrayList<AddressModel>()
    private val lastAdapter: LastAdapter by lazy { initLastAdapter() }

    @Inject
    lateinit var addressDao: AddressDao

    @Inject
    lateinit var restInterface: RESTInterface


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

        add_address_btn.setOnClickListener {
            showAddressInputView()
        }
        submit_address_btn.setOnClickListener {
            addAddressToList()
        }
        address_cancel_btn.setOnClickListener {
            hideAddressInputView()
        }
    }

    fun showAddressInputView(){
        input_address_layout.visibility = View.VISIBLE
    }

    fun hideAddressInputView(){
        input_address_layout.visibility = View.GONE
    }

    fun addAddressToList(){
        hideAddressInputView()
        val inputAddress = edit_input_address.text.toString().replace("\\s+","")
        validateAddress(inputAddress)
        val address = AddressModel(
                address = inputAddress,
                value = 0
        )
        addresses.add(address)
        getAddressValue(address)
        saveAddressToDb(address)
        lastAdapter.notifyDataSetChanged()

    }

    fun getAddressValue(address : AddressModel) {
        val command = """["account","${address.address}"]"""
        restInterface.postRequest(command)
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    val res = it.replace("\\s+","").split(",")
                    if(res[0] == """["ok""""){
                        address.value = res[2].toLong()
                        addressDao.update(address)
                        activity!!.runOnUiThread {
                            addresses.filter { it.address == address.address }.forEach { it.value = address.value }
                            lastAdapter.notifyDataSetChanged()
                        }
                    }else{
                        //todo show error
                    }
                },{
                    //todo show error
                })
    }


    fun validateAddress(address: String): Boolean{
        val addressIsInvalid = true
        return if(!address.isEmpty()){

            true
        }else{
            false
        }
    }

    fun saveAddressToDb(address : AddressModel){
        AsyncTask.execute {
            addressDao.save(address)
            activity!!.runOnUiThread { /*todo show confirmation*/ }
        }
    }
}