package com.example.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.MessageTemplate
import com.example.data.model.ScheduleRule
import com.example.receiver.MessageReminderReceiver

object MessageScheduler {

    const val CHANNEL_ID = "crystal_message_reminders"
    const val CHANNEL_NAME = "Message Reminders"

    const val EXTRA_TEMPLATE_ID = "extra_template_id"
    const val EXTRA_TEMPLATE_TITLE = "extra_template_title"
    const val EXTRA_MESSAGE_TEXT = "extra_message_text"
    const val EXTRA_RECIPIENT = "extra_recipient"
    const val EXTRA_PLATFORM = "extra_platform"

    const val ACTION_OPEN_MESSAGE_READY = "com.example.crystalkeyboard.ACTION_OPEN_MESSAGE_READY"

    fun initNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Timely reminders to send your scheduled messages"
                enableVibration(true)
                enableLights(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    fun showReminderNotification(
        context: Context,
        templateId: String,
        title: String,
        messageContent: String,
        recipient: String,
        platformName: String
    ) {
        initNotificationChannel(context)

        val notificationId = templateId.hashCode()

        // 1. Open Message Ready Action Intent
        val openIntent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_OPEN_MESSAGE_READY
            putExtra(EXTRA_TEMPLATE_ID, templateId)
            putExtra(EXTRA_TEMPLATE_TITLE, title)
            putExtra(EXTRA_MESSAGE_TEXT, messageContent)
            putExtra(EXTRA_RECIPIENT, recipient)
            putExtra(EXTRA_PLATFORM, platformName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            notificationId * 10 + 1,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 2. Copy Message Action Intent
        val copyIntent = Intent(context, MessageReminderReceiver::class.java).apply {
            action = MessageReminderReceiver.ACTION_COPY
            putExtra(EXTRA_MESSAGE_TEXT, messageContent)
        }
        val copyPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 2,
            copyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 3. Snooze Action Intent
        val snoozeIntent = Intent(context, MessageReminderReceiver::class.java).apply {
            action = MessageReminderReceiver.ACTION_SNOOZE
            putExtra(EXTRA_TEMPLATE_ID, templateId)
            putExtra(EXTRA_TEMPLATE_TITLE, title)
            putExtra(EXTRA_MESSAGE_TEXT, messageContent)
            putExtra(EXTRA_RECIPIENT, recipient)
            putExtra(EXTRA_PLATFORM, platformName)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 3,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("$title ☀️")
            .setContentText("Your scheduled message is ready: \"$messageContent\"")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Your scheduled message for $recipient via $platformName is ready:\n\n\"$messageContent\""))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)
            .addAction(0, "Open", openPendingIntent)
            .addAction(0, "Copy", copyPendingIntent)
            .addAction(0, "Snooze (10m)", snoozePendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // In case notification permission wasn't granted yet
        }
    }

    fun scheduleAlarm(
        context: Context,
        rule: ScheduleRule,
        template: MessageTemplate,
        resolvedText: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, MessageReminderReceiver::class.java).apply {
            action = MessageReminderReceiver.ACTION_REMINDER
            putExtra(EXTRA_TEMPLATE_ID, template.id)
            putExtra(EXTRA_TEMPLATE_TITLE, template.title)
            putExtra(EXTRA_MESSAGE_TEXT, resolvedText)
            putExtra(EXTRA_RECIPIENT, template.recipientName)
            putExtra(EXTRA_PLATFORM, template.platform.name)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            template.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAt = rule.nextTriggerTime.coerceAtLeast(System.currentTimeMillis() + 1000)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
                    } else {
                        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
                }
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            }
        } catch (e: Exception) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }

    fun cancelAlarm(context: Context, templateId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, MessageReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            templateId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
