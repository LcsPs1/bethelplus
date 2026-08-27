package com.example.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.BethelDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PrayerAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (action == "com.example.bethelplus.ACTION_SPIRITUAL_WAKEUP") {
            val morningVerses = listOf(
                Pair("Psaume 143:8", "Fais-moi dès le matin entendre ta bonté ! Car je me confie en toi. Fais-moi connaître le chemin où je dois marcher !"),
                Pair("Lamentations 3:22-23", "Les bontés de l'Éternel ne sont pas épuisées, ses compassions ne sont pas à leur terme ; elles se renouvellent chaque matin. Grande est ta fidélité !"),
                Pair("Psaume 5:4", "Éternel ! le matin tu entends ma voix ; Le matin je me tourne vers toi, et je regarde."),
                Pair("Ésaïe 50:4", "Il éveille, chaque matin, il éveille mon oreille, Pour que j'écoute comme écoutent des disciples.")
            )
            val selected = morningVerses.random()

            NotificationHelper.showSpiritualWakeupNotification(
                context = context,
                verseRef = selected.first,
                verseText = selected.second
            )

            // Reschedule wake-up alarm for tomorrow
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                val db = BethelDatabase.getDatabase(context, scope)
                val alarm = db.bethelDao().getSpiritualAlarmSync()
                if (alarm != null && alarm.isEnabled) {
                    NotificationHelper.scheduleSpiritualAlarm(context, alarm.hour, alarm.minute)
                }
            }
            return
        }

        val reminderId = intent.getIntExtra("EXTRA_REMINDER_ID", 100)
        val reminderTitle = intent.getStringExtra("EXTRA_REMINDER_TITLE") ?: "Temps de Prière"

        val spiritualQuotes = listOf(
            "« C'est ici la maison de Dieu, c'est ici la porte des cieux. » Entrez dans le repos et la prière.",
            "Prenez quelques minutes pour faire silence et écouter Sa voix bienveillante.",
            "Le Seigneur vous attend dans le lieu secret pour renouveler vos forces.",
            "« Ne vous inquiétez de rien, mais en toute chose faites connaître vos besoins à Dieu. »",
            "Mon âme, bénis l'Éternel et n'oublie aucun de ses bienfaits."
        )
        val message = spiritualQuotes.random()

        NotificationHelper.showInstantPrayerNotification(
            context = context,
            notificationId = reminderId,
            title = reminderTitle,
            message = message
        )

        // Reschedule next occurrence for tomorrow
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val db = BethelDatabase.getDatabase(context, scope)
            val reminders = db.bethelDao().getActiveReminders().firstOrNull()
            val currentReminder = reminders?.find { it.id == reminderId }
            if (currentReminder != null && currentReminder.isEnabled) {
                NotificationHelper.scheduleReminder(context, currentReminder)
            }
        }
    }
}
