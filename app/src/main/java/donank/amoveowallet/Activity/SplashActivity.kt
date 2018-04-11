package donank.amoveowallet.Activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Fragments.SplashFragment
import donank.amoveowallet.R
import donank.amoveowallet.Utility.showFragment

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)
        Log.d("SPLASHACTIVITY","ONCREATE")
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
        Log.d("showFragment","Triggered")
        fragment.showFragment(container = R.id.splash_fragment_container,
                fragmentManager = supportFragmentManager,
                addToBackStack = addToBackStack)
    }
}