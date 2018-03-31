package donank.amoveowallet.Repositories

import android.util.Log
import org.spongycastle.asn1.ASN1Integer
import org.spongycastle.asn1.DERSequenceGenerator
import org.spongycastle.asn1.sec.SECNamedCurves
import org.spongycastle.crypto.Signer
import org.spongycastle.crypto.digests.SHA256Digest
import org.spongycastle.crypto.generators.ECKeyPairGenerator
import org.spongycastle.crypto.params.*
import org.spongycastle.crypto.signers.ECDSASigner
import org.spongycastle.crypto.signers.HMacDSAKCalculator
import org.spongycastle.jce.provider.BouncyCastleProvider
import java.math.BigInteger
import java.security.*
import org.spongycastle.util.encoders.Base64
import org.spongycastle.util.encoders.Hex
import java.io.ByteArrayOutputStream


class CryptoRepository {


    init {
        Security.insertProviderAt(BouncyCastleProvider(),1)
    }

    private val EC_GEN_PARAM_SPEC = "secp256k1"
    private val HEX_RADIX = 16
    private val MIN_S_VALUE = BigInteger("1", HEX_RADIX)
    private val MAX_S_VALUE = BigInteger("7FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF5D576E7357A4501DDFE92F46681B20A0", HEX_RADIX)
    private val N = BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", HEX_RADIX)

    val curve = SECNamedCurves.getByName(EC_GEN_PARAM_SPEC)
    val domain = ECDomainParameters(curve.curve, curve.g,curve.n,curve.h)
    val generator = ECKeyPairGenerator()
    val keygenParams = ECKeyGenerationParameters(domain, SecureRandom())

    val hexArray = "0123456789ABCDEF".toCharArray()


    fun toHex(data: ByteArray):String{
        return Hex.toHexString(data)
    }

    fun generatePubKey(privateKey : String): String {
        val privKey = BigInteger(privateKey, HEX_RADIX)
        val curvePt = curve.g.multiply(privKey)
        val x = curvePt.x.toBigInteger()
        val y = curvePt.y.toBigInteger()
        val xBytes = removeSignByte(x.toByteArray())
        val yBytes = removeSignByte(y.toByteArray())
        val pubKeyBytes = ByteArray(65)
        pubKeyBytes[0] = "04".toByte()
        System.arraycopy(xBytes, 0, pubKeyBytes, 1, xBytes.size)
        System.arraycopy(yBytes, 0, pubKeyBytes, 33, yBytes.size)
        return Base64.toBase64String(pubKeyBytes)
    }

    private fun removeSignByte(arr: ByteArray): ByteArray {
        if (arr.size == 33) {
            val newArr = ByteArray(32)
            System.arraycopy(arr, 1, newArr, 0, newArr.size)
            return newArr
        }
        return arr
    }


    //https://stackoverflow.com/questions/8571501/how-to-check-whether-the-string-is-base64-encoded-or-not
    fun validateAddress(address: String): Boolean{
        return if(!address.isEmpty()){
            try {
                Base64.decode(address)
            }catch (e: Exception){
                false
            }
            true
        }else{
            false
        }
    }

    fun genKeyPair(): Pair<ECPrivateKeyParameters, ECPublicKeyParameters> {
        generator.init(keygenParams)
        val keyPair = generator.generateKeyPair()
        val privParams = keyPair.private as ECPrivateKeyParameters
        val pubParams = keyPair.public as ECPublicKeyParameters
        return Pair(privParams,pubParams)
    }

    //Generate transaction
    //https://bitcoin.stackexchange.com/questions/52434/building-signed-bitcoin-transaction-in-java
    //https://gist.github.com/Sjors/5574485
    //https://stackoverflow.com/questions/34451214/how-to-sign-and-verify-signature-with-ecdsa-in-python
    //https://bitcoin.stackexchange.com/questions/3374/how-to-redeem-a-basic-tx
    //https://ebrary.net/7941/education/signing_bitcoin_transaction_using_ecdsa

