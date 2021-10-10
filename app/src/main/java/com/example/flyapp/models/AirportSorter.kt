package com.example.flyapp.models

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun popularSort(airports: MutableList<Airport>): MutableList<Airport> {
    var i = 1
    while(i < airports.size) {
        var j = i
        while(j > 0 && airports[j-1].yearlyPassengers < airports[j].yearlyPassengers) {
            val tmp = airports[j]
            airports[j] = airports[j-1]
            airports[j-1] = tmp
            j--
        }
        i++
    }

    return airports
}

fun hotSort(airports: MutableList<Airport>): MutableList<Airport> {
    var i = 1
    while(i < airports.size) {
        var j = i
        while(j > 0 && airports[j-1].weatherObject.airTemp!! < airports[j].weatherObject.airTemp!!) {
            val tmp = airports[j]
            airports[j] = airports[j-1]
            airports[j-1] = tmp
            j--
        }
        i++
    }

    return airports
}
fun coldSort(airports: MutableList<Airport>): MutableList<Airport> {
    var i = 1
    while(i < airports.size) {
        var j = i
        while(j > 0 && airports[j-1].weatherObject.airTemp!! > airports[j].weatherObject.airTemp!!) {
            val tmp = airports[j]
            airports[j] = airports[j-1]
            airports[j-1] = tmp
            j--
        }
        i++
    }

    return airports
}

fun distanceSort(airports: MutableList<Airport>, userLat: Double, userLng: Double): MutableList<Airport> {
    // Uses the Haversine formula to calculate distances between two geographical points
    val radius = 6371e3 // Earths radius in meters
    val s1 = userLat * Math.PI/180
    for(ap in airports) {
        val s2 = ap.lat * Math.PI/180
        val b1 = (ap.lat - userLat) * Math.PI/180
        val b2 = (ap.lon - userLng) * Math.PI/180

        val a = sin(b1/2) * sin(b1/2) +
                cos(s1) * cos(s2) * sin(b2/2) * sin(b2/2)
        val c = 2 * atan2(sqrt(a), sqrt(1-a))

        val d = radius * c
        ap.newDistance(d)
    }

    var i = 1
    while(i < airports.size) {
        var j = i
        while(j > 0 && airports[j-1].distanceFromUser > airports[j].distanceFromUser) {
            val tmp = airports[j]
            airports[j] = airports[j-1]
            airports[j-1] = tmp
            j--
        }
        i++
    }

    return airports
}

fun calculateDistance(airports: MutableList<Airport>, userLat: Double, userLng: Double): MutableList<Airport> {
    val radius = 6371e3 // Earths radius in meters
    val s1 = userLat * Math.PI/180
    for(ap in airports) {
        val s2 = ap.lat * Math.PI/180
        val b1 = (ap.lat - userLat) * Math.PI/180
        val b2 = (ap.lon - userLng) * Math.PI/180

        val a = sin(b1/2) * sin(b1/2) +
                cos(s1) * cos(s2) * sin(b2/2) * sin(b2/2)
        val c = 2 * atan2(sqrt(a), sqrt(1-a))

        val d = radius * c
        ap.newDistance(d)
    }
    return airports
}

