package donank.amoveowallet.Data

import com.chibatching.kotpref.KotprefModel

object AppPref : KotprefModel(){
    var baseUrl by stringPref("http://159.65.120.84:8080/")
}