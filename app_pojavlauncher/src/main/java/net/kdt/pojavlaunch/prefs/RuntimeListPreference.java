package net.kdt.pojavlaunch.prefs;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.DialogPreference;

import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.multirt.Runtime;

public class RuntimeListPreference extends DialogPreference {

    public RuntimeListPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public RuntimeListPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RuntimeListPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RuntimeListPreference(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        updateSummary();
    }

    public void updateSummary() {
        String defaultRuntime = LauncherPreferences.PREF_DEFAULT_RUNTIME;
        if (defaultRuntime != null) {
            Runtime runtime = MultiRTUtils.read(defaultRuntime);
            if (runtime.versionString != null) {
                setSummary(runtime.name.replace(".tar.xz", "").replace("-", " ") + " (" + runtime.versionString + ")");
            } else {
                setSummary(runtime.name);
            }
        }
    }
}
