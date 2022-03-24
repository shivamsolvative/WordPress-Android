package org.wordpress.android.util.extensions

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.view.View
import org.wordpress.android.R
import org.wordpress.android.R.attr

@SuppressLint("InlinedApi")
fun Dialog.getPreferenceDialogContainerView(): View? {
    val containerViewId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        android.R.id.list_container
    } else {
        android.R.id.list
    }

    var view: View? = findViewById(containerViewId)

    // just in case, try to find a container of our own custom dialog
    if (view == null) {
        view = findViewById(R.id.list_editor_parent)
    }

    return view
}

fun Dialog.setStatusBarAsSurfaceColor() {
    window?.apply {
        statusBarColor = context.getColorFromAttribute(attr.colorSurface)
        if (!context.resources.configuration.isDarkTheme()) {
            decorView.systemUiVisibility = decorView
                    .systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}