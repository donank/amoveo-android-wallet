package donank.amoveowallet.Data

import com.chibatching.kotpref.KotprefModel

object AppPref : KotprefModel(){
    var peerUrl by stringPref("http://159.65.120.84:8080/")
    var validPeer by booleanPref()
    var currentWalletId by longPref(0)
}