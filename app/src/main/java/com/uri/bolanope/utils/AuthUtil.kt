package com.uri.bolanope.utils

import android.util.Log
import com.auth0.android.jwt.JWT

fun decodeJWT(token: String): String? {
    Log.d("token2", token)
    val jwt = JWT(token)

    val payload = jwt.claims

    val userId = payload["sub"]?.asString()

    return userId
}