    fun generateTransaction(toAddress: String, amount: String) {

    }

    fun sign(data : String, key : ECPrivateKeyParameters): String {
        var signatureBytes = byteArrayOf()
        try{
            Log.d("data",data)
            Log.d("Key",key.toString())

            val signer = ECDSASigner(HMacDSAKCalculator(SHA256Digest()))
            signer.init(true,ECPrivateKeyParameters(key.d,domain))

            val signature = signer.generateSignature(data.toByteArray())
            val r = signature[0]
            val s = signature[1]
            val lowS = getLowValue(s)
            val baos = ByteArrayOutputStream()
            val derSequenceGenerator = DERSequenceGenerator(baos)
            val as1nr = ASN1Integer(r)
            val asn1LowS = ASN1Integer(lowS)
            derSequenceGenerator.addObject(as1nr)
            derSequenceGenerator.addObject(asn1LowS)
            derSequenceGenerator.close()
            signatureBytes = baos.toByteArray()
        }catch (e: Exception){
            Log.d("EXCEPTION WHILE SIGNING",e.message)
        }
        return toHex(signatureBytes)
    }

    private fun getLowValue(s: BigInteger): BigInteger {
        val lowerThanMin = s.compareTo(MIN_S_VALUE)
        val higherThanMax = s.compareTo(MAX_S_VALUE)
        if (lowerThanMin < 0) {
            throw IllegalArgumentException(String.format("S value must be equal or greater than: %s", MIN_S_VALUE))
        } else if (higherThanMax > 0) {
            return N.subtract(s)
        }
        return s
    }

    fun sign1(){

    }

    fun hash(data: String){

    }

    fun serialize(data : Any): Any {
        return when (data) {
            is Number -> {
                return integerToArray(3,1).plus(integerToArray(data as Int,64))
            }
            is Array<*> -> {
                when{
                    data[0] == -6 ->{
                        val rest = serializeList(data.slice(1..1)) as List<*>
                        return (integerToArray(1,1) as List<*>).plus(integerToArray(rest.size,4) as List<*>).plus(rest)
                    }

                    data[0] == -7 ->{
                        val rest = serializeList(data.slice(1..1)) as List<*>
                        return (integerToArray(2,1) as List<*>).plus(integerToArray(rest.size,4) as List<*>).plus(rest)
                    }

                    data[0] is String ->{
                        val h = data[0] as String
                        val d0 = data.slice(1..1) as List<*>
                        val first = (integerToArray(4,1) as List<*>).plus(integerToArray(h.length,4) as List<*>).plus(stringToArray(h))
                        val rest = first.plus(serializeList(d0))
                        return (integerToArray(2,1) as List<*>).plus(integerToArray(rest.size,4) as List<*>).plus(rest)
                    }

                    else -> {
                        Log.d("SERIALIZE","ELSE")
                    }
                }
            }
            is String -> {
                val rest = stringToArray(Base64.decode(data).toString())
                return integerToArray(0,1).plus(integerToArray(rest.size,4)).plus(rest)
            }
            else -> {
                val d = data  as List<*>
                return (integerToArray(0,1) as List<*>).plus(integerToArray(d.size,4) as List<*>).plus(d)
            }
        }
    }

    fun integerToArray(num: Int, size: Int): Array<Number> {
            var i = size
            var a = num
            val b = arrayOf<Number>()
            while(i > 0){
                b.plus(((a%256)+256)%256)
                a = Math.floor(a/256.0).toInt()
                i += 1
            }
        return b
    }

    fun stringToArray(s: String) : Array<Number> {
        val a = arrayOf<Number>()
        var i = 0
        while(i < s.length){
            a[i] = s[i].toInt()
            i += 1
        }
        return a
    }

    fun serializeList(d : Any): Any {
        var m = listOf<Any>()
        var i = 0
        val l = d as List<Any>
        while(i > l.size){
            m = m.plus(serialize(l[i]))
            i += 1
        }
        return m
    }

}