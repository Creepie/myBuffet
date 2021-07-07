package com.ada.mybuffet.utils

import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import java.math.BigDecimal
import java.util.*

class NumberFormatUtilsTest {

    @Before
    fun setUp() {
        // set locale to germany to standardize test behavior
        NumberFormatUtils.locale = Locale.GERMANY
    }

    @Test
    fun testToCurrencyString_validBigDecimal_returnsFormattedNumber() {
        val inputNumber = BigDecimal(99.99)

        val result = NumberFormatUtils.toCurrencyString(inputNumber)

        // note: the space between the € sign and the number is a non-breaking space (NBSP)
        // use alt+space on a mac to get this character
        assertEquals("99,99 €", result)
    }

    @Test
    fun testToCurrencyString_validString_returnsFormattedNumber() {
        val inputNumber = "99.99"

        val result = NumberFormatUtils.toCurrencyString(inputNumber)

        assertEquals("99,99 €", result)
    }

    @Test
    fun testToCurrencyString_validNegativeBigDecimal_returnsFormattedNumber() {
        val inputNumber = BigDecimal(-99.99)

        val result = NumberFormatUtils.toCurrencyString(inputNumber)

        // note: the space between the € sign and the number is a non-breaking space (NBSP)
        // use alt+space on a mac to get this character
        assertEquals("-99,99 €", result)
    }

    @Test
    fun testToCurrencyString_validNegativeString_returnsFormattedNumber() {
        val inputNumber = "-99.99"

        val result = NumberFormatUtils.toCurrencyString(inputNumber)

        assertEquals("-99,99 €", result)
    }


    @Test
    fun testToCurrencyString_invalidString_returnsNotAvailable() {
        val inputNumber = "99.99 EUROS" // this causes a number format exception

        val result = NumberFormatUtils.toCurrencyString(inputNumber)

        assertEquals("n/a", result)
    }






    @Test
    fun testToPercentString_validPositiveBigDecimal_returnsFormattedNumber() {
        val inputNumber = BigDecimal(99.99)

        val result = NumberFormatUtils.toPercentString(inputNumber)

        assertEquals("(+99,99%)", result)
    }

    @Test
    fun testToPercentString_validPositiveString_returnsFormattedNumber() {
        val inputNumber = "99.99"

        val result = NumberFormatUtils.toPercentString(inputNumber)

        assertEquals("(+99,99%)", result)
    }

    @Test
    fun testToPercentString_validNegativeBigDecimal_returnsFormattedNumber() {
        val inputNumber = BigDecimal(-99.99)

        val result = NumberFormatUtils.toPercentString(inputNumber)

        assertEquals("(-99,99%)", result)
    }

    @Test
    fun testToPercentString_validNegativeString_returnsFormattedNumber() {
        val inputNumber = "-99.99"

        val result = NumberFormatUtils.toPercentString(inputNumber)

        assertEquals("(-99,99%)", result)
    }


    @Test
    fun testToPercentString_invalidString_returnsNotAvailable() {
        val inputNumber = "iWillCauseANumberFormatException"

        val result = NumberFormatUtils.toCurrencyString(inputNumber)

        assertEquals("n/a", result)
    }
}