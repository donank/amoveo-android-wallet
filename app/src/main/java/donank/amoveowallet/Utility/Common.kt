package donank.amoveowallet.Utility

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View
import android.graphics.Color
import android.util.Log
import android.widget.TextView
import org.spongycastle.util.encoders.Base64Encoder
import java.io.ByteArrayOutputStream


fun Fragment.showFragment(container: Int, fragmentManager: FragmentManager,
                          addToBackStack: Boolean = false) {
    val fm = fragmentManager.beginTransaction()
    fm.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out)
    fm.replace(container, this, this.javaClass.simpleName)
    if (addToBackStack) fm.addToBackStack(null)
    fm.commit()
}

fun showInSnack(view: View, text: String, duration: Int = Snackbar.LENGTH_LONG) {
    val snackbar = Snackbar.make(view, text, duration)
    val sbView = snackbar.view
    val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
    textView.setTextColor(Color.WHITE)
    textView.textSize = 12f
    snackbar.setAction("OK") { snackbar.dismiss() }
    snackbar.show()
}

fun copyToClipBoard(text: String, context: Context) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("address", text)
    clipboard.primaryClip = clip
}

fun serialize(data : Any): Any {
    return when (data) {
        is Number -> {
            //Log.d("isNumber","$data")
            return integerToArray(3,1).plus(integerToArray(data as Long,64))
        }
        is List<*> -> {
            when{
                data[0] == -6 ->{
                    //Log.d("-6","${data[0]}")
                    val rest = serializeList(data.slice(1..1).requireNoNulls())
                    Log.d("-6-REST","$rest")
                    return (integerToArray(1,1)).plus(integerToArray(rest.size.toLong(),4)).plus(rest)
                }

                data[0] == -7 ->{
                    //Log.d("-7","${data[0]}")
                    val rest = serializeList(data.slice(1..1).requireNoNulls())
                    //Log.d("-7-REST","$rest")
                    return (integerToArray(2,1)).plus(integerToArray(rest.size.toLong(),4)).plus(rest)
                }

                data[0] is String ->{
                    //Log.d("isString","${data[0]}")
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
                    //Log.d("ELSE SERIALIZE","NOTHING-${data[0]}")
                }
            }
        }
        is String -> {
            //Log.d("SERIALIZE BINARY STR","$data")
            val outStream = ByteArrayOutputStream()
            Base64Encoder().decode(data,outStream)
            val rest = mutableListOf<Int>()
            outStream.toByteArray().forEach {
                rest.add(it.toInt())
            }
            //Log.d("rest","$rest")
            outStream.close()
            //Log.d("SERIALIZE BINARY REST","$rest")
            return integerToArray(0,1).plus(integerToArray(rest.size.toLong(),4)).plus(rest)
        }
        else -> {
            //Log.d("SERIALIZE BINARY NOTSTR","$data")
            val d = data as List<Any>
            return (integerToArray(0,1)).plus(integerToArray(d.size.toLong(),4)).plus(d)
        }
    }
}

fun integerToArray(num: Long, size: Int): List<Number> {
    //Log.d("integerToArray","$num-$size")
    var i = 0
    var a = num
    val b = mutableListOf<Number>()
    while(i < size){
        b.add(((a%256)+256)%256)
        a = Math.floor(a/256.0).toLong()
        i += 1
    }
    //Log.d("integerToArray-res","${b.reversed()}")
    return b.reversed()
}

fun stringToArray(s: String) : List<Number> {
    //Log.d("stringToArray",s)

    val a = mutableListOf<Number>()
    var i = 0
    while(i < s.length){
        a.add(s[i].toInt())
        i += 1
    }
    //Log.d("stringToArray-res","$a")
    return a
}

fun serializeList(d : List<Any>): List<Any> {
    //Log.d("serializeList","$d")
    val m = mutableListOf<Any>()
    var i = 0
    while(i < d.size){
        val ser = serialize(d[i]) as List<*>
        ser.forEach {
            m.add(it as Any)
        }
        Log.d("sL - m","$m")
        i += 1
    }
    //Log.d("serializeList-res","$m")
    return m
}

fun arrayToString(data : ByteArray):String{
    //Log.d("arrayToString - data","$data")
    var a = ""
    var i = 0
    while(i<data.size){
        a += data[i].toChar()
        i += 1
    }
    //Log.d("arrayToString - return",a)
    return a
}
