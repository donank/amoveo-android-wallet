package donank.amoveowallet.Activity

import android.app.Application
import android.content.Context
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import donank.amoveowallet.Common.showFragment
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Fragments.Dashboard
import donank.amoveowallet.R
import kotlinx.android.synthetic.main.drawer_layout.*
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import android.view.View
import donank.amoveowallet.Fragments.Peer
import donank.amoveowallet.Fragments.Wallet
import kotlin.math.absoluteValue


class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
        (application as MainApplication).component.inject(this)
        handleNavigationClick()
        showFragment(
                Fragment.instantiate(
                        this,
                        Dashboard::class.java.name
                ),
                addToBackStack = false
        )
    }

    private fun handleNavigationClick(){
        nav_view.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            when(menuItem.itemId){
                R.id.nav_wallets->showFragment(
                        Fragment.instantiate(
                                this,
                                Dashboard::class.java.name
                        ),
                        addToBackStack = false
                )
                R.id.nav_peer->showFragment(
                        Fragment.instantiate(
                                this,
                                Peer::class.java.name
                        ),
                        addToBackStack = false
                )
                R.id.nav_contacts->showFragment(
                        Fragment.instantiate(
                                this,
                                Wallet::class.java.name
                        ),
                        addToBackStack = false
                )
                R.id.nav_settings->showFragment(
                        Fragment.instantiate(
                                this,
                                Dashboard::class.java.name
                        ),
                        addToBackStack = false
                )
            }
            drawer_layout.closeDrawers()
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.fragment_container,
                fragmentManager = supportFragmentManager,
                addToBackStack = addToBackStack)
    }

}
