package com.uri.bolanope.utils

import com.auth0.android.jwt.JWT

fun decodeJWT(token: String): String? {
    val jwt = JWT(token)

    val payload = jwt.claims

    val userId = payload["sub"]?.asString()

    return userId
}