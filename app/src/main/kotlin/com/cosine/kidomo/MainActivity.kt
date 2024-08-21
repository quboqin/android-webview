package com.cosine.kidomo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.cosine.kidomo.ui.ExampleAppTheme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

/**
 * The singular [ComponentActivity] that is used to display the web app inside
 * the [WebView].
 */
class MainActivity : ComponentActivity()
{
	private val tag = "MainActivity"

	// Declare the launcher at the top of your Activity/Fragment:
	private val requestPermissionLauncher = registerForActivityResult(
		ActivityResultContracts.RequestPermission(),
	) { isGranted: Boolean ->
		if (isGranted) {
			// FCM SDK (and your app) can post notifications.
		} else {
			// TODO: Inform user that that your app will not show notifications.
		}
	}

	private fun askNotificationPermission() {
		// This is only necessary for API level >= 33 (TIRAMISU)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
				PackageManager.PERMISSION_GRANTED
			) {
				// FCM SDK (and your app) can post notifications.
			} else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
				// TODO: display an educational UI explaining to the user the features that will be enabled
				//       by them granting the POST_NOTIFICATION permission. This UI should provide the user
				//       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
				//       If the user selects "No thanks," allow the user to continue without notifications.
			} else {
				// Directly ask for the permission
				requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		// Enables WebView debugging in Chrome. To debug web app:
		//  1. Connect Android device to computer
		//  2. Open Chrome web browser
		//  3. navigate to `chrome://inspect`
		//  4. Click "inspect" below your listed device.
		WebView.setWebContentsDebuggingEnabled(true)

		FirebaseApp.initializeApp(this)
		FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
			if (!task.isSuccessful) {
				Log.w(tag, "Fetching FCM registration token failed", task.exception)
				return@OnCompleteListener
			}

			// Get new FCM registration token
			val token = task.result

			// Log and toast
			Log.i(tag, "token = $token")
			Toast.makeText(
				WebApp.app,
				"get a token",
				Toast.LENGTH_SHORT
			).show()
		})

		// Setup the view using Jetpack Compose.
		setContent {
			askNotificationPermission()
			// Check to see if we have the appropriate permissions.
			ConditionallyRequestPermission(Manifest.permission.CAMERA)
			{
				if (!it)
				{
					Toast.makeText(
						WebApp.app,
						"Did not grant permission to use camera.",
						Toast.LENGTH_SHORT
					).show()
				}
			}
			ExampleAppTheme {
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colors.background)
				{
					AppNavHost()
				}
			}
		}
	}
}

/**
 * Conditionally request an app [permission][Manifest.permission].
 *
 * @param permission
 *   The String representation of the permission to request.
 * @param resultHandler
 *   Accepts `true` if the permission is granted; `false` if not.
 */
@Composable
fun ConditionallyRequestPermission (
	permission: String,
	resultHandler: (Boolean) -> Unit)
{
	val permissionFlag =
		ContextCompat.checkSelfPermission(WebApp.app, permission)
	if (permissionFlag != PackageManager.PERMISSION_GRANTED)
	{
		val launcher = rememberLauncherForActivityResult(
			ActivityResultContracts.RequestPermission(), resultHandler)
		SideEffect {
			launcher.launch(permission)
		}
	}
	else
	{
		resultHandler(true)
	}
}