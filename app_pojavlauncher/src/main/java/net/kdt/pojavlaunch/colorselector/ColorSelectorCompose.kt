package net.kdt.pojavlaunch.colorselector

import android.graphics.Color
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ashmeet.hyperlauncher.theme.PojavTheme

@Composable
fun ColorSelectorContent(
    initialColor: Int,
    alphaEnabled: Boolean,
    onColorChanged: (Int) -> Unit,
    onClose: () -> Unit
) {
    var currentColor by remember { mutableIntStateOf(initialColor) }
    val hsv = remember { floatArrayOf(0f, 0f, 0f) }
    
    // Initialize HSV once
    remember(currentColor) {
        Color.colorToHSV(currentColor, hsv)
        true
    }

    BackHandler {
        onClose()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp), // Reduced height to fit better
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // SV Rectangle
            AndroidView(
                factory = { context ->
                    SVRectangleView(context, null).apply {
                        setRectSelectionListener { luminosity, intensity ->
                            hsv[1] = intensity
                            hsv[2] = luminosity
                            currentColor = Color.HSVToColor(Color.alpha(currentColor), hsv)
                            onColorChanged(currentColor)
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                update = { view ->
                    view.setLuminosityIntensity(hsv[2], hsv[1])
                    val hueHsv = floatArrayOf(hsv[0], 1f, 1f)
                    view.setColor(Color.HSVToColor(hueHsv), true)
                }
            )

            if (alphaEnabled) {
                // Alpha Slider
                AndroidView(
                    factory = { context ->
                        AlphaView(context, null).apply {
                            setAlphaSelectionListener { alpha ->
                                currentColor = Color.HSVToColor(alpha, hsv)
                                onColorChanged(currentColor)
                            }
                        }
                    },
                    modifier = Modifier
                        .width(24.dp)
                        .fillMaxHeight(),
                    update = { view ->
                        view.setAlpha(Color.alpha(currentColor))
                    }
                )
            }

            // Hue Slider
            AndroidView(
                factory = { context ->
                    HueView(context, null).apply {
                        setHueSelectionListener { hue ->
                            hsv[0] = hue
                            currentColor = Color.HSVToColor(Color.alpha(currentColor), hsv)
                            onColorChanged(currentColor)
                        }
                    }
                },
                modifier = Modifier
                    .width(24.dp)
                    .fillMaxHeight(),
                update = { view ->
                    view.setHue(hsv[0])
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Preview
            AndroidView(
                factory = { context ->
                    ColorSideBySideView(context, null)
                },
                modifier = Modifier
                    .size(48.dp),
                update = { view ->
                    view.setColor(currentColor)
                }
            )

            // Hex Edit
            var hexText by remember(currentColor) { mutableStateOf(String.format("%08X", currentColor)) }
            OutlinedTextField(
                value = hexText,
                onValueChange = { hex ->
                    hexText = hex
                    try {
                        val color = Color.parseColor("#$hex")
                        currentColor = color
                        onColorChanged(currentColor)
                    } catch (e: Exception) {
                        // Ignore invalid hex
                    }
                },
                modifier = Modifier.weight(1f),
                label = { Text("HEX") },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall
            )
        }
    }
}
