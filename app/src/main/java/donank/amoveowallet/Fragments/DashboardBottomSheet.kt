package donank.amoveowallet.Fragments

import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.support.design.widget.BottomSheetDialogFragment
import android.view.View
import donank.amoveowallet.R


class DashboardBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
    inflater.inflate(R.layout.dashboard_bottom_sheet, container, false)

}