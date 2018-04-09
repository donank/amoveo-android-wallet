package donank.amoveowallet.Activity

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Fragments.SplashFragment
import donank.amoveowallet.R
import donank.amoveowallet.Utility.showFragment

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_splash)
        (application as MainApplication).component.inject(this)

        showFragment(
                Fragment.instantiate(
                        this,
                        SplashFragment::class.java.name
                ),
                addToBackStack = false
        )
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.splash_fragment_container,
                fragmentManager = supportFragmentManager,
                addToBackStack = addToBackStack)
    }
}