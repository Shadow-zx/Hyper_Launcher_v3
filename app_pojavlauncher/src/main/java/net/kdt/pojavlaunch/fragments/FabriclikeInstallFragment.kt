package net.kdt.pojavlaunch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.ashmeet.hyperlauncher.R
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.extra.ExtraCore
import net.kdt.pojavlaunch.instances.Instances
import net.kdt.pojavlaunch.modloaders.FabricVersion
import net.kdt.pojavlaunch.modloaders.FabriclikeUtils
import net.kdt.pojavlaunch.modloaders.ModloaderDownloadListener
import net.kdt.pojavlaunch.modloaders.ModloaderListenerProxy
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper
import net.kdt.pojavlaunch.screens.layouts.FabriclikeInstallScreen
import net.kdt.pojavlaunch.screens.theme.PojavTheme
import java.io.File
import java.io.IOException

abstract class FabriclikeInstallFragment(
    private val mFabriclikeUtils: FabriclikeUtils,
    private val mFragmentTag: String
) : Fragment(), ModloaderDownloadListener {
    private val mExtraTag: String = mFragmentTag + "_proxy"

    private var gameVersions by mutableStateOf<List<FabricVersion>>(emptyList())
    private var loaderVersions by mutableStateOf<List<FabricVersion>>(emptyList())
    private var isLoading by mutableStateOf(false)
    private var isInstalling by mutableStateOf(false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PojavTheme {
                    FabriclikeInstallScreen(
                        title = getString(R.string.fabric_dl_loader_title),
                        loaderName = mFabriclikeUtils.name,
                        isLoading = isLoading,
                        isInstalling = isInstalling,
                        gameVersions = gameVersions,
                        loaderVersions = loaderVersions,
                        onBack = { parentFragmentManager.popBackStack() },
                        onInstall = { gameVersion, loaderVersion ->
                            performInstallation(gameVersion, loaderVersion)
                        }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val proxy = getListenerProxy()
        if (proxy != null) {
            isInstalling = true
            proxy.attachListener(this)
        }
        loadVersions()
    }

    private fun loadVersions() {
        isLoading = true
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val gv = mFabriclikeUtils.downloadGameVersions()
                val lv = mFabriclikeUtils.downloadLoaderVersions()
                withContext(Dispatchers.Main) {
                    gameVersions = gv?.toList() ?: emptyList()
                    loaderVersions = lv?.toList() ?: emptyList()
                    isLoading = false
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    Tools.showError(requireContext(), e)
                }
            }
        }
    }

    private fun performInstallation(gameVersion: String, loaderVersion: String) {
        if (ProgressKeeper.hasOngoingTasks()) {
            Toast.makeText(requireContext(), R.string.tasks_ongoing, Toast.LENGTH_LONG).show()
            return
        }
        val proxy = ModloaderListenerProxy()
        proxy.attachListener(this)
        setListenerProxy(proxy)
        isInstalling = true

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val versionId = mFabriclikeUtils.install(gameVersion, loaderVersion)
                if (versionId == null) {
                    withContext(Dispatchers.Main) {
                        getListenerProxy()?.onDataNotAvailable()
                    }
                    return@launch
                }
                Instances.createInstance({ i ->
                    i.name = mFabriclikeUtils.name
                    i.icon = mFabriclikeUtils.iconName
                    i.versionId = versionId
                }, versionId)
                withContext(Dispatchers.Main) {
                    getListenerProxy()?.onDownloadFinished(null)
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Tools.showErrorRemote(e)
                }
            }
        }
    }

    override fun onDownloadFinished(downloadedFile: File?) {
        Tools.runOnUiThread {
            getListenerProxy()?.detachListener()
            setListenerProxy(null)
            isInstalling = false
            parentFragmentManager.popBackStackImmediate()
        }
    }

    override fun onDataNotAvailable() {
        Tools.runOnUiThread {
            val context = requireContext()
            getListenerProxy()?.detachListener()
            setListenerProxy(null)
            isInstalling = false
            Tools.dialog(
                context,
                context.getString(R.string.global_error),
                context.getString(R.string.fabric_dl_cant_read_meta, mFabriclikeUtils.name)
            )
        }
    }

    override fun onDownloadError(e: Exception) {
        Tools.runOnUiThread {
            getListenerProxy()?.detachListener()
            setListenerProxy(null)
            isInstalling = false
            Tools.showError(requireContext(), e)
        }
    }

    private fun getListenerProxy(): ModloaderListenerProxy? {
        return ExtraCore.getValue(mExtraTag) as? ModloaderListenerProxy
    }

    private fun setListenerProxy(listenerProxy: ModloaderListenerProxy?) {
        ExtraCore.setValue(mExtraTag, listenerProxy)
    }
}
