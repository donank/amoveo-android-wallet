package donank.amoveowallet.Repositories

import android.util.Log
import donank.amoveowallet.Data.AppPref
import donank.amoveowallet.Utility.serialize
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
import org.spongycastle.util.encoders.Hex
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


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


    fun generateTransaction(tx: List<Any>, privateKey: String):String {
        val sign = Base64.toBase64String(sign(tx,privateKey))
        Log.d("SIGN",sign)
        val pubkey = generatePubKey(privateKey)
        Log.d("PUBKEY",pubkey)
        val valid = verify(tx,sign,pubkey)
        Log.d("VALID?","$valid")
        return ""
    }

    fun sign(data: List<Any>, pKey: String): ByteArray {
        Log.d("sign","$data -- $pKey")
        Log.d("pkey - BigInteger","${BigInteger(pKey, HEX_RADIX)}")
        val key = ECPrivateKeyParameters(BigInteger(pKey, HEX_RADIX),domain)
        var signatureBytes = byteArrayOf()
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
        }
        Log.d("SIGNATURE","$signatureBytes")
        Log.d("SIGNATURE-TOHEX","${toHex(signatureBytes)}")
        Log.d("SIGNATURE-BASE64","${Base64.toBase64String(signatureBytes)}")
        return signatureBytes
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

    fun verify(message: Any, signature : String, pubkey: String): Boolean {
        val sig =bin2rs(Base64.decode(signature))
        val d2 = serialize(message) as List<Int>
        val baos = ByteArrayOutputStream()
        val dout = DataOutputStream(baos)
        d2.forEach {
            dout.write(it)
        }
        Log.d("baos1","$baos")
        Log.d("baos1String", String(baos.toByteArray()))
        val bArr = byteArrayOf()
        baos.write(bArr)
        val h = hash(bArr)
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

    fun encrypt(data: String, userKey : String = AppPref.passcode): String {
        val key = generateKey(userKey.toByteArray())
        val cipher = Cipher.getInstance("AES","BC")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encrypted = cipher.doFinal(data.toByteArray())
        val encryptedValue = Base64.encode(encrypted)
        return String(encryptedValue)
    }

    fun decrypt(data: String, userKey : String = AppPref.passcode): String {
        val key = generateKey(userKey.toByteArray())
        val cipher = Cipher.getInstance("AES","BC")
        cipher.init(Cipher.DECRYPT_MODE,key)
        val decodeBytes = Base64.decode(data.toByteArray())
        val original = cipher.doFinal(decodeBytes)
        return String(original)
    }

    fun generateKey(key: ByteArray): Key{
            return SecretKeySpec(key,"AES")
    }

}