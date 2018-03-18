package donank.amoveowallet.Repositories

import android.app.Activity
import android.content.Context
import android.util.Log
import donank.amoveowallet.Api.RESTInterface
import donank.amoveowallet.Common.showInSnack
import donank.amoveowallet.Data.Model.Wallet
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class NetworkRepository {

    @Inject
    lateinit var restInterface: RESTInterface

    fun getAddressValue(wallet : Wallet) {
        val command = """["account","${wallet.address}"]"""
        restInterface.postRequest(command)
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    val res = it.replace("\\s+","").split(",")
                    if(res[0] == """["ok""""){
                        wallet.value = res[2].toLong()
                    }else{

                            Log.d("GETADDRESSVALUE","Error loading account details!")

                    }
                },{

                        Log.d("GETADDRESSVALUE","Error loading account details!")

                })
    }
}