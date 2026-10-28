package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.model.MessagingPlatform
import java.net.URLEncoder

object PlatformSender {

    fun isAppInstalled(context: Context, packageName: String?): Boolean {
        if (packageName == null) return true
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun openPlatformComposer(
        context: Context,
        platform: MessagingPlatform,
        messageText: String,
        recipientAddress: String = ""
    ): Boolean {
        return try {
            when (platform) {
                MessagingPlatform.SMS -> {
                    val uri = if (recipientAddress.isNotBlank()) {
                        Uri.parse("smsto:${Uri.encode(recipientAddress)}")
                    } else {
                        Uri.parse("smsto:")
                    }
                    val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                        putExtra("sms_body", messageText)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    true
                }

                MessagingPlatform.WHATSAPP -> {
                    val encoded = URLEncoder.encode(messageText, "UTF-8")
                    val uriString = if (recipientAddress.isNotBlank()) {
                        val cleanPhone = recipientAddress.replace(Regex("[^0-9]"), "")
                        "https://api.whatsapp.com/send?phone=$cleanPhone&text=$encoded"
                    } else {
                        "https://api.whatsapp.com/send?text=$encoded"
                    }
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString)).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    true
                }

                MessagingPlatform.TELEGRAM -> {
                    val encoded = URLEncoder.encode(messageText, "UTF-8")
                    val uriString = "tg://msg?text=$encoded"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString)).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                        true
                    } else {
                        // Fallback web share
                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/share/url?text=$encoded")).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(webIntent)
                        true
                    }
                }

                MessagingPlatform.MESSENGER -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, messageText)
                        `package` = "com.facebook.orca"
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                        true
                    } else {
                        fallbackShareSheet(context, messageText)
                        false
                    }
                }

                MessagingPlatform.VIBER -> {
                    val encoded = URLEncoder.encode(messageText, "UTF-8")
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("viber://forward?text=$encoded")).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                        true
                    } else {
                        fallbackShareSheet(context, messageText)
                        false
                    }
                }

                MessagingPlatform.INSTAGRAM -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, messageText)
                        `package` = "com.instagram.android"
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                        true
                    } else {
                        fallbackShareSheet(context, messageText)
                        false
                    }
                }

                MessagingPlatform.EMAIL -> {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:${if (recipientAddress.isNotBlank()) recipientAddress else ""}")
                        putExtra(Intent.EXTRA_SUBJECT, "Scheduled Message from Crystal")
                        putExtra(Intent.EXTRA_TEXT, messageText)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    true
                }

                MessagingPlatform.OTHER -> {
                    fallbackShareSheet(context, messageText)
                    true
                }
            }
        } catch (e: Exception) {
            fallbackShareSheet(context, messageText)
            false
        }
    }

    fun copyToClipboard(context: Context, text: String, label: String = "Message") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard?.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    fun fallbackShareSheet(context: Context, messageText: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, messageText)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val chooser = Intent.createChooser(intent, "Share prepared message via...").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(chooser)
        } catch (e: Exception) {
            copyToClipboard(context, messageText)
        }
    }
}
