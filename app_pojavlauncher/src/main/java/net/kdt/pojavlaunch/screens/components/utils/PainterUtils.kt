package net.kdt.pojavlaunch.screens.components.utils

import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap

@Composable
fun rememberCustomPainter(@DrawableRes id: Int): Painter {
    val context = LocalContext.current
    return remember(id) {
        val drawable = AppCompatResources.getDrawable(context, id)
            ?: throw IllegalArgumentException("Drawable resource $id not found")
        val bitmap = drawable.toBitmap()
        BitmapPainter(bitmap.asImageBitmap())
    }
}
