package com.example.chap.internal


class SolarCalendar(private val y: Int, private val m: Int, private val d: Int) {

    var jy = 0
    var jm = 0
    var jd = 0

    init {
        val g_days_in_month =
            intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val j_days_in_month =
            intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        val gy: Int = y - 1600
        val gm: Int = m - 1
        val gd: Int = d - 1

        var g_day_no: Int =
            365 * gy + div((gy + 3).toFloat(), 4f) - div(
                (gy + 99.toFloat()),
                100f
            ) + div((gy + 399.toFloat()), 400f)

        for (i in 0 until gm) g_day_no += g_days_in_month[i]
        if (gm > 1 && (gy % 4 == 0 && gy % 100 != 0 || gy % 400 == 0)) // leap and after Feb
            g_day_no++
        g_day_no += gd

        var j_day_no = g_day_no - 79

        val j_np: Int = div(j_day_no.toFloat(), 12053f) //12053 = 365*33 + 32/4

        j_day_no = j_day_no % 12053

        jy = 979 + 33 * j_np + 4 * div(
            j_day_no.toFloat(),
            1461f
        ) // 1461 = 365*4 + 4/4


        j_day_no %= 1461

        if (j_day_no >= 366) {
            jy += div((j_day_no - 1.toFloat()), 365f)
            j_day_no = (j_day_no - 1) % 365
        }

        var j = 0
        while (j < 11 && j_day_no >= j_days_in_month[j]) {
            j_day_no -= j_days_in_month[j]
            j++
        }

        jm = j + 1
        jd = j_day_no + 1

    }

    private fun div(a: Float, b: Float): Int {
        return (a / b).toInt()
    }

}

