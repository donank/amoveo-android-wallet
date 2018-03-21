package donank.amoveowallet.Repositories

import android.util.Base64
import org.spongycastle.jce.provider.BouncyCastleProvider
import java.security.*
import java.security.spec.ECGenParameterSpec
import java.security.Security.insertProviderAt



class CryptoRepository {


    init {
        Security.insertProviderAt(BouncyCastleProvider(),1)
    }

    val keyPairGen = KeyPairGenerator.getInstance("ECDSA", "SC")

    val ecsp = ECGenParameterSpec("secp256k1")


    fun sha256(){

    }

    fun generatePubKey(privateKey : ByteArray): ByteArray? {
        return privateKey
    }

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

    //http://www.bouncycastle.org/wiki/display/JA1/Elliptic+Curve+Key+Pair+Generation+and+Key+Factories
    //https://stackoverflow.com/questions/19673962/codes-to-generate-a-public-key-in-an-elliptic-curve-algorithm-using-a-given-priv
    //https://github.com/kamax-io/matrix-java-sdk/blob/master/src/main/java/io/kamax/matrix/sign/KeyManager.java#L52
    //https://gist.github.com/om26er/494b2b34bd605ec081b9d7057cc4aa2f
    //https://stackoverflow.com/questions/16133579/using-spongy-castle-library-to-generate-a-key-pair-in-ecdh
    //https://stackoverflow.com/questions/35713800/android-spongycastle-ecdh-secp384r1-key-size-incorrect
    //https://stackoverflow.com/questions/30868936/retrieve-ecc-public-key-from-base64-encoded-string
    //https://stackoverflow.com/questions/31435160/how-to-construct-privatekey-if-you-know-the-curve-name-raw-private-key-point


    fun genKeyPair(salt: String?): KeyPair {
        keyPairGen.initialize(ecsp, SecureRandom())
        return if(!salt.isNullOrEmpty()) {
            keyPairGen.genKeyPair()
        }else{
            keyPairGen.genKeyPair()
        }
    }

    //Brain Wallet ->
    //String -> Sha256(String) -> tohex -> encode to base64 private key


    //Generate transaction
    //https://bitcoin.stackexchange.com/questions/52434/building-signed-bitcoin-transaction-in-java
    //https://gist.github.com/Sjors/5574485
    //https://stackoverflow.com/questions/34451214/how-to-sign-and-verify-signature-with-ecdsa-in-python
    //https://bitcoin.stackexchange.com/questions/3374/how-to-redeem-a-basic-tx
    //https://ebrary.net/7941/education/signing_bitcoin_transaction_using_ecdsa

    fun generateTransaction(){

    }

    fun signTransaction(pKey : String, data : String){

    }

}