package br.com.hermivaldo.rememberme.fragments


import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import java.util.*

class DatePicker : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONDAY)
        val day = c.get(Calendar.DAY_OF_MONTH)
        var listener = this.fragmentManager!!.fragments.get(0) as DatePickerDialog.OnDateSetListener?
        return DatePickerDialog(activity, listener, year, month, day)
    }

}
