package com.bagasbest.jarambadriver.viewmodel.utils

object Validator {

    fun validateStartTrip(trayek: String, plat: String) : Boolean{
        return !(trayek.isEmpty() || plat.isEmpty())
    }

    fun validateFinishTrip(rating: Double, review: String) : Boolean {
        return !(rating == 0.0 || review.isEmpty())
    }

}