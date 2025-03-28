package com.example.horizon.domain.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * A data class that represents the precipitation probability.
 * Note: It's better to have the time as an instance of LocalDateTime. This will allow for additional
 * features such as accommodating for 12/24 hr time format preference of the user. Since, the main motive
 * of this app, is not internationalization/user specific customization, LocalDateTime is not used here.
 */
data class PrecipitationProbability(
    val latitude: String,
    val longitude: String,
    val dateTime: LocalDateTime,
    val probabilityPercentage: Int
)

/**
 * A string that contains the current hour in the following format - "hh a". It also ensures that
 * the length of the string will be consistent regardless of whether the current hour is a single
 * digit or not.
 *
 * Examples :
 *  - "10 AM" -> "10 AM"
 *  - "01 AM" -> " 1 AM"
 */
val PrecipitationProbability.hourStringInTwelveHourFormat: String
    get() {
        //h - Hour in am/pm (1-12)
        //m - Minute in hour
        //a - Am/pm marker
        val dateTimeFormatter = DateTimeFormatter.ofPattern("hh a")
        // Add empty characters to the start of the string if the hour is a single digit number
        // to ensure that the length of the hour text remains constant, regardless of whether
        // the hour text is a single digit or not.
        return dateTime.format(dateTimeFormatter).let {
            if (it.startsWith("0")) it.replaceFirst('0', ' ')
            else it
        }
    }
