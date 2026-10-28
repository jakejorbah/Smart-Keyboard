package com.example.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.data.KeyboardPreferences
import com.example.data.model.MessagingPlatform
import com.example.data.model.ReminderStatus
import com.example.data.model.ScheduledReminderHistory
import com.example.util.MessageScheduler
import com.example.util.PlatformSender

class MessageReminderReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_REMINDER = "com.example.crystalkeyboard.ACTION_REMINDER"
        const val ACTION_COPY = "com.example.crystalkeyboard.ACTION_COPY"
        const val ACTION_SNOOZE = "com.example.crystalkeyboard.ACTION_SNOOZE"
        const val ACTION_SKIP = "com.example.crystalkeyboard.ACTION_SKIP"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val prefs = KeyboardPreferences.getInstance(context)

        val templateId = intent.getStringExtra(MessageScheduler.EXTRA_TEMPLATE_ID) ?: ""
        val title = intent.getStringExtra(MessageScheduler.EXTRA_TEMPLATE_TITLE) ?: "Scheduled Message"
        val messageText = intent.getStringExtra(MessageScheduler.EXTRA_MESSAGE_TEXT) ?: ""
        val recipient = intent.getStringExtra(MessageScheduler.EXTRA_RECIPIENT) ?: ""
        val platformStr = intent.getStringExtra(MessageScheduler.EXTRA_PLATFORM) ?: MessagingPlatform.SMS.name

        val platform = try {
            MessagingPlatform.valueOf(platformStr)
        } catch (e: Exception) {
            MessagingPlatform.SMS
        }

        when (action) {
            ACTION_REMINDER -> {
                // Show notification to user
                MessageScheduler.showReminderNotification(
                    context = context,
                    templateId = templateId,
                    title = title,
                    messageContent = messageText,
                    recipient = recipient,
                    platformName = platform.displayName
                )

                // Record in history as PENDING
                prefs.addHistoryEntry(
                    ScheduledReminderHistory(
                        id = System.currentTimeMillis().toString(),
                        templateId = templateId,
                        templateTitle = title,
                        messageContent = messageText,
                        recipient = recipient,
                        platform = platform,
                        scheduledTime = System.currentTimeMillis(),
                        status = ReminderStatus.PENDING,
                        actionLog = "Reminder triggered"
                    )
                )

                // Advance schedule to next occurrence if recurring
                prefs.advanceScheduleNextOccurrence(context, templateId)
            }

            ACTION_COPY -> {
                PlatformSender.copyToClipboard(context, messageText, "Scheduled Message")
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                manager?.cancel(templateId.hashCode())
            }

            ACTION_SNOOZE -> {
                val snoozeMinutes = 10
                Toast.makeText(context, "Snoozed for $snoozeMinutes minutes", Toast.LENGTH_SHORT).show()
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                manager?.cancel(templateId.hashCode())

                // Reschedule for 10 minutes later
                val snoozeTime = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000)
                prefs.snoozeReminder(context, templateId, snoozeTime)
            }

            ACTION_SKIP -> {
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                manager?.cancel(templateId.hashCode())
                Toast.makeText(context, "Occurrence skipped", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
