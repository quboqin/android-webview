package com.cosine.kidomo

import android.content.Context
import android.util.Log
import okhttp3.*
import java.io.IOException

class NetworkHelper(private val context: Context) {

    fun sendTokenUpdateRequest(token: String, bladeAuth: String, authorization: String) {
        // Set up OkHttpClient and the request
        val client = OkHttpClient()

        val endpoint = BuildConfig.ENDPOINT
        Log.d("Network Helper", "Endpoint URL: $endpoint")

        val request = Request.Builder()
            .url("${endpoint}token=${token}&&platform=android")
            .header("Blade-Auth", bladeAuth)
            .header("Authorization", authorization)
            .build()

        // Execute the request asynchronously
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    // Handle the error
                } else {
                    // Handle the response
                }
            }
        })
    }
}