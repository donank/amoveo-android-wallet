package donank.amoveowallet.Repositories

import android.util.Base64
import org.spongycastle.asn1.sec.SECNamedCurves
import org.spongycastle.crypto.AsymmetricCipherKeyPair
import org.spongycastle.crypto.generators.ECKeyPairGenerator
import org.spongycastle.crypto.params.ECDomainParameters
import org.spongycastle.crypto.params.ECKeyGenerationParameters
import org.spongycastle.crypto.params.ECPrivateKeyParameters
import org.spongycastle.jce.provider.BouncyCastleProvider
import java.math.BigInteger
import java.security.*
import org.spongycastle.crypto.params.ECPublicKeyParameters
import kotlin.experimental.and


class CryptoRepository {


    init {
        Security.insertProviderAt(BouncyCastleProvider(),1)
    }

    val curve = SECNamedCurves.getByName("secp256k1")
    val domain = ECDomainParameters(curve.curve, curve.g,curve.n,curve.h)
    val generator = ECKeyPairGenerator()
    val keygenParams = ECKeyGenerationParameters(domain, SecureRandom())

    val hexArray = "0123456789ABCDEF".toCharArray()



    fun sha256(){

    }

    fun generatePubKey(privateKey : String): String {
        val privKey = BigInteger(privateKey, 16)
        val ecp = SECNamedCurves.getByName("secp256k1")
        val curvePt = ecp.g.multiply(privKey)
        val x = curvePt.x.toBigInteger()
        val y = curvePt.y.toBigInteger()
        val xBytes = removeSignByte(x.toByteArray())
        val yBytes = removeSignByte(y.toByteArray())
        val pubKeyBytes = ByteArray(65)
        pubKeyBytes[0] = "04".toByte()
        System.arraycopy(xBytes, 0, pubKeyBytes, 1, xBytes.size)
        System.arraycopy(yBytes, 0, pubKeyBytes, 33, yBytes.size)
        return this.bytesToHex(pubKeyBytes)
    }

    private fun removeSignByte(arr: ByteArray): ByteArray {
        if (arr.size == 33) {
            val newArr = ByteArray(32)
            System.arraycopy(arr, 1, newArr, 0, newArr.size)
            return newArr
        }
        return arr
    }

    fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        var v: Int
        for (j in bytes.indices) {
            v = bytes[j].and(0xFF.toByte()).toInt()
            hexChars[j * 2] = hexArray[v.ushr(4)]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
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


    fun genKeyPair(salt: String?): Pair<ECPrivateKeyParameters, ECPublicKeyParameters> {
        generator.init(keygenParams)
        val keyPair = generator.generateKeyPair()
        val privParams = keyPair.private as ECPrivateKeyParameters
        val pubParams = keyPair.public as ECPublicKeyParameters
        return Pair(privParams,pubParams)
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

    fun signData(pKey : String, data : String){

    }

}