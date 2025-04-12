package com.project.horizon.data

import retrofit2.Response

/**
 * Returns the non-null [body] of the Retrofit [Response], or throws an [Exception] with
 * detailed information if the body is null.
 *
 * This is typically used after a successful network call (e.g., [Response.isSuccessful] is true),
 * but where Retrofit might still return a null body.
 *
 * @throws Exception if the response body is null. The exception message includes the HTTP
 * response [code], [message], and the [errorBody] if available.
 *
 * @return The non-null response body of type [T].
 */
fun <T> Response<T>.getBodyOrThrowException(): T {
    return body() ?: throw Exception(
        buildString {
            append("HTTP ${code()}: ${message()}")
            errorBody()?.string()?.let { errorContent ->
                append(" | Error body: $errorContent")
            }
        }
    )
}
