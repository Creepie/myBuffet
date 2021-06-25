package com.ada.mybuffet.utils

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

class CurrencyFormatter {
    companion object {
        @JvmStatic
        fun toCurrencyString(number: BigDecimal) : String {
            return try {
                NumberFormat.getCurrencyInstance(Locale.GERMANY).format(number)
            } catch (e: java.lang.NumberFormatException) {
                "n/a"
            }
        }

        @JvmStatic
        fun toCurrencyString(numberStr: String) : String {
            return try {
                val number = BigDecimal(numberStr)
                NumberFormat.getCurrencyInstance(Locale.GERMANY).format(number)
            } catch (e: java.lang.NumberFormatException) {
                "n/a"
            }
        }
    }
}