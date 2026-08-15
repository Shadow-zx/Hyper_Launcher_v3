package com.ashmeet.hyperlauncher.dialog

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ashmeet.hyperlauncher.prefs.LauncherPreferences
import com.ashmeet.hyperlauncher.prefs.LauncherPreferences.*
import com.google.android.material.materialswitch.MaterialSwitch
import com.kdt.CustomSeekbar
import com.kdt.SideDialogView
import net.ashmeet.hyperlauncher.R
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.utils.interfaces.SimpleSeekBarListener

/**
 * Side dialog for quick settings that you can change in game
 * The implementation has to take action on some preference changes
 */
abstract class QuickSettingSideDialog(context: Context, parent: ViewGroup) :
    SideDialogView(context, parent, R.layout.dialog_quick_setting) {

    private var mEditor: SharedPreferences.Editor? = null

    private lateinit var mGyroSwitch: MaterialSwitch
    private lateinit var mGyroXSwitch: MaterialSwitch
    private lateinit var mGyroYSwitch: MaterialSwitch
    private lateinit var mGestureSwitch: MaterialSwitch

    private lateinit var mGyroSensitivityBar: CustomSeekbar
    private lateinit var mMouseSpeedBar: CustomSeekbar
    private lateinit var mGestureDelayBar: CustomSeekbar
    private lateinit var mResolutionBar: CustomSeekbar

    private lateinit var mGyroSensitivityText: TextView
    private lateinit var mGyroSensitivityDisplayText: TextView
    private lateinit var mMouseSpeedText: TextView
    private lateinit var mGestureDelayText: TextView
    private lateinit var mGestureDelayDisplayText: TextView
    private lateinit var mResolutionText: TextView

    private var mOriginalGyroEnabled = false
    private var mOriginalGyroXEnabled = false
    private var mOriginalGyroYEnabled = false
    private var mOriginalGestureDisabled = false

    private var mOriginalGyroSensitivity = 0f
    private var mOriginalMouseSpeed = 0f
    private var mOriginalResolution = 0f
    private var mOriginalGestureDelay = 0

    init {
        setTitle(R.string.quick_setting_title)
        setupCancelButton()
    }

    override fun onInflate() {
        bindLayout()
        Tools.runOnUiThread {
            setupListeners()
            updateGyroCompatibility()
        }
    }

    override fun onDestroy() {
        removeListeners()
    }

    private fun bindLayout() {
        mDialogContent.apply {
            mGyroSwitch = findViewById(R.id.checkboxGyro)
            mGyroXSwitch = findViewById(R.id.checkboxGyroX)
            mGyroYSwitch = findViewById(R.id.checkboxGyroY)
            mGestureSwitch = findViewById(R.id.checkboxGesture)

            mGyroSensitivityBar = findViewById(R.id.editGyro_seekbar)
            mMouseSpeedBar = findViewById(R.id.editMouseSpeed_seekbar)
            mGestureDelayBar = findViewById(R.id.editGestureDelay_seekbar)
            mResolutionBar = findViewById(R.id.editResolution_seekbar)

            mGyroSensitivityText = findViewById(R.id.editGyro_textView_percent)
            mGyroSensitivityDisplayText = findViewById(R.id.editGyro_textView)
            mMouseSpeedText = findViewById(R.id.editMouseSpeed_textView_percent)
            mGestureDelayText = findViewById(R.id.editGestureDelay_textView_percent)
            mGestureDelayDisplayText = findViewById(R.id.editGestureDelay_textView)
            mResolutionText = findViewById(R.id.editResolution_textView_percent)
        }
    }

    private fun setupListeners() {
        mEditor = DEFAULT_PREF.edit()

        mOriginalGyroEnabled = PREF_ENABLE_GYRO
        mOriginalGyroXEnabled = PREF_GYRO_INVERT_X
        mOriginalGyroYEnabled = PREF_GYRO_INVERT_Y
        mOriginalGestureDisabled = PREF_DISABLE_GESTURES

        mOriginalGyroSensitivity = PREF_GYRO_SENSITIVITY
        mOriginalMouseSpeed = PREF_MOUSESPEED
        mOriginalGestureDelay = PREF_LONGPRESS_TRIGGER
        mOriginalResolution = PREF_SCALE_FACTOR

        mGyroSwitch.isChecked = mOriginalGyroEnabled
        mGyroXSwitch.isChecked = mOriginalGyroXEnabled
        mGyroYSwitch.isChecked = mOriginalGyroYEnabled
        mGestureSwitch.isChecked = mOriginalGestureDisabled

        mGyroSwitch.setOnCheckedChangeListener { _, isChecked ->
            PREF_ENABLE_GYRO = isChecked
            onGyroStateChanged()
            updateGyroVisibility(isChecked)
            mEditor?.putBoolean("enableGyro", isChecked)
        }

        mGyroXSwitch.setOnCheckedChangeListener { _, isChecked ->
            PREF_GYRO_INVERT_X = isChecked
            onGyroStateChanged()
            mEditor?.putBoolean("gyroInvertX", isChecked)
        }

        mGyroYSwitch.setOnCheckedChangeListener { _, isChecked ->
            PREF_GYRO_INVERT_Y = isChecked
            onGyroStateChanged()
            mEditor?.putBoolean("gyroInvertY", isChecked)
        }

        mGestureSwitch.setOnCheckedChangeListener { _, isChecked ->
            PREF_DISABLE_GESTURES = isChecked
            updateGestureVisibility(isChecked)
            mEditor?.putBoolean("disableGestures", isChecked)
        }

        mGyroSensitivityBar.setOnSeekBarChangeListener(SimpleSeekBarListener { _, progress, _ ->
            PREF_GYRO_SENSITIVITY = progress / 100f
            mEditor?.putInt("gyroSensitivity", progress)
            setSeekTextPercent(mGyroSensitivityText, progress)
        })
        mGyroSensitivityBar.progress = (mOriginalGyroSensitivity * 100f).toInt()
        setSeekTextPercent(mGyroSensitivityText, mGyroSensitivityBar.progress)

        mMouseSpeedBar.setOnSeekBarChangeListener(SimpleSeekBarListener { _, progress, _ ->
            PREF_MOUSESPEED = progress / 100f
            mEditor?.putInt("mousespeed", progress)
            setSeekTextPercent(mMouseSpeedText, progress)
        })
        mMouseSpeedBar.progress = (mOriginalMouseSpeed * 100f).toInt()
        setSeekTextPercent(mMouseSpeedText, mMouseSpeedBar.progress)

        mGestureDelayBar.setOnSeekBarChangeListener(SimpleSeekBarListener { _, progress, _ ->
            PREF_LONGPRESS_TRIGGER = progress
            mEditor?.putInt("timeLongPressTrigger", progress)
            setSeekTextMillisecond(mGestureDelayText, progress)
        })
        mGestureDelayBar.progress = mOriginalGestureDelay
        setSeekTextMillisecond(mGestureDelayText, mGestureDelayBar.progress)

        mResolutionBar.setOnSeekBarChangeListener(SimpleSeekBarListener { _, progress, _ ->
            PREF_SCALE_FACTOR = progress / 100f
            mEditor?.putInt("resolutionRatio", progress)
            setSeekTextPercent(mResolutionText, progress)
            onResolutionChanged()
        })
        mResolutionBar.progress = (mOriginalResolution * 100).toInt()
        setSeekTextPercent(mResolutionText, mResolutionBar.progress)

        updateGyroVisibility(mOriginalGyroEnabled)
        updateGestureVisibility(mOriginalGestureDisabled)
    }

    private fun setSeekTextMillisecond(target: TextView, value: Int) {
        setSeekText(target, R.string.millisecond_format, value)
    }

    private fun setSeekTextPercent(target: TextView, value: Int) {
        setSeekText(target, R.string.percent_format, value)
    }

    private fun setSeekText(target: TextView, format: Int, value: Int) {
        target.text = target.context.getString(format, value)
    }

    private fun updateGyroVisibility(isEnabled: Boolean) {
        val visibility = if (isEnabled) View.VISIBLE else View.GONE
        mGyroXSwitch.visibility = visibility
        mGyroYSwitch.visibility = visibility
        mGyroSensitivityBar.visibility = visibility
        mGyroSensitivityText.visibility = visibility
        mGyroSensitivityDisplayText.visibility = visibility
    }

    private fun updateGyroCompatibility() {
        val isGyroAvailable = Tools.deviceSupportsGyro(mDialogContent.context)
        if (!isGyroAvailable) {
            mGyroSwitch.visibility = View.GONE
            updateGestureVisibility(false)
        }
    }

    private fun updateGestureVisibility(isDisabled: Boolean) {
        val visibility = if (isDisabled) View.GONE else View.VISIBLE
        mGestureDelayBar.visibility = visibility
        mGestureDelayText.visibility = visibility
        mGestureDelayDisplayText.visibility = visibility
    }

    private fun removeListeners() {
        mGyroSwitch.setOnCheckedChangeListener(null)
        mGyroXSwitch.setOnCheckedChangeListener(null)
        mGyroYSwitch.setOnCheckedChangeListener(null)
        mGestureSwitch.setOnCheckedChangeListener(null)
        mGyroSensitivityBar.setOnSeekBarChangeListener(null)
        mMouseSpeedBar.setOnSeekBarChangeListener(null)
        mGestureDelayBar.setOnSeekBarChangeListener(null)
        mResolutionBar.setOnSeekBarChangeListener(null)
    }

    private fun setupCancelButton() {
        setStartButtonListener(android.R.string.cancel) { cancel() }
        setEndButtonListener(android.R.string.ok) {
            mEditor?.apply()
            disappear(true)
        }
    }

    /** Resets all settings to their original values */
    fun cancel() {
        if (isDisplaying) {
            PREF_ENABLE_GYRO = mOriginalGyroEnabled
            PREF_GYRO_INVERT_X = mOriginalGyroXEnabled
            PREF_GYRO_INVERT_Y = mOriginalGyroYEnabled
            PREF_DISABLE_GESTURES = mOriginalGestureDisabled
            PREF_GYRO_SENSITIVITY = mOriginalGyroSensitivity
            PREF_MOUSESPEED = mOriginalMouseSpeed
            PREF_LONGPRESS_TRIGGER = mOriginalGestureDelay
            PREF_SCALE_FACTOR = mOriginalResolution
            onGyroStateChanged()
            onResolutionChanged()
        }
        disappear(true)
    }

    /** Called when the resolution is changed. Use [LauncherPreferences.PREF_SCALE_FACTOR] */
    abstract fun onResolutionChanged()

    /** Called when the gyro state is changed.
     * Use [LauncherPreferences.PREF_ENABLE_GYRO]
     * Use [LauncherPreferences.PREF_GYRO_INVERT_X]
     * Use [LauncherPreferences.PREF_GYRO_INVERT_Y]
     */
    abstract fun onGyroStateChanged()
}
