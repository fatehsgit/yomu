package eu.kanade.tachiyomi.util.view

import android.view.Window
import android.view.WindowManager

fun Window.setSecureScreen(enabled: Boolean) {
    clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
}
