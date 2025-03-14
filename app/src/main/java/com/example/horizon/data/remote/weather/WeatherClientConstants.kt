package com.example.horizon.data.remote.weather

object WeatherClientConstants {

    // The base URL of the OpenWeather API
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    // The endpoints of the OpenWeatherMap API
    object EndPoints {
        const val GET_WEATHER_ENDPOINT = "weather"
    }

    // Used to configure the units returned by the OpenWeatherMap API
    object Units {
        const val CELSIUS = "metric"
        const val FAHRENHEIT = "imperial"
    }
}