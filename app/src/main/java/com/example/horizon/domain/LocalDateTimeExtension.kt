package com.example.horizon.domain

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Returns the current hour in a consistent 12-hour format string with AM/PM,
 * ensuring a constant length by replacing a leading '0' with a space.
 *
 * Format used: `"hh a"` (e.g., "01 AM", "10 PM").
 *
 * Examples:
 * - `10 AM` → `"10 AM"`
 * - `01 AM` → `" 1 AM"` (space-padded to maintain consistent string length)
 */
val LocalDateTime.hourStringInTwelveHourFormat: String
    get() {
        val formatter = DateTimeFormatter.ofPattern("hh a")
        return format(formatter).replaceFirst("^0".toRegex(), " ")
    }
