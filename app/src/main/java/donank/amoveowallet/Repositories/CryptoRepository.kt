package donank.amoveowallet.Repositories

import android.util.Base64

class CryptoRepository {


    //val keyPairGen = KeyPairGenerator.getInstance("EC", "SunEC")

    //val ecsp = ECGenParameterSpec("secp256k1")


    //https://stackoverflow.com/questions/8571501/how-to-check-whether-the-string-is-base64-encoded-or-not
    fun validateAddress(address: String): Boolean{
        return if(!address.isEmpty()){
            try {
                Base64.decode(address, Base64.DEFAULT)
            }catch (e: Exception){
                false
            }
            true
        }else{
            false
        }
    }

    /*
    fun genKeyPair(salt: String?): KeyPair{
        keyPairGen.initialize(ecsp)
        return if(!salt.isNullOrEmpty()) {
            keyPairGen.genKeyPair()
        }else{
            keyPairGen.genKeyPair()
        }
    }
*/
}