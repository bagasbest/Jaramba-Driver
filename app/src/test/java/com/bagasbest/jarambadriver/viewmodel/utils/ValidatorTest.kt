package com.bagasbest.jarambadriver.viewmodel.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ValidatorTest {

    @Test
    fun driverValidStartTrip() {
        val trayek = "Cicaheum - Cibureum"
        val plat = "F 1234 AB"
        val result = Validator.validateStartTrip(trayek, plat)
        assertThat(result).isEqualTo(true)
    }

    @Test
    fun driverInvalidStartTrip() {
        val trayek = ""
        val plat = ""
        val result = Validator.validateStartTrip(trayek, plat)
        assertThat(result).isEqualTo(false)
    }

    @Test
    fun driverValidateFinishTrip() {
        val rating = 5.0
        val review = "Aplikasi Jaramba Mobile Sangat Membantu Saya Dalam Kehidupan Sehari - hari"
        val result = Validator.validateFinishTrip(rating, review)
        assertThat(result).isEqualTo(true)
    }

    @Test
    fun driverInvalidateFinishTrip() {
        val rating = 0.0
        val review = ""
        val result = Validator.validateFinishTrip(rating, review)
        assertThat(result).isEqualTo(false)
    }

}