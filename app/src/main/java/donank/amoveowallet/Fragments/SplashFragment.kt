package donank.amoveowallet.Fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import donank.amoveowallet.Activity.MainActivity
import donank.amoveowallet.Dagger.MainApplication
import donank.amoveowallet.Data.AppPref
import donank.amoveowallet.R
import donank.amoveowallet.Utility.showInSnack
import kotlinx.android.synthetic.main.fragment_splash.*

class SplashFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as MainApplication).component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_splash, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(AppPref.accountExists){
            register_btn.visibility = View.GONE
            login_btn.visibility = View.VISIBLE
        }

        register_btn.setOnClickListener {
            first_view.visibility = View.GONE
            new_account.visibility = View.VISIBLE
        }

        submit_reg_passcode_1_btn.setOnClickListener {
            if(edit_reg_passcode_1.text.isNullOrEmpty()){
                showInSnack(this.view!!,"Input is Empty")
            }else{
                new_account.visibility = View.GONE
                new_account_confirm.visibility = View.VISIBLE
            }
        }

        submit_reg_passcode_2_btn.setOnClickListener {
            when {
                edit_reg_passcode_2.text.isNullOrEmpty() -> showInSnack(this.view!!, "Input is Empty.")
                edit_reg_passcode_2.text.toString() == edit_reg_passcode_1.text.toString() -> {
                    AppPref.passcode = edit_reg_passcode_2.text.toString()
                    startActivity(Intent(activity, MainActivity::class.java))
                }
                else -> showInSnack(this.view!!, "Entered passcode is different from previous input.")
            }
        }

        login_btn.setOnClickListener {
            first_view.visibility = View.GONE
            login.visibility = View.VISIBLE
        }

        submit_login_passcode_btn.setOnClickListener {
            when {
                edit_login_passcode.text.isNullOrEmpty() -> showInSnack(this.view!!,"Input is Empty.")
                edit_login_passcode.text.toString() == AppPref.passcode -> startActivity(Intent(activity, MainActivity::class.java))
                else -> showInSnack(this.view!!,"Invalid Passcode. Try Again!")
            }
        }
    }
}