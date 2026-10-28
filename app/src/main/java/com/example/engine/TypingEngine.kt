package com.example.engine

import com.example.data.model.TextShortcut

class TypingEngine {

    private val commonWords = listOf(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "i",
        "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
        "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
        "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
        "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
        "when", "make", "can", "like", "time", "no", "just", "him", "know", "take",
        "people", "into", "year", "your", "good", "some", "could", "them", "see", "other",
        "than", "then", "now", "look", "only", "come", "its", "over", "think", "also",
        "back", "after", "use", "two", "how", "our", "work", "first", "well", "way",
        "even", "new", "want", "because", "any", "these", "give", "day", "most", "us",
        "great", "amazing", "crystal", "keyboard", "transparent", "glass", "beautiful", "smooth",
        "swift", "typing", "smart", "love", "awesome", "perfect", "clean", "design", "message",
        "hello", "morning", "afternoon", "evening", "tonight", "tomorrow", "yesterday", "weekend",
        "meeting", "coffee", "lunch", "dinner", "call", "project", "presentation", "report", "update",
        "ready", "done", "finished", "started", "working", "happy", "excited", "grateful", "thanks",
        "welcome", "please", "sorry", "excuse", "pardon", "welcome", "sure", "definitely", "absolutely",
        "where", "why", "here", "today", "later", "soon", "already", "almost", "always", "never",
        "sometimes", "often", "usually", "probably", "perhaps", "maybe", "really", "very", "much",
        "many", "more", "less", "little", "big", "small", "quick", "fast", "slow", "easy", "hard",
        "best", "better", "cool", "fine", "nice", "kind", "super", "wonderful", "fantastic", "excellent"
    )

    private val nextWordPredictions = mapOf(
        "how" to listOf("are", "is", "about"),
        "thank" to listOf("you", "so", "very"),
        "thanks" to listOf("a lot", "so much", "for"),
        "good" to listOf("morning", "afternoon", "job"),
        "see" to listOf("you", "it", "that"),
        "i" to listOf("am", "will", "would"),
        "i'm" to listOf("on", "going", "ready"),
        "what" to listOf("time", "do", "is"),
        "where" to listOf("are", "is", "do"),
        "when" to listOf("can", "will", "are"),
        "let" to listOf("me", "us", "know"),
        "on" to listOf("my", "the", "time"),
        "at" to listOf("the", "home", "work"),
        "have" to listOf("a", "to", "been"),
        "do" to listOf("you", "not", "it"),
        "can" to listOf("you", "we", "i"),
        "are" to listOf("you", "we", "they"),
        "talk" to listOf("to", "soon", "later"),
        "sounds" to listOf("great", "good", "fun"),
        "looking" to listOf("forward", "for", "good"),
        "please" to listOf("let", "find", "call"),
        "happy" to listOf("birthday", "to", "for"),
        "hope" to listOf("you", "this", "all"),
        "love" to listOf("you", "it", "the"),
        "crystal" to listOf("keyboard", "clear", "glass"),
        "glass" to listOf("keyboard", "effect", "design")
    )

    fun getSuggestions(
        textBeforeCursor: String,
        shortcuts: List<TextShortcut> = emptyList(),
        customWords: Set<String> = emptySet(),
        suggestionsEnabled: Boolean = true
    ): List<String> {
        if (!suggestionsEnabled) return emptyList()

        val trimmed = textBeforeCursor
        if (trimmed.isEmpty()) {
            return listOf("I", "The", "Hey")
        }

        // Determine current word being typed
        val lastChar = trimmed.last()
        val isAtWordEnd = lastChar.isWhitespace() || lastChar in listOf('.', ',', '!', '?', ';', ':')

        if (isAtWordEnd) {
            // Predict next word based on previous token
            val tokens = trimmed.trim().split(Regex("\\s+"))
            val prevWord = tokens.lastOrNull()?.lowercase()?.filter { it.isLetter() } ?: ""
            val nextList = nextWordPredictions[prevWord]
            if (nextList != null) {
                return nextList.take(3)
            }
            return listOf("and", "the", "you")
        }

        // Currently typing a word prefix
        val wordMatch = Regex("([a-zA-Z0-9'-]+)$").find(trimmed)
        val currentPrefix = wordMatch?.value ?: ""
        if (currentPrefix.isEmpty()) {
            return emptyList()
        }

        val prefixLower = currentPrefix.lowercase()
        val results = mutableListOf<String>()

        // 1. Check for text shortcuts (e.g. omw -> On my way!)
        val shortcut = shortcuts.find { it.trigger.equals(prefixLower, ignoreCase = true) }
        if (shortcut != null) {
            results.add(shortcut.replacement)
        }

        // 2. Custom words match
        customWords.filter { it.startsWith(prefixLower) && it != prefixLower }
            .forEach { if (!results.contains(it)) results.add(it) }

        // 3. Common words prefix match
        commonWords.filter { it.startsWith(prefixLower) }
            .sortedBy { it.length }
            .forEach { word ->
                val formatted = formatCasing(currentPrefix, word)
                if (!results.contains(formatted)) {
                    results.add(formatted)
                }
            }

        // 4. If no shortcut matched and currentPrefix not already in results, prepend it
        if (shortcut == null && !results.contains(currentPrefix)) {
            results.add(0, currentPrefix)
        } else if (results.isEmpty()) {
            results.add(currentPrefix)
        }

        return results.take(3)
    }

    private fun formatCasing(source: String, candidate: String): String {
        return when {
            source.all { it.isUpperCase() } && source.length > 1 -> candidate.uppercase()
            source.firstOrNull()?.isUpperCase() == true -> candidate.replaceFirstChar { it.uppercase() }
            else -> candidate
        }
    }

    fun shouldAutoCapitalize(textBeforeCursor: String): Boolean {
        if (textBeforeCursor.isEmpty()) return true
        val trimmed = textBeforeCursor.trimEnd(' ', '\t')
        if (trimmed.isEmpty()) return true
        val last = trimmed.last()
        return last == '.' || last == '!' || last == '?' || last == '\n'
    }
}
