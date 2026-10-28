package com.example

import com.example.data.model.KeyboardTheme
import com.example.data.model.RepeatType
import com.example.data.model.ScheduleRule
import com.example.data.model.TextShortcut
import com.example.engine.EmojiData
import com.example.engine.TemplateEngine
import com.example.engine.TypingEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ExampleUnitTest {

    @Test
    fun testTypingEngineSuggestions() {
        val engine = TypingEngine()

        // Test prefix suggestion
        val suggestions = engine.getSuggestions(textBeforeCursor = "cryst")
        assertTrue("Suggestions should contain 'crystal'", suggestions.any { it.contains("crystal", ignoreCase = true) })

        // Test next word prediction
        val nextWords = engine.getSuggestions(textBeforeCursor = "thank ")
        assertTrue("Next words should include 'you'", nextWords.contains("you"))

        // Test shortcut expansion
        val shortcuts = listOf(TextShortcut("1", "omw", "On my way!"))
        val shortcutSuggestions = engine.getSuggestions(textBeforeCursor = "omw", shortcuts = shortcuts)
        assertEquals("On my way!", shortcutSuggestions.firstOrNull())
    }

    @Test
    fun testAutoCapitalization() {
        val engine = TypingEngine()
        assertTrue(engine.shouldAutoCapitalize(""))
        assertTrue(engine.shouldAutoCapitalize("Hello. "))
        assertTrue(engine.shouldAutoCapitalize("Great job! "))
        assertTrue(engine.shouldAutoCapitalize("What? "))
        assertFalse(engine.shouldAutoCapitalize("Hello "))
        assertFalse(engine.shouldAutoCapitalize("Crystal"))
    }

    @Test
    fun testKeyboardThemes() {
        val allThemes = KeyboardTheme.AllThemes
        assertEquals(10, allThemes.size)

        val crystalClear = KeyboardTheme.getById("crystal_clear")
        assertEquals("Crystal Clear", crystalClear.name)
        assertTrue(crystalClear.keyboardOpacity in 0.1f..1.0f)
        assertTrue(crystalClear.keycapOpacity in 0.1f..1.0f)

        // Test fallback
        val unknown = KeyboardTheme.getById("non_existent_theme")
        assertEquals(KeyboardTheme.CrystalClear.id, unknown.id)
    }

    @Test
    fun testEmojiSearch() {
        val loveEmojis = EmojiData.searchEmojis("love")
        assertTrue(loveEmojis.isNotEmpty())
        assertTrue(loveEmojis.contains("❤️") || loveEmojis.contains("😍"))

        val coolEmojis = EmojiData.searchEmojis("cool")
        assertTrue(coolEmojis.isNotEmpty())
    }

    @Test
    fun testTemplateEngineVariableResolution() {
        val template = "Good morning, {name}! Today is {day}. Enjoy {custom_text}!"
        val resolved = TemplateEngine.resolveTemplateVariables(
            templateText = template,
            recipientName = "Alex",
            customText = "your coffee"
        )
        assertTrue("Should contain recipient name", resolved.contains("Alex"))
        assertTrue("Should contain custom text", resolved.contains("your coffee"))
        assertFalse("Should not contain unresolved {name}", resolved.contains("{name}"))
        assertFalse("Should not contain unresolved {custom_text}", resolved.contains("{custom_text}"))
    }

    @Test
    fun testTemplateMissingVariables() {
        val template = "Hello {name}, your code is {code} for {service}"
        val missing = TemplateEngine.findMissingVariables(template)
        assertEquals(3, missing.size)
        assertTrue(missing.contains("name"))
        assertTrue(missing.contains("code"))
        assertTrue(missing.contains("service"))
    }

    @Test
    fun testTimeBasedSuggestions() {
        val templates = TemplateEngine.StarterTemplates
        assertTrue("Starter library should contain templates", templates.isNotEmpty())

        val morningSuggestions = TemplateEngine.getTimeBasedSuggestions(templates, currentHour = 8)
        assertEquals("Daily Greetings", morningSuggestions.first().category)

        val lunchSuggestions = TemplateEngine.getTimeBasedSuggestions(templates, currentHour = 12)
        assertEquals("Lunch Reminders", lunchSuggestions.first().category)

        val eveningSuggestions = TemplateEngine.getTimeBasedSuggestions(templates, currentHour = 22)
        assertEquals("Evening Greetings", eveningSuggestions.first().category)
    }

    @Test
    fun testScheduleNextOccurrence() {
        val fromCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 28, 10, 0, 0)
        }
        val fromTimestamp = fromCal.timeInMillis

        // Daily rule at 14:30 (today)
        val dailyRule = ScheduleRule(
            id = "rule_daily_1",
            templateId = "tmpl_1",
            repeatType = RepeatType.DAILY,
            hour = 14,
            minute = 30
        )
        val nextDaily = TemplateEngine.calculateNextOccurrence(dailyRule, fromTimestamp)
        val nextCal = Calendar.getInstance().apply { timeInMillis = nextDaily }
        assertEquals(28, nextCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(14, nextCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(30, nextCal.get(Calendar.MINUTE))

        // Daily rule at 09:00 (already passed today -> next day)
        val pastRule = ScheduleRule(
            id = "rule_daily_past",
            templateId = "tmpl_1",
            repeatType = RepeatType.DAILY,
            hour = 9,
            minute = 0
        )
        val nextPast = TemplateEngine.calculateNextOccurrence(pastRule, fromTimestamp)
        val pastCal = Calendar.getInstance().apply { timeInMillis = nextPast }
        assertEquals(29, pastCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(9, pastCal.get(Calendar.HOUR_OF_DAY))
    }
}
