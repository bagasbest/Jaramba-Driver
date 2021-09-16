package com.bagasbest.jarambadriver.model.model

data class HistoryModel(
    var busId: String? = null,
    var comment: String? = null,
    var driverName: String? = null,
    var finishDate: String? = null,
    var income: Int? = 0,
    var rating: String? = null,
    var startDate: String? = null,
    var status: String? = null,
    var totalPassanger: Int? = 0,
    var trayek: String? = null,
    var tripId: String? = null,
    var calendar: String? = null,
)