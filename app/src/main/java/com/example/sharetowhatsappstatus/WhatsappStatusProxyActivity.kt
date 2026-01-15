package com.example.sharetowhatsappstatus

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity

/**
 * A proxy transparent activity to call whatsapp deeplink activity
 *
 * 1. FOR-RESULT REQUIREMENT: WhatsApp's Status deep-link often expects to be started via
 * startActivityForResult. The System Share Sheet (via PendingIntent) cannot do this directly.
 * 2. FOREGROUND AUTHORITY: Android restricts "Background Activity Launches" (BAL). By opening
 * this transparent activity first, we move into the foreground, which gives us the authority
 * to reliably launch WhatsApp without being blocked by the OS.
 * 3. URI PERMISSION CHAIN: Permissions granted to this Proxy are easily delegated to WhatsApp
 * within the same task stack, ensuring media isn't blocked by security exceptions.
 */
class WhatsappStatusProxyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Extract the nested intent
        val targetIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(MainActivity.EXTRA_TARGET_INTENT, Intent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(MainActivity.EXTRA_TARGET_INTENT)
        }

        if (targetIntent != null) {
            try {
                // Launch the intent that was passed in
                startActivityForResult(targetIntent, 0)
            } catch (e: Exception) {
                finish()
            }
        } else {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        finish() // Close the proxy once the target activity returns
    }
}
