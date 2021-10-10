package com.example.flyapp.repositories


import org.junit.Assert.assertEquals
import org.junit.Test

class FlightAPITest {
    // simulated api data
    private var flight1: MutableList<Any?> = mutableListOf("478793",null,"Norway",1618489219,1618489219,11.899,64.3931,7315.2,false,117.6,207.91,0,7345.68,false,0)
    private var flight2: MutableList<Any?> = mutableListOf(null,null,null,null,null,11.899,64.3931,7315.2,false,117.6,207.91,0,7345.68,false,0)

    // private var flight1: MutableList<Any?> = mutableListOf("478793","WIF772  ","Norway",1618489219,1618489219,11.899,64.3931,7315.2,false,117.6,207.91,0,7345.68,false,0)


    @Test
    fun `Test fail if no Flight object is made`() {
        val flights: MutableList<MutableList<Any?>>? = mutableListOf()

        flights?.add(flight1)

        val flightObjects = FlightAPI().makeFlightObjects(flights)
        val resultOfTest = (flightObjects.isEmpty())
        assertEquals(resultOfTest, false)

    }

    @Test
    fun `Test fail if no Flight object is made with null values in data`() {
        val flights: MutableList<MutableList<Any?>>? = mutableListOf()

        flights?.add(flight2)

        val flightObjects = FlightAPI().makeFlightObjects(flights)
        val resultOfTest = (flightObjects.isEmpty())
        assertEquals(resultOfTest, false)

    }


}