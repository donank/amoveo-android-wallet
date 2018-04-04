package donank.amoveowallet.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import donank.amoveowallet.Utility.showFragment
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.R
import kotlinx.android.synthetic.main.drawer_layout.*
import android.support.v4.view.GravityCompat
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import donank.amoveowallet.Fragments.*
import kotlinx.android.synthetic.main.fragment_dashboard.*


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
                R.id.nav_participate->showFragment(
                        Fragment.instantiate(
                                this,
                                Participate::class.java.name
                        ),
                        addToBackStack = false
                )
                R.id.nav_contacts->showFragment(
                        Fragment.instantiate(
                                this,
                                Contacts::class.java.name
                        ),
                        addToBackStack = false
                )
                R.id.nav_settings->showFragment(
                        Fragment.instantiate(
                                this,
                                Settings::class.java.name
                        ),
                        addToBackStack = true
                )
            }
            drawer_layout.closeDrawers()
            true
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if(count == 0){
            AlertDialog.Builder(this)
                    .setMessage("Do you want to exit?")
                    .setPositiveButton("Yes"){_,_->finish()}
                    .setNegativeButton("No"){_,_->}
                    .create()
                    .show()
        }else {
            supportFragmentManager.popBackStack()
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
