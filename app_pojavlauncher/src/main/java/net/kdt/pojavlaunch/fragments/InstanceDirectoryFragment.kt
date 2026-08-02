package net.kdt.pojavlaunch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.screens.layouts.InstanceDirectoryScreen
import net.kdt.pojavlaunch.screens.theme.PojavTheme

class InstanceDirectoryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PojavTheme {
                    InstanceDirectoryScreen(
                        onBack = { Tools.removeCurrentFragment(requireActivity()) }
                    )
                }
            }
        }
    }

    companion object {
        const val TAG = "InstanceDirectoryFragment"
    }
}
