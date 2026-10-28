package com.example.engine

import com.example.data.model.MessageTemplate
import com.example.data.model.RepeatType
import com.example.data.model.ScheduleRule
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TemplateEngine {

    val StarterTemplates = listOf(
        MessageTemplate(
            id = "starter_good_morning",
            title = "Good Morning",
            content = "Good morning, {name}! ☀️ Wishing you a beautiful, joyful, and productive {day}.",
            category = "Daily Greetings",
            recipientName = "Friend",
            isFavorite = true
        ),
        MessageTemplate(
            id = "starter_lunch",
            title = "Lunch Reminder",
            content = "Have you had lunch yet? 🍽️ Don't forget to take a break and nourish yourself!",
            category = "Lunch Reminders",
            recipientName = ""
        ),
        MessageTemplate(
            id = "starter_checkin",
            title = "Daily Check-In",
            content = "Hi {name}! Just checking in to see how you're doing today. Hope all is going smoothly! ✨",
            category = "Check-Ins",
            recipientName = ""
        ),
        MessageTemplate(
            id = "starter_followup",
            title = "Work Follow-Up",
            content = "Hello! Just following up on our previous conversation regarding the project update. Please let me know when you have a moment.",
            category = "Work Updates",
            recipientName = ""
        ),
        MessageTemplate(
            id = "starter_good_night",
            title = "Good Night",
            content = "Good night, {name}! 🌙 Rest well, recharge, and have wonderful sweet dreams.",
            category = "Evening Greetings",
            recipientName = "Love",
            isFavorite = true
        ),
        MessageTemplate(
            id = "starter_thank_you",
            title = "Thank You",
            content = "Thank you so much, {name}! I truly appreciate your help, support, and kindness. 🙏",
            category = "Thank You",
            recipientName = ""
        ),
        MessageTemplate(
            id = "starter_birthday",
            title = "Happy Birthday",
            content = "Happy birthday, {name}! 🎂🎉 Wishing you tremendous happiness, great health, and continued success this year!",
            category = "Celebrations",
            recipientName = ""
        )
    )

    fun resolveTemplateVariables(
        templateText: String,
        recipientName: String = "",
        customNickname: String = "",
        customText: String = "",
        date: Date = Date()
    ): String {
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        val resolvedName = when {
            recipientName.isNotBlank() -> recipientName
            customNickname.isNotBlank() -> customNickname
            else -> "there"
        }
        val resolvedNickname = if (customNickname.isNotBlank()) customNickname else resolvedName

        var result = templateText
        result = result.replace("{name}", resolvedName, ignoreCase = true)
        result = result.replace("{nickname}", resolvedNickname, ignoreCase = true)
        result = result.replace("{date}", dateFormat.format(date), ignoreCase = true)
        result = result.replace("{day}", dayFormat.format(date), ignoreCase = true)
        result = result.replace("{time}", timeFormat.format(date), ignoreCase = true)
        result = result.replace("{custom_text}", customText, ignoreCase = true)
        return result
    }

    fun findMissingVariables(templateText: String): List<String> {
        val regex = Regex("\\{([a-zA-Z0-9_]+)\\}")
        return regex.findAll(templateText).map { it.groupValues[1] }.distinct().toList()
    }

    fun getTimeBasedSuggestions(
        templates: List<MessageTemplate>,
        currentHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    ): List<MessageTemplate> {
        val targetCategory = when (currentHour) {
            in 6..10 -> "Daily Greetings"
            in 11..13 -> "Lunch Reminders"
            in 14..16 -> "Work Updates"
            in 17..20 -> "Check-Ins"
            else -> "Evening Greetings"
        }

        val matching = templates.filter { it.category.equals(targetCategory, ignoreCase = true) }
        val favorites = templates.filter { it.isFavorite && !matching.contains(it) }
        val rest = templates.filter { !matching.contains(it) && !favorites.contains(it) }

        return (matching + favorites + rest)
    }

    fun calculateNextOccurrence(rule: ScheduleRule, fromTimestamp: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = fromTimestamp
            set(Calendar.HOUR_OF_DAY, rule.hour)
            set(Calendar.MINUTE, rule.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If time already passed today, advance by at least 1 day
        if (cal.timeInMillis <= fromTimestamp) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }

        when (rule.repeatType) {
            RepeatType.ONCE -> {
                // If start date is in future, use start date's day with rule's hour/minute
                if (rule.startDate > fromTimestamp) {
                    val startCal = Calendar.getInstance().apply { timeInMillis = rule.startDate }
                    cal.set(Calendar.YEAR, startCal.get(Calendar.YEAR))
                    cal.set(Calendar.DAY_OF_YEAR, startCal.get(Calendar.DAY_OF_YEAR))
                }
                return cal.timeInMillis
            }
            RepeatType.DAILY -> {
                return cal.timeInMillis
            }
            RepeatType.WEEKDAYS -> {
                while (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
                return cal.timeInMillis
            }
            RepeatType.WEEKENDS -> {
                while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
                return cal.timeInMillis
            }
            RepeatType.WEEKLY -> {
                // Same weekday as start date
                val startCal = Calendar.getInstance().apply { timeInMillis = rule.startDate }
                val targetDayOfWeek = startCal.get(Calendar.DAY_OF_WEEK)
                while (cal.get(Calendar.DAY_OF_WEEK) != targetDayOfWeek) {
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
                return cal.timeInMillis
            }
            RepeatType.MONTHLY -> {
                val startCal = Calendar.getInstance().apply { timeInMillis = rule.startDate }
                val targetDayOfMonth = startCal.get(Calendar.DAY_OF_MONTH)
                cal.set(Calendar.DAY_OF_MONTH, targetDayOfMonth)
                if (cal.timeInMillis <= fromTimestamp) {
                    cal.add(Calendar.MONTH, 1)
                }
                return cal.timeInMillis
            }
        }
    }
}
