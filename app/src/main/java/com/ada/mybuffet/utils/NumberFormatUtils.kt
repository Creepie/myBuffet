package com.ada.mybuffet.utils

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

class NumberFormatUtils {

    companion object {
        @JvmStatic
        var locale: Locale = Locale.GERMANY

        @JvmStatic
        fun toCurrencyString(number: BigDecimal) : String {
            return try {
                NumberFormat.getCurrencyInstance(locale).format(number)
            } catch (e: java.lang.NumberFormatException) {
                "n/a"
            }
        }

        @JvmStatic
        fun toCurrencyString(numberStr: String) : String {
            return try {
                val number = BigDecimal(numberStr)
                NumberFormat.getCurrencyInstance(locale).format(number)
            } catch (e: java.lang.NumberFormatException) {
                "n/a"
            }
        }

        @JvmStatic
        fun toPercentString(numberStr: String) : String {
            return try {
                val number = BigDecimal(numberStr)
                val localizedNumber = NumberFormat.getInstance(locale).format(number)

                return if (number >= BigDecimal.ZERO) {
                    "(+$localizedNumber%)"
                } else {
                    "($localizedNumber%)"
                }
            } catch (e: java.lang.NumberFormatException) {
                "n/a"
            }
        }
    }


}