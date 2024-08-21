package com.cosine.kidomo

import android.util.Log
import com.cosine.kidomo.Token
import com.cosine.kidomo.PreferenceHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {
    private var tag = "MessagingService"
    override fun onNewToken(token: String) {
        Log.d(tag, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        // sendRegistrationToServer(token)

        val preferenceHelper =
            PreferenceHelper<Token>(WebApp.app, "KidomoPreferences", "TokenKey")

        val token = Token(token)

        preferenceHelper.saveObject(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(tag, "Notification Message Body: " + message.notification!!.body)
        if (message.data.isNotEmpty()) {
            Log.d(tag, "Message Data payload: " + message.data)
        }
    }

}