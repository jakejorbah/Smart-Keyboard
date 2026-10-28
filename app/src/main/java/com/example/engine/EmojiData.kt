package com.example.engine

data class EmojiCategory(
    val name: String,
    val icon: String,
    val emojis: List<String>
)

object EmojiData {
    val categories = listOf(
        EmojiCategory(
            name = "Smileys",
            icon = "😀",
            emojis = listOf(
                "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "🥲", "🥹",
                "😊", "😇", "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗",
                "😙", "😚", "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓",
                "😎", "🥸", "🤩", "🥳", "😏", "😒", "😞", "😔", "😟", "😕",
                "🙁", "☹️", "😣", "😖", "😫", "😩", "🥺", "😢", "😭", "😮‍💨",
                "😤", "😠", "😡", "🤬", "🤯", "😳", "🥵", "🥶", "😱", "😨"
            )
        ),
        EmojiCategory(
            name = "Gestures",
            icon = "👍",
            emojis = listOf(
                "👍", "👎", "👌", "🤌", "🤏", "✌️", "🤞", "🫰", "🤟", "🤘",
                "🤙", "👈", "👉", "👆", "🖕", "👇", "☝️", "🫵", "👋", "🤚",
                "🖐️", "✋", "🖖", "🫱", "🫲", "👏", "🙌", "🫶", "👐", "🤲",
                "🤝", "🙏", "✍️", "💅", "🤳", "💪", "🦾", "🦿", "🦵", "🦶"
            )
        ),
        EmojiCategory(
            name = "Nature",
            icon = "🐶",
            emojis = listOf(
                "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐻‍❄️", "🐨",
                "🐯", "🦁", "🐮", "🐷", "🐸", "🐵", "🙈", "🙉", "🙊", "🐒",
                "🐔", "🐧", "🐦", "🐤", "🐣", "🐥", "🦆", "🦅", "🦉", "🦇",
                "🐺", "🐗", "🐴", "🦄", "🐝", "🪱", "🐛", "🦋", "🐌", "🐞",
                "🌸", "💮", "🏵️", "🌹", "🥀", "🌺", "🌻", "🌼", "🌷", "🌱",
                "🪴", "🌲", "🌳", "🌴", "🌵", "🌾", "🌿", "☘️", "🍀", "🍁"
            )
        ),
        EmojiCategory(
            name = "Food",
            icon = "🍕",
            emojis = listOf(
                "🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐",
                "🍈", "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🥑", "🍆",
                "🌽", "🌶️", "🫑", "🥒", "🥬", "🥦", "🧄", "🧅", "🍄", "🥜",
                "🍞", "🥐", "🥖", "🫓", "🥨", "🥯", "🥞", "🧇", "🧀", "🍖",
                "🍗", "🥩", "🥓", "🍔", "🍟", "🍕", "🌭", "🥪", "🌮", "🌯",
                "🫔", "🥙", "🧆", "🥚", "🍳", "🥘", "🍲", "🫕", "🥣", "🥗"
            )
        ),
        EmojiCategory(
            name = "Travel",
            icon = "✈️",
            emojis = listOf(
                "🚗", "🚕", "🚙", "🚌", "🚎", "🏎️", "🚓", "🚑", "🚒", "🚐",
                "🛻", "🚚", "🚛", "🚜", "🛴", "🚲", "🛵", "🏍️", "🛺", "🚨",
                "🚔", "🚍", "🚘", "🚖", "🚡", "🚠", "🚟", "🚃", "🚋", "🚞",
                "🚝", "🚄", "🚅", "🚈", "🚂", "🚆", "🚇", "🚊", "🚉", "✈️",
                "🛫", "🛬", "🛩️", "💺", "🛰️", "🚀", "🛸", "🚁", "🛶", "⛵",
                "🚤", "🛥️", "🛳️", "⛴️", "🚢", "⚓", "🛟", "🪝", "⛽", "🚧"
            )
        ),
        EmojiCategory(
            name = "Objects",
            icon = "💡",
            emojis = listOf(
                "⌚", "📱", "📲", "💻", "⌨️", "🖥️", "🖨️", "🖱️", "🖲️", "🕹️",
                "🗜️", "💽", "💾", "💿", "📀", "📼", "📷", "📸", "📹", "🎥",
                "📽️", "🎞️", "📞", "☎️", "📟", "📠", "📺", "📻", "🎙️", "🎚️",
                "🎛️", "🧭", "⏱️", "⏲️", "⏰", "🕰️", "⌛", "⏳", "📡", "🔋",
                "🪫", "🔌", "💡", "🔦", "🕯️", "🪔", "🧯", "🛢️", "💸", "💵",
                "💎", "💍", "👑", "🔮", "🧿", "🪄", "🪬", "💈", "⚗️", "🔭"
            )
        ),
        EmojiCategory(
            name = "Symbols",
            icon = "❤️",
            emojis = listOf(
                "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
                "❤️‍🔥", "❤️‍🩹", "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝",
                "💟", "☮️", "✝️", "☪️", "🕉️", "☸️", "✡️", "🔯", "🕎", "☯️",
                "☦️", "🛐", "⛎", "♈", "♉", "♊", "♋", "♌", "♍", "♎",
                "♏", "♐", "♑", "♒", "♓", "🆔", "⚛️", "🉑", "☢️", "☣️",
                "📴", "📳", "🈶", "🈚", "🈸", "🈺", "🈷️", "✴️", "❇️", "✳️",
                "💯", "♨️", "💢", "💬", "👁️‍🗨️", "🗯️", "💭", "💤", "✨", "⭐"
            )
        )
    )

