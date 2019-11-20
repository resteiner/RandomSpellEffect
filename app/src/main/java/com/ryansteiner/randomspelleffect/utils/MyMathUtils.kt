package com.ryansteiner.randomspelleffect.utils

import kotlin.math.abs
import kotlin.math.roundToInt


class MyMathUtils{
    fun convertDecimalToFraction(x: Double): String? {
        /**
         * Code adapted from Matthew556 on StackOverflow (https://stackoverflow.com/questions/31585931/how-to-convert-decimal-to-fractions)
         */
        if (x < 0) {
            return "-" + convertDecimalToFraction(-x)
        }
        val tolerance = 1.0E-6
        var h1 = 1.0
        var h2 = 0.0
        var k1 = 0.0
        var k2 = 1.0
        var b = x
        do {
            val a = Math.floor(b)
            var aux = h1
            h1 = a * h1 + h2
            h2 = aux
            aux = k1
            k1 = a * k1 + k2
            k2 = aux
            b = 1 / (b - a)
        } while (abs(x - h1 / k1) > x * tolerance)

        return "${h1.roundToInt()}/${k1.roundToInt()}"
    }
}