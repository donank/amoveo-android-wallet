package donank.amoveowallet.Common

import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.R


fun Fragment.showFragment(container: Int, fragmentManager: FragmentManager,
                          addToBackStack: Boolean = false) {
    val fm = fragmentManager.beginTransaction()
    fm.replace(container, this, this.javaClass.simpleName)
    if (addToBackStack) fm.addToBackStack(null)
    fm.commit()
}

fun showInSnack(text: String, context: Context = MainApplication.instance){
    Snackbar.make(View.inflate(context,R.layout.custom_snackbar,null),text,Snackbar.LENGTH_SHORT)
}