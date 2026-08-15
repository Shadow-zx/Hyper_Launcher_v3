package net.kdt.pojavlaunch.customcontrols.handleview

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.edit
import com.ashmeet.hyperlauncher.prefs.LauncherPreferences
import java.io.File

class DrawerPullButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val composeView = ComposeView(context)
    private var mInitialX = 0f
    private var mInitialY = 0f
    private var mInitialTouchX = 0f
    private var mInitialTouchY = 0f
    private var mHasMoved = false

    private var pullSizePerc by mutableFloatStateOf(LauncherPreferences.PREF_DRAWER_PULL_SIZE_PERC)
    private var bgOpacity by mutableIntStateOf(LauncherPreferences.PREF_DRAWER_PULL_BG_OPACITY)
    private var iconOpacity by mutableIntStateOf(LauncherPreferences.PREF_DRAWER_PULL_ICON_OPACITY)
    private var showBackground by mutableStateOf(LauncherPreferences.PREF_DRAWER_PULL_BACKGROUND)
    private var iconPath by mutableStateOf(LauncherPreferences.PREF_DRAWER_PULL_ICON_PATH)

    private val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            "drawer_pull_size_perc", "drawer_pull_opacity", "drawer_pull_icon_opacity",
            "drawer_pull_background", "drawer_pull_icon_path" -> {
                updateAppearance()
            }
        }
    }

    init {
        addView(composeView)
        composeView.setContent {
            DrawerPullButtonContent(
                showBackground = showBackground,
                bgOpacity = bgOpacity,
                iconOpacity = iconOpacity,
                iconPath = iconPath
            )
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        LauncherPreferences.DEFAULT_PREF.registerOnSharedPreferenceChangeListener(prefListener)
        updateAppearance()
    }

    override fun onDetachedFromWindow() {
        LauncherPreferences.DEFAULT_PREF.unregisterOnSharedPreferenceChangeListener(prefListener)
        super.onDetachedFromWindow()
    }

    fun updateAppearance() {
        pullSizePerc = LauncherPreferences.PREF_DRAWER_PULL_SIZE_PERC
        bgOpacity = LauncherPreferences.PREF_DRAWER_PULL_BG_OPACITY
        iconOpacity = LauncherPreferences.PREF_DRAWER_PULL_ICON_OPACITY
        showBackground = LauncherPreferences.PREF_DRAWER_PULL_BACKGROUND
        iconPath = LauncherPreferences.PREF_DRAWER_PULL_ICON_PATH

        val dm = resources.displayMetrics
        // 10% -> 25dp, 100% -> 60dp. Doubled for the user request.
        val dpSize = (25 + (pullSizePerc - 10) * (35f / 90f)) * 2
        val size = (dpSize * dm.density).toInt()
        
        layoutParams?.let {
            if (it.width != size || it.height != size) {
                it.width = size
                it.height = size
                layoutParams = it
            }
        }
    }

    @Composable
    private fun DrawerPullButtonContent(
        showBackground: Boolean,
        bgOpacity: Int,
        iconOpacity: Int,
        iconPath: String?
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (showBackground) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.75f) // Reduced background size
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = bgOpacity / 100f))
                )
            }

            val customBitmap = remember(iconPath) {
                iconPath?.let { path ->
                    if (File(path).exists()) {
                        BitmapFactory.decodeFile(path)
                    } else null
                }
            }

            if (customBitmap != null) {
                androidx.compose.foundation.Image(
                    bitmap = customBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(0.5f)
                        .alpha(iconOpacity / 100f)
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(0.5f)
                        .alpha(iconOpacity / 100f),
                    tint = Color.White
                )
            }
        }
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return LauncherPreferences.PREF_DRAWER_PULL_HOLD_TO_MOVE
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!LauncherPreferences.PREF_DRAWER_PULL_HOLD_TO_MOVE) {
            return super.onTouchEvent(event)
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mInitialX = x
                mInitialY = y
                mInitialTouchX = event.rawX
                mInitialTouchY = event.rawY
                mHasMoved = false
                performClick()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                x = mInitialX + (event.rawX - mInitialTouchX)
                y = mInitialY + (event.rawY - mInitialTouchY)
                mHasMoved = true
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (mHasMoved) {
                    savePosition()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun savePosition() {
        LauncherPreferences.PREF_DRAWER_PULL_POS_X = x
        LauncherPreferences.PREF_DRAWER_PULL_POS_Y = y
        
        LauncherPreferences.DEFAULT_PREF.edit {
            putFloat("drawer_pull_pos_x", x)
            putFloat("drawer_pull_pos_y", y)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (LauncherPreferences.PREF_DRAWER_PULL_POS_X != -1f && LauncherPreferences.PREF_DRAWER_PULL_POS_Y != -1f) {
            x = LauncherPreferences.PREF_DRAWER_PULL_POS_X
            y = LauncherPreferences.PREF_DRAWER_PULL_POS_Y
        } else if (!LauncherPreferences.PREF_DRAWER_PULL_HOLD_TO_MOVE) {
            val parentView = parent as? View
            parentView?.let {
                translationX = (it.width * 0.25f)
            }
        }
    }
}
