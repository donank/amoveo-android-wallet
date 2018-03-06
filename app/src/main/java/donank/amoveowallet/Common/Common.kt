package donank.amoveowallet.Common

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager


fun Fragment.showFragment(container: Int, fragmentManager: FragmentManager,
                          addToBackStack: Boolean = false) {
    val fm = fragmentManager.beginTransaction()
    fm.replace(container, this, this.javaClass.simpleName)
    if (addToBackStack) fm.addToBackStack(null)
    fm.commit()
}