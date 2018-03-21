package donank.amoveowallet.Fragments

import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.Fragment
import android.view.View
import donank.amoveowallet.Common.showFragment
import donank.amoveowallet.R
import kotlinx.android.synthetic.main.dashboard_bottom_sheet.*


class DashboardBottomSheet : BottomSheetDialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
    inflater.inflate(R.layout.dashboard_bottom_sheet, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        generate_account_btn.setOnClickListener {
            showFragment(
                    Fragment.instantiate(
                            activity,
                            GenerateWallet::class.java.name
                    ),
                    addToBackStack = false
            )
        }
        import_account_btn.setOnClickListener {
            showFragment(
                    Fragment.instantiate(
                            activity,
                            ImportWallet::class.java.name
                    ),
                    addToBackStack = false
            )
        }
        watch_address_btn.setOnClickListener {
            showFragment(
                    Fragment.instantiate(
                            activity,
                            WatchWallet::class.java.name
                    ),
                    addToBackStack = false
            )
        }
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.fragment_container,
                fragmentManager = activity!!.supportFragmentManager,
                addToBackStack = addToBackStack)
    }

}