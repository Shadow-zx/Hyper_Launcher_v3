package net.kdt.pojavlaunch.screens.compose

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
            val refreshAccount = account.reload() ?: return@waitUntilDone
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
                    ExtraCore.setValue(ExtraConstants.REFRESH_ACCOUNT_SPINNER, true)
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
                                    tint = MaterialTheme.colorScheme.error
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
        var bitmap = account.skinFace3D
        if (bitmap == null) {
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
    
    Box(modifier = Modifier.size(32.dp)) {
        if (headBitmap != null) {
            Image(
                bitmap = headBitmap,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.FillBounds
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Color.Gray, RoundedCornerShape(4.dp)))
        }

        if (account.authType != AuthType.LOCAL && account.authType.iconResource != 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
                    .padding(2.dp)
            ) {
                Icon(
                    painter = painterResource(id = account.authType.iconResource),
                    contentDescription = null,
                    modifier = Modifier.size(10.dp),
                    tint = Color.Unspecified
                )
            }
        }
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
