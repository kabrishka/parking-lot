package parking

import java.util.*
import kotlin.collections.Collection

class ParkingSpace {
    private var parkingSpace = mutableMapOf<Int, Car?>()

    fun parkingLifecycle() {
        while (true) {
            val request = readln().lowercase().split(" ")
            when (request.first()) {
                "create" -> create(request)
                "park" -> println(park(request))
                "leave" -> println(leave(request))
                "status" -> status()
                "exit" -> return
                else -> println(queryCommands(request))
            }
        }
    }

    private fun create(request: List<String>): Boolean {
        return try {
            val count = request.last().toInt()
            parkingSpace = mutableMapOf()
            (1..count).map { parkingSpace[it] = null }
            println("Created a parking lot with $count spots.")
            true
        } catch (e: NumberFormatException) {
            println("Failed to recognize the number")
            false
        }
    }

    private fun park(request: List<String>): String {
        if (parkingSpace.isEmpty()) return "Sorry, a parking lot has not been created."

        if (!parkingSpace.containsValue(null)) return "Sorry, the parking lot is full."

        val (num, color) = request.slice(1..2)
        var car = Car(num,color)

        parkingSpace.forEach { (key, value) ->
            if (value == null) {
                parkingSpace[key] = car
                return "${car.color} car parked in spot $key."
            }
        }
        return ""
    }

    private fun leave(request: List<String>): String {
        if (parkingSpace.isEmpty()) return "Sorry, a parking lot has not been created."

        try {
            val position = request.last().toInt()

            parkingSpace.forEach { (key, value) ->
                if (key == position) {
                    return if (value == null) {
                        "There is no car in spot $position."
                    } else {
                        parkingSpace[key] = null
                        "Spot $key is free."
                    }
                }
            }
            return ""
        } catch (e: NumberFormatException) {
            return "Failed to recognize the number"
        }
    }

    private fun status() {
        if (parkingSpace.isEmpty()) {
            println("Sorry, a parking lot has not been created.")
            return
        }

        if (parkingSpace.values.stream().allMatch(Objects::isNull)) {
            println("Parking lot is empty.")
            return
        }

        parkingSpace.forEach { (key, value) ->
            if (value != null) {
                println("$key ${value.registrationNum.uppercase()} ${value.color}")
            }
        }
    }

    private fun queryCommands(request: List<String>): String {
        if (parkingSpace.isEmpty()) return "Sorry, a parking lot has not been created."

        var info = mutableListOf<String>()

        val (flag, searchValue) = request.slice(0..1)

        parkingSpace.forEach { (key, value) ->
            if (value != null) {
                when(flag) {
                    "reg_by_color" -> {
                        if (value.color == searchValue) info.add(value.registrationNum.uppercase())
                    }
                    "spot_by_color" -> if (value.color == searchValue) info.add(key.toString())
                    "spot_by_reg" -> if (value.registrationNum == searchValue) info.add(key.toString())
                }
            }
        }

        if(info.isEmpty()) {
            when(flag) {
                "reg_by_color","spot_by_color" -> return "No cars with color ${searchValue.uppercase()} were found."
                "spot_by_reg" -> return "No cars with registration number ${searchValue.uppercase()} were found."
            }
        }
        return info.joinToString(", ")
    }
}