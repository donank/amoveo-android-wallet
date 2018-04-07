package donank.amoveowallet.Repositories

import android.util.Log
import org.spongycastle.asn1.ASN1Integer
import org.spongycastle.asn1.DERSequenceGenerator
import org.spongycastle.asn1.sec.SECNamedCurves
import org.spongycastle.crypto.digests.SHA256Digest
import org.spongycastle.crypto.generators.ECKeyPairGenerator
import org.spongycastle.crypto.params.*
import org.spongycastle.crypto.signers.ECDSASigner
import org.spongycastle.crypto.signers.HMacDSAKCalculator
import org.spongycastle.jce.provider.BouncyCastleProvider
import java.math.BigInteger
import java.security.*
import org.spongycastle.util.encoders.Base64
import org.spongycastle.util.encoders.Base64Encoder
import org.spongycastle.util.encoders.Hex
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream


class CryptoRepository {


    init {
        Security.insertProviderAt(BouncyCastleProvider() as Provider,1)
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

    val signer = ECDSASigner(HMacDSAKCalculator(SHA256Digest()))

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


    fun generateTransaction(toAddress: String, amount: String, password: String, res: String):String {
        return ""
    }

    fun sign(data: List<Any>, pKey: String): String {
        Log.d("sign","$data -- $pKey")
        Log.d("pkey - BigInteger","${BigInteger(pKey, HEX_RADIX)}")
        val key = ECPrivateKeyParameters(BigInteger(pKey, HEX_RADIX),domain)
        val signatureBytes: ByteArray
        try{
            Log.d("data","$data")
            Log.d("Key",key.toString())

            val serializedData = serialize(data) as List<Int>

            Log.d("serializedData","$serializedData")
            val baos1 = ByteArrayOutputStream()
            val dout = DataOutputStream(baos1)
            serializedData.forEach {
                dout.write(it)
            }
            Log.d("baos1","$baos1")
            Log.d("baos1String", String(baos1.toByteArray()))
            val bArr = byteArrayOf()
            baos1.write(bArr)
            val hash = hash(bArr)

            Log.d("hash","$hash")

            signer.init(true,ECPrivateKeyParameters(key.d,domain))

            val signature = signer.generateSignature(hash)
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
            return "error"
        }
        Log.d("SIGNATURE","${toHex(signatureBytes)}")
        return Base64.toBase64String(signatureBytes)
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

    fun hash(data: ByteArray): ByteArray {
        Log.d("hash - input","$data")
        var hash = byteArrayOf()
        val digest = MessageDigest.getInstance("SHA-256")
        try {
            hash = digest.digest(data)
            Log.d("SHA256Digest encoded","$hash")
        }catch (e: Exception){
            Log.d("Exception while hashing",e.message)
        }
        Log.d("hash - output","$data")
        return hash
    }

    fun serialize(data : Any): Any {
        return when (data) {
            is Number -> {
                Log.d("isNumber","$data")
                return integerToArray(3,1).plus(integerToArray(data as Long,64))
            }
            is List<*> -> {
                when{
                    data[0] == -6 ->{
                        Log.d("-6","${data[0]}")
                        val rest = serializeList(data.slice(1..1).requireNoNulls())
                        Log.d("-6-REST","$rest")
                        return (integerToArray(1,1)).plus(integerToArray(rest.size.toLong(),4)).plus(rest)
                    }

                    data[0] == -7 ->{
                        Log.d("-7","${data[0]}")
                        val rest = serializeList(data.slice(1..1).requireNoNulls())
                        //Log.d("-7-REST","$rest")
                        return (integerToArray(2,1)).plus(integerToArray(rest.size.toLong(),4)).plus(rest)
                    }

                    data[0] is String ->{
                        Log.d("isString","${data[0]}")
                        val h = data[0] as String
                        val d0 = data.subList(1,data.size)
                        val first = (integerToArray(4,1)).plus(integerToArray(h.length.toLong(),4)).plus(stringToArray(h))
                        val rest = first.plus(serializeList(d0.requireNoNulls()))
                        //Log.d("isStringfirst","$first")
                        //Log.d("isStringrest","$rest")
                        //Log.d("FINAL RETURN RESULT","${(integerToArray(2,1)).plus(integerToArray(rest.size.toLong(),4)).plus(rest)}")
                        return (integerToArray(2,1)).plus(integerToArray(rest.size.toLong(),4)).plus(rest)
                    }

                    else -> {
                        Log.d("ELSE SERIALIZE","NOTHING-${data[0]}")
                    }
                }
            }
            is String -> {
                Log.d("SERIALIZE BINARY STR","$data")
                val outStream = ByteArrayOutputStream()
                Base64Encoder().decode(data,outStream)
                val rest = mutableListOf<Int>()
                outStream.toByteArray().forEach {
                    if(it <= 0){
                        rest.add((it.toInt()+256))
                    }
                    else rest.add(it.toInt())
                }
                Log.d("rest","$rest")
                outStream.close()
                //Log.d("SERIALIZE BINARY REST","$rest")
                return integerToArray(0,1).plus(integerToArray(rest.size.toLong(),4)).plus(rest)
            }
            else -> {
                Log.d("SERIALIZE BINARY NOTSTR","$data")
                val d = data as List<Any>
                return (integerToArray(0,1)).plus(integerToArray(d.size.toLong(),4)).plus(d)
            }
        }
    }

    fun integerToArray(num: Long, size: Int): List<Number> {
        Log.d("integerToArray","$num-$size")
            var i = 0
            var a = num
            val b = mutableListOf<Number>()
            while(i < size){
                b.add(((a%256)+256)%256)
                a = Math.floor(a/256.0).toLong()
                i += 1
            }
        Log.d("integerToArray-res","$b")
        return b
    }

    fun stringToArray(s: String) : List<Number> {
        Log.d("stringToArray",s)

        val a = mutableListOf<Number>()
        var i = 0
        while(i < s.length){
            a.add(s[i].toInt())
            i += 1
        }
        Log.d("stringToArray-res","$a")
        return a
    }

    fun serializeList(d : List<Any>): List<Any> {
        Log.d("serializeList","$d")
        val m = mutableListOf<Any>()
        var i = 0
        val l = d
        while(i < l.size){
            val ser = serialize(d[i]) as List<*>
            ser.forEach {
                m.add(it as Any)
            }
            Log.d("sL - m","$m")
            i += 1
        }
        Log.d("serializeList-res","$m")
        return m
    }

    fun verify(message: String, signature : String, pubkey: String): Boolean {
        val sig =bin2rs(Base64.decode(signature))
        val d2 = serialize(message)
        val h = hash(d2 as ByteArray)
        signer.init(false,ECPublicKeyParameters(curve.curve.decodePoint(pubkey.toByteArray()),domain))
        return signer.verifySignature(h, BigInteger(sig.first), BigInteger(sig.second))
    }

    fun bin2rs(data: ByteArray): Pair<String, String> {
        val h = toHex(data)
        val a2 = data[3].toInt()
        val r = h.slice(8..8+(a2*2))
        val s = h.slice(12+(a2*2)..12+(a2*2))
        return Pair(r,s)
    }

}