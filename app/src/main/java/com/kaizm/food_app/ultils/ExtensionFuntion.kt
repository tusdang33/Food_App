package com.kaizm.food_app.ultils

import android.content.Context
import java.text.DecimalFormat

fun String.currencyFormat(): String {
    val decimalFormat = DecimalFormat("###,###,##0" + " đ")
    return decimalFormat.format(this.toDouble())
}

fun Float.dipToPx(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}