    private val emojiKeywords = mapOf(
        "love" to listOf("❤️", "😍", "🥰", "💕", "💖", "😘", "🫶"),
        "heart" to listOf("❤️", "💜", "💙", "💚", "💛", "🧡", "🖤", "🤍"),
        "happy" to listOf("😀", "😊", "😄", "😁", "🥰", "🥳", "✨"),
        "smile" to listOf("🙂", "😊", "😀", "😃", "😄", "😁", "😆"),
        "fire" to listOf("🔥", "❤️‍🔥", "💥", "⚡"),
        "cool" to listOf("😎", "🤙", "🥶", "🧊", "✨"),
        "cry" to listOf("😭", "😢", "🥺", "😿", "💧"),
        "laugh" to listOf("😂", "🤣", "😆", "😹"),
        "party" to listOf("🥳", "🎉", "🎊", "🍾", "🍻"),
        "ok" to listOf("👌", "👍", "✅", "🙆"),
        "star" to listOf("⭐", "🌟", "✨", "💫", "🤩"),
        "car" to listOf("🚗", "🏎️", "🚘", "🚙"),
        "plane" to listOf("✈️", "🛫", "🛬", "🛩️"),
        "cat" to listOf("🐱", "🐈", "😻", "😽"),
        "dog" to listOf("🐶", "🐕", "🦮", "🐩"),
        "coffee" to listOf("☕", "🧋", "🍵", "🍩"),
        "food" to listOf("🍕", "🍔", "🍟", "🌮", "🥗"),
        "money" to listOf("💵", "💰", "💸", "💎", "💳"),
        "crystal" to listOf("💎", "🔮", "✨", "💠", "🧊")
    )

    fun searchEmojis(query: String): List<String> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return categories.first().emojis

        val keywordMatches = emojiKeywords.filterKeys { it.contains(q) || q.contains(it) }.values.flatten()
        if (keywordMatches.isNotEmpty()) {
            return keywordMatches.distinct()
        }

        // Search through category names
        val categoryMatch = categories.find { it.name.lowercase().contains(q) }
        if (categoryMatch != null) {
            return categoryMatch.emojis
        }

        return categories.flatMap { it.emojis }.take(40)
    }
}
