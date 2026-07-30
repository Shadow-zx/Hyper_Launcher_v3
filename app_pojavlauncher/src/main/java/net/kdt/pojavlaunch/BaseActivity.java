package net.kdt.pojavlaunch;

import android.content.*;
import android.graphics.Color;
import android.os.*;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.*;
import net.kdt.pojavlaunch.utils.*;

import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_IGNORE_NOTCH;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleUtils.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (shouldEnableEdgeToEdge()) {
            EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowInsetsController controller = getWindow().getInsetsController();
                if (controller != null) {
                    controller.hide(WindowInsets.Type.systemBars());
                    controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                getWindow().setAttributes(layoutParams);
            }
        }

        super.onCreate(savedInstanceState);
        LocaleUtils.setLocale(this);

        if (!shouldEnableEdgeToEdge()) {
            Tools.setInsetsMode(this, setFullscreen(), shouldIgnoreNotch());
        } else {
            View insetView = findViewById(android.R.id.content);
            if (insetView != null) {
                insetView.setOnApplyWindowInsetsListener(null);
                insetView.setPadding(0, 0, 0, 0);
            }
        }

        Tools.getDisplayMetrics(this);
    }

    /** @return Whether the activity should be set as a fullscreen one */
    public boolean setFullscreen(){
        return true;
    }


    @Override
    public void startActivity(Intent i) {
        super.startActivity(i);
        //new Throwable("StartActivity").printStackTrace();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.checkStorageInteractive(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (!shouldEnableEdgeToEdge()) {
            Tools.setInsetsMode(this, setFullscreen(), shouldIgnoreNotch());
        } else {
            View insetView = findViewById(android.R.id.content);
            if (insetView != null) {
                insetView.setOnApplyWindowInsetsListener(null);
                insetView.setPadding(0, 0, 0, 0);
            }
        }
        Tools.getDisplayMetrics(this);
    }

    /** @return Whether or not the notch should be ignored */
    protected boolean shouldIgnoreNotch(){
        return PREF_IGNORE_NOTCH;
    }

    /** @return Whether the activity should enable Edge-to-Edge */
    protected boolean shouldEnableEdgeToEdge() {
        return false;
    }
}
