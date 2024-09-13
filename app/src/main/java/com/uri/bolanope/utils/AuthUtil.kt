package com.uri.bolanope.utils

import android.util.Log
import com.auth0.android.jwt.JWT

fun decodeJWT(token: String): Pair<String?, String?> {
    val jwt = JWT(token)

    val payload = jwt.claims

    val userId = payload["userId"]?.asString()

    val role = payload["role"]?.asString()

    return Pair(userId, role)
}