package com.ashmeet.hyperlauncher.skin

import android.content.Context
import android.graphics.*
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kdt.pojavlaunch.authenticator.AuthType
import net.kdt.pojavlaunch.authenticator.accounts.Account
import net.kdt.pojavlaunch.authenticator.accounts.SkinHeadRenderer
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object SkinUtils {

    private const val TAG = "SkinUtils"

    fun getSkinUrl(account: Account?): String? {
        if (account == null) return null

        if (account.authType == AuthType.LOCAL) {
            return account.skinPath?.let { "file://$it" }
        }

        return when (account.authType) {
            AuthType.ELY_BY -> {
                val idToUse = if (account.profileId != null && !account.profileId.contains("00000000")) {
                    account.profileId
                } else {
                    account.username
                }
                "https://skinsystem.ely.by/skins/$idToUse.png"
            }
            AuthType.MICROSOFT -> {
                val idToUse = if (account.profileId != null && !account.profileId.contains("00000000")) {
                    account.profileId
                } else {
                    account.username
                }
                "https://minotar.net/skin/$idToUse"
            }
            else -> null
        }
    }

    /**
     * Determines the model type for the skin viewer.
     */
    fun getModelType(account: Account?): String {
        return when (account?.skinModel) {
            SkinModelType.ALEX -> "slim"
            else -> "default"
        }
    }

    /**
     * Renders a 3D isometric head from a skin bitmap or file.
     */
    suspend fun renderHead(context: Context, account: Account?): Bitmap? = withContext(Dispatchers.IO) {
        val skinUrl = getSkinUrl(account)
        val skinBitmap = getSkinBitmap(skinUrl) ?: return@withContext loadSteveHead3D(context)

        val head = try {
            SkinHeadRenderer().render(120, skinBitmap)
        } catch (e: Exception) {
            Log.e(TAG, "Renderer error", e)
            null
        }

        skinBitmap.recycle()
        return@withContext head ?: loadSteveHead3D(context)
    }

    /**
     * Renders a 2D front face head from a skin bitmap or file.
     */
    suspend fun renderHead2D(context: Context, account: Account?): Bitmap? = withContext(Dispatchers.IO) {
        val skinUrl = getSkinUrl(account)
        val skinBitmap = getSkinBitmap(skinUrl) ?: return@withContext loadSteveHead2D(context)

        val ratio = skinBitmap.width / 64
        val size = 128
        val result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint().apply { isFilterBitmap = false }

        val srcBase = Rect(8 * ratio, 8 * ratio, 16 * ratio, 16 * ratio)
        val srcOverlay = Rect(40 * ratio, 8 * ratio, 48 * ratio, 16 * ratio)
        val dst = Rect(0, 0, size, size)

        canvas.drawBitmap(skinBitmap, srcBase, dst, paint)

        canvas.drawBitmap(skinBitmap, srcOverlay, dst, paint)

        skinBitmap.recycle()
        return@withContext result
    }

    private suspend fun getSkinBitmap(skinUrl: String?): Bitmap? = withContext(Dispatchers.IO) {
        if (skinUrl == null) return@withContext null

        if (skinUrl.startsWith("file://")) {
            val path = skinUrl.substring(7)
            val file = File(path)
            if (file.exists()) {
                return@withContext try {
                    BitmapFactory.decodeFile(file.absolutePath)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to decode skin file $path", e)
                    null
                }
            }
        } else {
            return@withContext downloadBitmap(skinUrl)
        }
        return@withContext null
    }

    private fun downloadBitmap(urlString: String): Bitmap? {
        return try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            connection.inputStream.use { BitmapFactory.decodeStream(it) }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to download skin from $urlString", e)
            null
        }
    }

    private fun loadSteveHead3D(context: Context): Bitmap? {
        val steveBitmap = try {
            context.assets.open("steve.png").use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            Log.w(TAG, "steve.png not found in assets")
            null
        } ?: return null

        val head = try {
            SkinHeadRenderer().render(120, steveBitmap)
        } catch (e: Exception) {
            Log.e(TAG, "Renderer failed for steve.png", e)
            null
        }

        steveBitmap.recycle()
        return head
    }

    private fun loadSteveHead2D(context: Context): Bitmap? {
        val steveBitmap = try {
            context.assets.open("steve.png").use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            Log.w(TAG, "steve.png not found in assets")
            null
        } ?: return null

        val ratio = steveBitmap.width / 64
        val size = 128
        val result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint().apply { isFilterBitmap = false }

        val srcBase = Rect(8 * ratio, 8 * ratio, 16 * ratio, 16 * ratio)
        val dst = Rect(0, 0, size, size)
        canvas.drawBitmap(steveBitmap, srcBase, dst, paint)

        steveBitmap.recycle()
        return result
    }

    /**
     * Composable helper to get a 3D skinhead state.
     */
    @Composable
    fun rememberSkinHead(account: Account?): State<Bitmap?> {
        val context = LocalContext.current
        val stableKey = "${account?.profileId}_${account?.skinPath}_${account?.username}_3D"
        return produceState<Bitmap?>(initialValue = null, stableKey) {
            value = renderHead(context, account)
        }
    }

    /**
     * Composable helper to get a 2D skinhead state.
     */
    @Composable
    fun rememberSkinHead2D(account: Account?): State<Bitmap?> {
        val context = LocalContext.current
        val stableKey = "${account?.profileId}_${account?.skinPath}_${account?.username}_2D"
        return produceState<Bitmap?>(initialValue = null, stableKey) {
            value = renderHead2D(context, account)
        }
    }
}
