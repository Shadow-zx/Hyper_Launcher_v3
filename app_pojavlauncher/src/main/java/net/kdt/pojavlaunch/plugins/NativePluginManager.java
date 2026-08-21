package net.kdt.pojavlaunch.plugins;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativePluginManager {
    private static final List<NativePlugin> sPlugins = new ArrayList<>();

    public static void registerPlugin(NativePlugin plugin) {
        sPlugins.add(plugin);
    }

    public static void discoverAarPlugins(Context context) {
        // MobileGlues
        registerPlugin(new NativePlugin() {
            @Override
            public String[] getPaths() {
                return new String[]{context.getApplicationInfo().nativeLibraryDir};
            }

            @Override
            public Map<String, String> getJVMEnv() {
                return new HashMap<>();
            }
        });
    }

    public static String getRuntimeLibraryPath() {
        StringBuilder sb = new StringBuilder();
        for (NativePlugin plugin : sPlugins) {
            for (String path : plugin.getPaths()) {
                if (sb.length() > 0) {
                    sb.append(":");
                }
                sb.append(path);
            }
        }
        return sb.toString();
    }

    public static Map<String, String> getJVMEnv() {
        Map<String, String> env = new HashMap<>();
        for (NativePlugin plugin : sPlugins) {
            env.putAll(plugin.getJVMEnv());
        }
        return env;
    }
}
