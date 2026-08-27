package com.example.services

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log

object DndHelper {
    private const val TAG = "DndHelper"
    private var previousInterruptionFilter: Int? = null

    /**
     * Checks whether the app has permission to access and modify Do Not Disturb policy.
     */
    fun hasDndPermission(context: Context): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.isNotificationPolicyAccessGranted
        } else {
            true
        }
    }

    /**
     * Returns an intent to prompt the user to grant Do Not Disturb policy access.
     */
    fun getDndPermissionIntent(): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
    }

    /**
     * Activates Zen / DND mode for a prayer session.
     * Saves the current filter to restore it seamlessly at the end of prayer.
     */
    fun activateDnd(context: Context): Boolean {
        if (!hasDndPermission(context)) return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                    ?: return false
                if (previousInterruptionFilter == null) {
                    previousInterruptionFilter = notificationManager.currentInterruptionFilter
                }
                // Set to Priority Only (Alarms and priority remain active)
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
                Log.d(TAG, "Mode Ne Pas Déranger activé pour la prière.")
                return true
            } catch (e: Exception) {
                Log.e(TAG, "Erreur activation DND", e)
            }
        }
        return false
    }

    /**
     * Restores previous DND state when prayer finishes.
     */
    fun restoreDnd(context: Context): Boolean {
        if (!hasDndPermission(context)) return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                    ?: return false
                val targetFilter = previousInterruptionFilter ?: NotificationManager.INTERRUPTION_FILTER_ALL
                notificationManager.setInterruptionFilter(targetFilter)
                previousInterruptionFilter = null
                Log.d(TAG, "Mode Ne Pas Déranger restauré.")
                return true
            } catch (e: Exception) {
                Log.e(TAG, "Erreur restauration DND", e)
            }
        }
        return false
    }
}
