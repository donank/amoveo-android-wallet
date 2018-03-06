package donank.amoveowallet.Data

import com.chibatching.kotpref.KotprefModel

object AppPref : KotprefModel(){
    var baseUrl by stringPref("http://159.89.106.253:8080")
}