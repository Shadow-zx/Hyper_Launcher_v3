package net.kdt.pojavlaunch

import net.kdt.pojavlaunch.plugins.NativePluginManager

class LaunchArgs {
    companion object {
        @JvmStatic
        fun getNativePluginArgs(): List<String> {
            val args = mutableListOf<String>()
            NativePluginManager.getJVMEnv().forEach { (key, value) ->
                args.add("-D$key=$value")
            }
            return args
        }
    }
}
