package com.example.horizon.data.remote.weather

/**
 * This object contains used by the [WeatherClient].
 */
object WeatherClientConstants {

    /**
     * The base URL of the OpenWeatherMap API.
     */
    const val BASE_URL = "https://api.open-meteo.com/v1/"

    /**
     * The endpoints of the OpenWeatherMap API
     */
    object EndPoints {
        const val GET_WEATHER_ENDPOINT = "forecast"
    }

    /**
     * Used to configure the units returned by the OpenWeatherMap API
     */
    object Units {
        object TemperatureUnits {
            const val CELSIUS = "celsius"
            const val FAHRENHEIT = "fahrenheit"
        }

        object WindSpeedUnit {
            const val KILOMETERS_PER_HOUR = "kmh"
            const val MILES_PER_HOUR = "mph"
        }

        object PrecipitationUnit {
            const val MILLIMETERS = "mm"
            const val INCHES = "inch"
        }
    }
}