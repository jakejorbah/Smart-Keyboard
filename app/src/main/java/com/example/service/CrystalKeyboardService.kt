package com.example.service

import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.MainActivity
import com.example.data.KeyboardPreferences
import com.example.data.model.KeyboardTheme
import com.example.ui.components.KeyboardLayout
import com.example.ui.theme.CrystalKeyboardTheme

class CrystalKeyboardService : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private lateinit var preferences: KeyboardPreferences
    private var textBeforeCursorState by mutableStateOf("")
    private var actionLabelState by mutableStateOf("Done")

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        preferences = KeyboardPreferences.getInstance(this)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        updateEditorContext(info)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
    }

    private fun updateEditorContext(info: EditorInfo?) {
        actionLabelState = when (info?.imeOptions?.and(EditorInfo.IME_MASK_ACTION)) {
            EditorInfo.IME_ACTION_GO -> "Go"
            EditorInfo.IME_ACTION_SEARCH -> "Search"
            EditorInfo.IME_ACTION_SEND -> "Send"
            EditorInfo.IME_ACTION_NEXT -> "Next"
            EditorInfo.IME_ACTION_DONE -> "Done"
            else -> "Enter"
        }
        updateTextBeforeCursor()
    }

    private fun updateTextBeforeCursor() {
        val ic = currentInputConnection
        val text = ic?.getTextBeforeCursor(80, 0)?.toString() ?: ""
        textBeforeCursorState = text
    }

    override fun onUpdateSelection(
        oldSelStart: Int,
        oldSelEnd: Int,
        newSelStart: Int,
        newSelEnd: Int,
        candidatesStart: Int,
        candidatesEnd: Int
    ) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd)
        updateTextBeforeCursor()
    }

    override fun onCreateInputView(): View {
        val composeView = ComposeView(this)
        composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)

        composeView.setViewTreeLifecycleOwner(this)
        composeView.setViewTreeViewModelStoreOwner(this)
        composeView.setViewTreeSavedStateRegistryOwner(this)

        composeView.setContent {
            val settings by preferences.settings.collectAsState()
            val shortcuts by preferences.shortcuts.collectAsState()
            val snippets by preferences.snippets.collectAsState()
            val templates by preferences.templates.collectAsState()
            val customWords by preferences.customWords.collectAsState()

            CrystalKeyboardTheme(darkTheme = KeyboardTheme.getById(settings.themeId).isDark) {
                KeyboardLayout(
                    settings = settings,
                    shortcuts = shortcuts,
                    snippets = snippets,
                    templates = templates,
                    customWords = customWords,
                    currentTextBeforeCursor = textBeforeCursorState,
                    actionLabel = actionLabelState,
                    onOpenMessagePlanner = {
                        val intent = Intent(this@CrystalKeyboardService, MainActivity::class.java).apply {
                            action = "OPEN_PLANNER"
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                        startActivity(intent)
                    },
                    onTextInput = { text ->
                        currentInputConnection?.commitText(text, 1)
                        updateTextBeforeCursor()
                    },
                    onDelete = {
                        val ic = currentInputConnection ?: return@KeyboardLayout
                        val selectedText = ic.getSelectedText(0)
                        if (selectedText.isNullOrEmpty()) {
                            ic.deleteSurroundingText(1, 0)
                        } else {
                            ic.commitText("", 1)
                        }
                        updateTextBeforeCursor()
                    },
                    onEnter = {
                        val ic = currentInputConnection ?: return@KeyboardLayout
                        val action = currentInputEditorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION)
                        if (action != null && action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
                            ic.performEditorAction(action)
                        } else {
                            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
                        }
                        updateTextBeforeCursor()
                    },
                    onOpenSettings = {
                        val intent = Intent(this@CrystalKeyboardService, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                        startActivity(intent)
                    },
                    onAddSnippet = { preferences.addSnippet(it) },
                    onDeleteSnippet = { preferences.deleteSnippet(it) },
                    onUpdateOneHandedMode = { preferences.updateSettings(settings.copy(oneHandedMode = it)) },
                    onCycleTheme = {
                        val all = KeyboardTheme.AllThemes
                        val idx = all.indexOfFirst { it.id == settings.themeId }
                        val next = all[(if (idx >= 0) idx + 1 else 0) % all.size]
                        preferences.selectTheme(next.id)
                    }
                )
            }
        }

        return composeView
    }
}
