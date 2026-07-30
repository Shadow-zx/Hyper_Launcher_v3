package net.kdt.pojavlaunch.screens.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.ashmeet.hyperlauncher.R
import net.kdt.pojavlaunch.PojavApplication
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.authenticator.AuthType
import net.kdt.pojavlaunch.authenticator.accounts.Account
import net.kdt.pojavlaunch.authenticator.accounts.Accounts
import net.kdt.pojavlaunch.authenticator.accounts.SkinHeadRenderer
import net.kdt.pojavlaunch.authenticator.listener.LoginListener
import net.kdt.pojavlaunch.extra.ExtraConstants
import net.kdt.pojavlaunch.extra.ExtraCore
import net.kdt.pojavlaunch.extra.ExtraListener
import java.io.IOException

@Composable
fun AccountSpinnerCompose(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var accounts by remember { mutableStateOf<List<Account>>(emptyList()) }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    var expanded by remember { mutableStateOf(false) }
    var loginProgress by remember { mutableFloatStateOf(0f) }
    var isAuthenticating by remember { mutableStateOf(false) }

    val loginListener = remember {
        object : LoginListener {
            private var maxSteps = 5
            override fun onLoginDone(account: Account?) {
                loginProgress = 0f
                isAuthenticating = false
            }

            override fun onLoginError(errorMessage: Throwable?) {
                loginProgress = 0f
                isAuthenticating = false
            }

            override fun onLoginProgress(step: Int) {
                loginProgress = step.toFloat() / maxSteps
                isAuthenticating = true
            }

            override fun setMaxLoginProgress(max: Int) {
                maxSteps = max
            }
        }
    }

    val refreshAccount: (Account) -> Unit = { account ->
        net.kdt.pojavlaunch.progresskeeper.ProgressKeeper.waitUntilDone {
            val refreshAccount = account.reload()
            if (refreshAccount == null) return@waitUntilDone
            val authType = refreshAccount.authType
            if (authType.requiresLogin() && System.currentTimeMillis() > refreshAccount.expiresAt) {
                isAuthenticating = true
                authType.createAuth().refreshAccount(loginListener, refreshAccount)
            }
        }
    }

    val reloadAccounts: () -> Unit = {
        PojavApplication.sExecutorService.execute {
            try {
                val loadedAccounts = Accounts.load()
                Tools.runOnUiThread {
                    accounts = loadedAccounts.accounts
                    selectedIndex = loadedAccounts.selectionIndex
                    
                    if (selectedIndex >= 0 && selectedIndex < accounts.size) {
                        refreshAccount(accounts[selectedIndex])
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(Unit) {
        reloadAccounts()
    }

    // Listener for refresh and login progress
    DisposableEffect(Unit) {
        val refreshListener = object : ExtraListener<Any> {
            override fun onValueSet(key: String, value: Any): Boolean {
                reloadAccounts()
                return false
            }
        }

        val microsoftLoginListener = object : ExtraListener<String> {
            override fun onValueSet(key: String, value: String): Boolean {
                val backgroundLogin = AuthType.MICROSOFT.createAuth()
                isAuthenticating = true
                backgroundLogin.createAccount(loginListener, value)
                return false
            }
        }

        val elyByLoginListener = object : ExtraListener<String> {
            override fun onValueSet(key: String, value: String): Boolean {
                val backgroundLogin = AuthType.ELY_BY.createAuth()
                isAuthenticating = true
                backgroundLogin.createAccount(loginListener, value)
                return false
            }
        }

        val mojangLoginListener = object : ExtraListener<Array<String>> {
            override fun onValueSet(key: String, value: Array<String>): Boolean {
                try {
                    val account = Accounts.create { acc: Account -> acc.username = value[0] }
                    loginListener.onLoginDone(account)
                    reloadAccounts()
                } catch (e: IOException) {
                    loginListener.onLoginError(e)
                }
                return false
            }
        }

        ExtraCore.addExtraListener(ExtraConstants.REFRESH_ACCOUNT_SPINNER, refreshListener)
        ExtraCore.addExtraListener(ExtraConstants.MICROSOFT_LOGIN_TODO, microsoftLoginListener)
        ExtraCore.addExtraListener(ExtraConstants.ELYBY_LOGIN_TODO, elyByLoginListener)
        ExtraCore.addExtraListener(ExtraConstants.MOJANG_LOGIN_TODO, mojangLoginListener)

        onDispose {
            ExtraCore.removeExtraListenerFromValue(ExtraConstants.REFRESH_ACCOUNT_SPINNER, refreshListener)
            ExtraCore.removeExtraListenerFromValue(ExtraConstants.MICROSOFT_LOGIN_TODO, microsoftLoginListener)
            ExtraCore.removeExtraListenerFromValue(ExtraConstants.ELYBY_LOGIN_TODO, elyByLoginListener)
            ExtraCore.removeExtraListenerFromValue(ExtraConstants.MOJANG_LOGIN_TODO, mojangLoginListener)
        }
    }

    val selectedAccount = if (selectedIndex >= 0 && selectedIndex < accounts.size) accounts[selectedIndex] else null

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clickable { expanded = true },
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(0.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedAccount != null) {
                        AccountItemContent(selectedAccount)
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(id = R.string.main_add_account),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp
                        )
                    }
                }
                
                if (isAuthenticating) {
                    LinearProgressIndicator(
                        progress = { loginProgress },
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .height(2.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.Transparent,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(300.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                )
                .clip(RoundedCornerShape(12.dp))
        ) {
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(id = R.string.main_add_account),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                onClick = {
                    expanded = false
                    ExtraCore.setValue(ExtraConstants.SELECT_AUTH_METHOD, true)
                }
            )

            accounts.forEachIndexed { index, account ->
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AccountItemContent(account)
                            }
                            IconButton(onClick = {
                                expanded = false
                                // Show delete dialog logic
                                MaterialAlertDialogBuilder(context)
                                    .setMessage(R.string.warning_remove_account)
                                    .setPositiveButton(android.R.string.cancel, null)
                                    .setNeutralButton(R.string.global_delete) { _, _ ->
                                        Accounts.delete(account)
                                        reloadAccounts()
                                    }
                                    .show()
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFFF5252)
                                )
                            }
                        }
                    },
                    onClick = {
                        expanded = false
                        Accounts.setCurrent(account)
                        reloadAccounts()
                    }
                )
            }
        }
    }
}

@Composable
fun AccountItemContent(account: Account) {
    val context = LocalContext.current
    val headBitmap = remember(account) {
        var bitmap = account.skinFace
        if (bitmap == null && account.isLocal) {
            try {
                val steveSkin = BitmapFactory.decodeStream(context.assets.open("steve.png"))
                bitmap = SkinHeadRenderer().render(100, steveSkin)
                steveSkin.recycle()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        bitmap?.asImageBitmap()
    }
    
    if (headBitmap != null) {
        Image(
            bitmap = headBitmap,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.FillBounds
        )
    } else {
        Box(modifier = Modifier.size(32.dp).background(Color.Gray, RoundedCornerShape(4.dp)))
    }
    
    Spacer(modifier = Modifier.width(16.dp))
    
    Column {
        Text(
            text = account.username,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
