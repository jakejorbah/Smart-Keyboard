package com.example.data.model

enum class MessagingPlatform(
    val displayName: String,
    val iconEmoji: String,
    val packageName: String?,
    val defaultScheme: String?
) {
    SMS("SMS / Messages", "💬", null, "smsto:"),
    WHATSAPP("WhatsApp", "🟢", "com.whatsapp", "https://api.whatsapp.com/send"),
    TELEGRAM("Telegram", "✈️", "org.telegram.messenger", "tg://msg"),
    MESSENGER("Messenger", "⚡", "com.facebook.orca", null),
    VIBER("Viber", "💜", "com.viber.voip", "viber://forward"),
    INSTAGRAM("Instagram Direct", "📸", "com.instagram.android", null),
    EMAIL("Email", "✉️", null, "mailto:"),
    OTHER("System Share", "🔗", null, null)
}

enum class RepeatType(val label: String) {
    ONCE("One Time"),
    DAILY("Every Day"),
    WEEKDAYS("Weekdays Only (Mon-Fri)"),
    WEEKENDS("Weekends Only (Sat-Sun)"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly")
}

enum class ReminderStatus(val label: String) {
    PENDING("Pending"),
    SENT_CONFIRMED("Marked as Sent (User Confirmed)"),
    SKIPPED("Skipped"),
    SNOOZED("Snoozed"),
    MISSED("Missed")
}

data class MessageTemplate(
    val id: String,
    val title: String,
    val content: String,
    val category: String = "Daily Greetings",
    val recipientName: String = "",
    val recipientAddress: String = "",
    val platform: MessagingPlatform = MessagingPlatform.SMS,
    val isFavorite: Boolean = false,
    val isDraft: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class ScheduleRule(
    val id: String,
    val templateId: String,
    val repeatType: RepeatType = RepeatType.DAILY,
    val selectedDays: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7), // 1=Mon .. 7=Sun
    val hour: Int = 7,
    val minute: Int = 0,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val isEnabled: Boolean = true,
    val snoozeDurationMinutes: Int = 10,
    val leadTimeMinutes: Int = 0,
    val nextTriggerTime: Long = 0L
)

data class ScheduledReminderHistory(
    val id: String,
    val templateId: String,
    val templateTitle: String,
    val messageContent: String,
    val recipient: String,
    val platform: MessagingPlatform,
    val scheduledTime: Long,
    val status: ReminderStatus,
    val confirmedSentTime: Long? = null,
    val actionLog: String = ""
)
