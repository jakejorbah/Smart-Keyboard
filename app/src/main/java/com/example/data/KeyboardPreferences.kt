package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.ClipboardSnippet
import com.example.data.model.KeyboardSettings
import com.example.data.model.KeyboardTheme
import com.example.data.model.MessageTemplate
import com.example.data.model.MessagingPlatform
import com.example.data.model.OneHandedMode
import com.example.data.model.ReminderStatus
import com.example.data.model.RepeatType
import com.example.data.model.ScheduleRule
import com.example.data.model.ScheduledReminderHistory
import com.example.data.model.TextShortcut
import com.example.engine.TemplateEngine
import com.example.util.MessageScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class KeyboardPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("crystal_keyboard_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<KeyboardSettings> = _settings.asStateFlow()

    private val _shortcuts = MutableStateFlow(loadShortcuts())
    val shortcuts: StateFlow<List<TextShortcut>> = _shortcuts.asStateFlow()

    private val _snippets = MutableStateFlow(loadSnippets())
    val snippets: StateFlow<List<ClipboardSnippet>> = _snippets.asStateFlow()

    private val _customWords = MutableStateFlow(loadCustomWords())
    val customWords: StateFlow<Set<String>> = _customWords.asStateFlow()

    private val _templates = MutableStateFlow(loadTemplates())
    val templates: StateFlow<List<MessageTemplate>> = _templates.asStateFlow()

    private val _schedules = MutableStateFlow(loadSchedules())
    val schedules: StateFlow<List<ScheduleRule>> = _schedules.asStateFlow()

    private val _history = MutableStateFlow(loadHistory())
    val history: StateFlow<List<ScheduledReminderHistory>> = _history.asStateFlow()

    init {
        // Defaults for shortcuts
        if (_shortcuts.value.isEmpty()) {
            val defaults = listOf(
                TextShortcut("1", "omw", "On my way!"),
                TextShortcut("2", "brb", "Be right back!"),
                TextShortcut("3", "ty", "Thank you so much!"),
                TextShortcut("4", "np", "No problem at all!"),
                TextShortcut("5", "idk", "I don't know"),
                TextShortcut("6", "lmk", "Let me know"),
                TextShortcut("7", "btw", "By the way"),
                TextShortcut("8", "email", "contact@crystalkeyboard.app")
            )
            saveShortcuts(defaults)
        }

        // Defaults for snippets
        if (_snippets.value.isEmpty()) {
            val defaultSnippets = listOf(
                ClipboardSnippet("1", "Thanks! Looking forward to it.", isPinned = true),
                ClipboardSnippet("2", "Running 5 minutes late, will be there shortly!", isPinned = true),
                ClipboardSnippet("3", "Sounds good to me. Let's do it!", isPinned = false),
                ClipboardSnippet("4", "Can you send the details whenever you're free?", isPinned = false)
            )
            saveSnippets(defaultSnippets)
        }

        // Defaults for templates
        if (_templates.value.isEmpty()) {
            saveTemplates(TemplateEngine.StarterTemplates)

            // Seed default daily 7:00 AM schedule for Good Morning
            val morningRule = ScheduleRule(
                id = "rule_default_morning",
                templateId = "starter_good_morning",
                repeatType = RepeatType.DAILY,
                hour = 7,
                minute = 0,
                isEnabled = true
            )
            val nextTime = TemplateEngine.calculateNextOccurrence(morningRule)
            saveSchedules(listOf(morningRule.copy(nextTriggerTime = nextTime)))
        }
    }

    private fun loadSettings(): KeyboardSettings {
        val themeId = prefs.getString("theme_id", KeyboardTheme.CrystalClear.id) ?: KeyboardTheme.CrystalClear.id
        val theme = KeyboardTheme.getById(themeId)

        return KeyboardSettings(
            themeId = themeId,
            keyboardOpacity = prefs.getFloat("keyboard_opacity", theme.keyboardOpacity),
            keycapOpacity = prefs.getFloat("keycap_opacity", theme.keycapOpacity),
            cornerRadius = prefs.getInt("corner_radius", 10),
            keySpacing = prefs.getInt("key_spacing", 5),
            keyboardHeightFactor = prefs.getFloat("keyboard_height_factor", 1.0f),
            borderWidth = prefs.getFloat("border_width", 1.0f),
            borderOpacity = prefs.getFloat("border_opacity", 0.45f),
            blurIntensity = prefs.getInt("blur_intensity", 60),
            backgroundTintColor = prefs.getLong("bg_tint_color", 0x00000000L),
            tintOpacity = prefs.getFloat("tint_opacity", 0.35f),
            keypressAnimationEnabled = prefs.getBoolean("keypress_anim", true),
            respectReducedMotion = prefs.getBoolean("respect_reduced_motion", true),
            hapticFeedback = prefs.getBoolean("haptic_feedback", true),
            hapticIntensity = prefs.getFloat("haptic_intensity", 1.0f),
            soundFeedback = prefs.getBoolean("sound_feedback", false),
            autoCapitalization = prefs.getBoolean("auto_cap", true),
            doubleSpacePeriod = prefs.getBoolean("double_space", true),
            suggestionsEnabled = prefs.getBoolean("suggestions", true),
            autoCorrectEnabled = prefs.getBoolean("autocorrect", true),
            numberRowEnabled = prefs.getBoolean("number_row", false),
            oneHandedMode = OneHandedMode.valueOf(prefs.getString("one_handed", OneHandedMode.NONE.name) ?: OneHandedMode.NONE.name),
            activeLanguage = prefs.getString("active_language", "English (US)") ?: "English (US)",
            customTintColor = prefs.getLong("custom_tint", 0xFF0F172AL),
            customKeyColor = prefs.getLong("custom_key", 0xFF1E293BL),
            customTextColor = prefs.getLong("custom_text", 0xFFFFFFFFL)
        )
    }

    fun updateSettings(newSettings: KeyboardSettings) {
        prefs.edit().apply {
            putString("theme_id", newSettings.themeId)
            putFloat("keyboard_opacity", newSettings.keyboardOpacity)
            putFloat("keycap_opacity", newSettings.keycapOpacity)
            putInt("corner_radius", newSettings.cornerRadius)
            putInt("key_spacing", newSettings.keySpacing)
            putFloat("keyboard_height_factor", newSettings.keyboardHeightFactor)
            putFloat("border_width", newSettings.borderWidth)
            putFloat("border_opacity", newSettings.borderOpacity)
            putInt("blur_intensity", newSettings.blurIntensity)
            putLong("bg_tint_color", newSettings.backgroundTintColor)
            putFloat("tint_opacity", newSettings.tintOpacity)
            putBoolean("keypress_anim", newSettings.keypressAnimationEnabled)
            putBoolean("respect_reduced_motion", newSettings.respectReducedMotion)
            putBoolean("haptic_feedback", newSettings.hapticFeedback)
            putFloat("haptic_intensity", newSettings.hapticIntensity)
            putBoolean("sound_feedback", newSettings.soundFeedback)
            putBoolean("auto_cap", newSettings.autoCapitalization)
            putBoolean("double_space", newSettings.doubleSpacePeriod)
            putBoolean("suggestions", newSettings.suggestionsEnabled)
            putBoolean("autocorrect", newSettings.autoCorrectEnabled)
            putBoolean("number_row", newSettings.numberRowEnabled)
            putString("one_handed", newSettings.oneHandedMode.name)
            putString("active_language", newSettings.activeLanguage)
            putLong("custom_tint", newSettings.customTintColor)
            putLong("custom_key", newSettings.customKeyColor)
            putLong("custom_text", newSettings.customTextColor)
            apply()
        }
        _settings.value = newSettings
    }

    fun selectTheme(themeId: String) {
        val theme = KeyboardTheme.getById(themeId)
        val current = _settings.value
        val updated = current.copy(
            themeId = themeId,
            keyboardOpacity = theme.keyboardOpacity,
            keycapOpacity = theme.keycapOpacity
        )
        updateSettings(updated)
    }

    fun resetToDefaults() {
        prefs.edit().clear().apply()
        _settings.value = KeyboardSettings()
    }

    // Shortcuts
    private fun loadShortcuts(): List<TextShortcut> {
        val jsonStr = prefs.getString("shortcuts_json", null) ?: return emptyList()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<TextShortcut>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    TextShortcut(
                        id = obj.optString("id", i.toString()),
                        trigger = obj.getString("trigger"),
                        replacement = obj.getString("replacement"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveShortcuts(list: List<TextShortcut>) {
        val array = JSONArray()
        list.forEach {
            val obj = JSONObject().apply {
                put("id", it.id)
                put("trigger", it.trigger)
                put("replacement", it.replacement)
                put("timestamp", it.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString("shortcuts_json", array.toString()).apply()
        _shortcuts.value = list
    }

    fun addShortcut(trigger: String, replacement: String) {
        val newId = System.currentTimeMillis().toString()
        val list = _shortcuts.value.filter { it.trigger.lowercase() != trigger.lowercase() } + TextShortcut(newId, trigger.trim(), replacement.trim())
        saveShortcuts(list)
    }

    fun deleteShortcut(id: String) {
        val list = _shortcuts.value.filter { it.id != id }
        saveShortcuts(list)
    }

    // Snippets
    private fun loadSnippets(): List<ClipboardSnippet> {
        val jsonStr = prefs.getString("snippets_json", null) ?: return emptyList()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<ClipboardSnippet>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ClipboardSnippet(
                        id = obj.optString("id", i.toString()),
                        text = obj.getString("text"),
                        isPinned = obj.optBoolean("isPinned", false),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveSnippets(list: List<ClipboardSnippet>) {
        val array = JSONArray()
        list.forEach {
            val obj = JSONObject().apply {
                put("id", it.id)
                put("text", it.text)
                put("isPinned", it.isPinned)
                put("timestamp", it.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString("snippets_json", array.toString()).apply()
        _snippets.value = list
    }

    fun addSnippet(text: String, isPinned: Boolean = false) {
        val newId = System.currentTimeMillis().toString()
        val list = listOf(ClipboardSnippet(newId, text.trim(), isPinned)) + _snippets.value.filter { it.text != text.trim() }
        saveSnippets(list.take(30))
    }

    fun togglePinSnippet(id: String) {
        val list = _snippets.value.map {
            if (it.id == id) it.copy(isPinned = !it.isPinned) else it
        }
        saveSnippets(list)
    }

    fun deleteSnippet(id: String) {
        val list = _snippets.value.filter { it.id != id }
        saveSnippets(list)
    }

    fun clearSnippets() {
        val pinned = _snippets.value.filter { it.isPinned }
        saveSnippets(pinned)
    }

    // Custom Words
    private fun loadCustomWords(): Set<String> {
        return prefs.getStringSet("custom_words", emptySet()) ?: emptySet()
    }

    fun addCustomWord(word: String) {
        val words = _customWords.value + word.lowercase().trim()
        prefs.edit().putStringSet("custom_words", words).apply()
        _customWords.value = words
    }

    fun clearCustomWords() {
        prefs.edit().remove("custom_words").apply()
        _customWords.value = emptySet()
    }

    // Templates
    private fun loadTemplates(): List<MessageTemplate> {
        val jsonStr = prefs.getString("templates_json", null) ?: return emptyList()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<MessageTemplate>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    MessageTemplate(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        content = obj.getString("content"),
                        category = obj.optString("category", "General"),
                        recipientName = obj.optString("recipientName", ""),
                        recipientAddress = obj.optString("recipientAddress", ""),
                        platform = try {
                            MessagingPlatform.valueOf(obj.optString("platform", MessagingPlatform.SMS.name))
                        } catch (e: Exception) {
                            MessagingPlatform.SMS
                        },
                        isFavorite = obj.optBoolean("isFavorite", false),
                        isDraft = obj.optBoolean("isDraft", false),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                        updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveTemplates(list: List<MessageTemplate>) {
        val array = JSONArray()
        list.forEach {
            val obj = JSONObject().apply {
                put("id", it.id)
                put("title", it.title)
                put("content", it.content)
                put("category", it.category)
                put("recipientName", it.recipientName)
                put("recipientAddress", it.recipientAddress)
                put("platform", it.platform.name)
                put("isFavorite", it.isFavorite)
                put("isDraft", it.isDraft)
                put("createdAt", it.createdAt)
                put("updatedAt", it.updatedAt)
            }
            array.put(obj)
        }
        prefs.edit().putString("templates_json", array.toString()).apply()
        _templates.value = list
    }

    fun addOrUpdateTemplate(template: MessageTemplate) {
        val current = _templates.value
        val exists = current.any { it.id == template.id }
        val updated = if (exists) {
            current.map { if (it.id == template.id) template else it }
        } else {
            listOf(template) + current
        }
        saveTemplates(updated)
    }

    fun deleteTemplate(context: Context, templateId: String) {
        MessageScheduler.cancelAlarm(context, templateId)
        val updatedTemplates = _templates.value.filter { it.id != templateId }
        val updatedSchedules = _schedules.value.filter { it.templateId != templateId }
        saveTemplates(updatedTemplates)
        saveSchedules(updatedSchedules)
    }

    fun duplicateTemplate(templateId: String) {
        val original = _templates.value.find { it.id == templateId } ?: return
        val copy = original.copy(
            id = System.currentTimeMillis().toString(),
            title = "${original.title} (Copy)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        addOrUpdateTemplate(copy)
    }

    fun toggleFavoriteTemplate(templateId: String) {
        val updated = _templates.value.map {
            if (it.id == templateId) it.copy(isFavorite = !it.isFavorite) else it
        }
        saveTemplates(updated)
    }

    // Schedules
    private fun loadSchedules(): List<ScheduleRule> {
        val jsonStr = prefs.getString("schedules_json", null) ?: return emptyList()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<ScheduleRule>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val daysArray = obj.optJSONArray("selectedDays")
                val daysList = mutableListOf<Int>()
                if (daysArray != null) {
                    for (d in 0 until daysArray.length()) daysList.add(daysArray.getInt(d))
                } else {
                    daysList.addAll(listOf(1, 2, 3, 4, 5, 6, 7))
                }

                list.add(
                    ScheduleRule(
                        id = obj.getString("id"),
                        templateId = obj.getString("templateId"),
                        repeatType = try {
                            RepeatType.valueOf(obj.optString("repeatType", RepeatType.DAILY.name))
                        } catch (e: Exception) {
                            RepeatType.DAILY
                        },
                        selectedDays = daysList,
                        hour = obj.optInt("hour", 7),
                        minute = obj.optInt("minute", 0),
                        startDate = obj.optLong("startDate", System.currentTimeMillis()),
                        endDate = if (obj.has("endDate") && !obj.isNull("endDate")) obj.getLong("endDate") else null,
                        isEnabled = obj.optBoolean("isEnabled", true),
                        snoozeDurationMinutes = obj.optInt("snoozeDurationMinutes", 10),
                        leadTimeMinutes = obj.optInt("leadTimeMinutes", 0),
                        nextTriggerTime = obj.optLong("nextTriggerTime", 0L)
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveSchedules(list: List<ScheduleRule>) {
        val array = JSONArray()
        list.forEach {
            val obj = JSONObject().apply {
                put("id", it.id)
                put("templateId", it.templateId)
                put("repeatType", it.repeatType.name)
                put("selectedDays", JSONArray(it.selectedDays))
                put("hour", it.hour)
                put("minute", it.minute)
                put("startDate", it.startDate)
                put("endDate", it.endDate)
                put("isEnabled", it.isEnabled)
                put("snoozeDurationMinutes", it.snoozeDurationMinutes)
                put("leadTimeMinutes", it.leadTimeMinutes)
                put("nextTriggerTime", it.nextTriggerTime)
            }
            array.put(obj)
        }
        prefs.edit().putString("schedules_json", array.toString()).apply()
        _schedules.value = list
    }

    fun setScheduleForTemplate(context: Context, template: MessageTemplate, rule: ScheduleRule) {
        val nextTime = TemplateEngine.calculateNextOccurrence(rule)
        val updatedRule = rule.copy(nextTriggerTime = nextTime)

        // Cancel previous alarm if any
        MessageScheduler.cancelAlarm(context, template.id)

        val current = _schedules.value.filter { it.templateId != template.id }
        val updated = current + updatedRule
        saveSchedules(updated)

        if (updatedRule.isEnabled) {
            val resolvedText = TemplateEngine.resolveTemplateVariables(template.content, template.recipientName)
            MessageScheduler.scheduleAlarm(context, updatedRule, template, resolvedText)
        }
    }

    fun toggleScheduleEnabled(context: Context, templateId: String, isEnabled: Boolean) {
        val schedule = _schedules.value.find { it.templateId == templateId } ?: return
        val template = _templates.value.find { it.id == templateId } ?: return

        val updatedRule = if (isEnabled) {
            val nextTime = TemplateEngine.calculateNextOccurrence(schedule)
            schedule.copy(isEnabled = true, nextTriggerTime = nextTime)
        } else {
            schedule.copy(isEnabled = false)
        }

        val updatedList = _schedules.value.map { if (it.templateId == templateId) updatedRule else it }
        saveSchedules(updatedList)

        if (isEnabled) {
            val resolvedText = TemplateEngine.resolveTemplateVariables(template.content, template.recipientName)
            MessageScheduler.scheduleAlarm(context, updatedRule, template, resolvedText)
        } else {
            MessageScheduler.cancelAlarm(context, templateId)
        }
    }

    fun advanceScheduleNextOccurrence(context: Context, templateId: String) {
        val schedule = _schedules.value.find { it.templateId == templateId } ?: return
        val template = _templates.value.find { it.id == templateId } ?: return

        if (schedule.repeatType == RepeatType.ONCE) {
            // Disable one-time schedule after it fires
            val updated = _schedules.value.map {
                if (it.templateId == templateId) it.copy(isEnabled = false) else it
            }
            saveSchedules(updated)
            MessageScheduler.cancelAlarm(context, templateId)
        } else {
            val nextTime = TemplateEngine.calculateNextOccurrence(schedule, System.currentTimeMillis())
            val updatedRule = schedule.copy(nextTriggerTime = nextTime)
            val updated = _schedules.value.map {
                if (it.templateId == templateId) updatedRule else it
            }
            saveSchedules(updated)
            val resolvedText = TemplateEngine.resolveTemplateVariables(template.content, template.recipientName)
            MessageScheduler.scheduleAlarm(context, updatedRule, template, resolvedText)
        }
    }

    fun snoozeReminder(context: Context, templateId: String, snoozeTime: Long) {
        val schedule = _schedules.value.find { it.templateId == templateId } ?: return
        val template = _templates.value.find { it.id == templateId } ?: return

        val updatedRule = schedule.copy(nextTriggerTime = snoozeTime)
        val updated = _schedules.value.map {
            if (it.templateId == templateId) updatedRule else it
        }
        saveSchedules(updated)
        val resolvedText = TemplateEngine.resolveTemplateVariables(template.content, template.recipientName)
        MessageScheduler.scheduleAlarm(context, updatedRule, template, resolvedText)
    }

    fun rescheduleAllActiveAlarms(context: Context) {
        val templatesMap = _templates.value.associateBy { it.id }
        _schedules.value.filter { it.isEnabled }.forEach { rule ->
            val template = templatesMap[rule.templateId]
            if (template != null) {
                val nextTime = if (rule.nextTriggerTime > System.currentTimeMillis()) {
                    rule.nextTriggerTime
                } else {
                    TemplateEngine.calculateNextOccurrence(rule)
                }
                val updatedRule = rule.copy(nextTriggerTime = nextTime)
                val resolvedText = TemplateEngine.resolveTemplateVariables(template.content, template.recipientName)
                MessageScheduler.scheduleAlarm(context, updatedRule, template, resolvedText)
            }
        }
    }

    // History
    private fun loadHistory(): List<ScheduledReminderHistory> {
        val jsonStr = prefs.getString("history_json", null) ?: return emptyList()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<ScheduledReminderHistory>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ScheduledReminderHistory(
                        id = obj.getString("id"),
                        templateId = obj.getString("templateId"),
                        templateTitle = obj.getString("templateTitle"),
                        messageContent = obj.getString("messageContent"),
                        recipient = obj.optString("recipient", ""),
                        platform = try {
                            MessagingPlatform.valueOf(obj.optString("platform", MessagingPlatform.SMS.name))
                        } catch (e: Exception) {
                            MessagingPlatform.SMS
                        },
                        scheduledTime = obj.optLong("scheduledTime", System.currentTimeMillis()),
                        status = try {
                            ReminderStatus.valueOf(obj.optString("status", ReminderStatus.PENDING.name))
                        } catch (e: Exception) {
                            ReminderStatus.PENDING
                        },
                        confirmedSentTime = if (obj.has("confirmedSentTime") && !obj.isNull("confirmedSentTime")) obj.getLong("confirmedSentTime") else null,
                        actionLog = obj.optString("actionLog", "")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveHistory(list: List<ScheduledReminderHistory>) {
        val array = JSONArray()
        list.forEach {
            val obj = JSONObject().apply {
                put("id", it.id)
                put("templateId", it.templateId)
                put("templateTitle", it.templateTitle)
                put("messageContent", it.messageContent)
                put("recipient", it.recipient)
                put("platform", it.platform.name)
                put("scheduledTime", it.scheduledTime)
                put("status", it.status.name)
                put("confirmedSentTime", it.confirmedSentTime)
                put("actionLog", it.actionLog)
            }
            array.put(obj)
        }
        prefs.edit().putString("history_json", array.toString()).apply()
        _history.value = list
    }

    fun addHistoryEntry(entry: ScheduledReminderHistory) {
        val list = listOf(entry) + _history.value.take(49)
        saveHistory(list)
    }

    fun markHistoryAsSent(historyId: String) {
        val list = _history.value.map {
            if (it.id == historyId) {
                it.copy(
                    status = ReminderStatus.SENT_CONFIRMED,
                    confirmedSentTime = System.currentTimeMillis(),
                    actionLog = "User confirmed message sent"
                )
            } else it
        }
        saveHistory(list)
    }

    companion object {
        @Volatile
        private var instance: KeyboardPreferences? = null

        fun getInstance(context: Context): KeyboardPreferences {
            return instance ?: synchronized(this) {
                instance ?: KeyboardPreferences(context.applicationContext).also { instance = it }
            }
        }
    }
}
