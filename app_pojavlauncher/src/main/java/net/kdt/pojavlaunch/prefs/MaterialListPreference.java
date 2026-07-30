package net.kdt.pojavlaunch.prefs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * A ListPreference that ensures it uses the Material 3 MaterialAlertDialogBuilder.
 */
public class MaterialListPreference extends ListPreference {

    public MaterialListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MaterialListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MaterialListPreference(Context context, AttributeSet attrs) {
        this(context, attrs, androidx.preference.R.attr.dialogPreferenceStyle);
    }

    public MaterialListPreference(Context context) {
        this(context, null);
    }

    public static class MaterialListPreferenceDialogFragment extends PreferenceDialogFragmentCompat {
        private int mClickedDialogEntryIndex;

        public static MaterialListPreferenceDialogFragment newInstance(String key) {
            MaterialListPreferenceDialogFragment fragment = new MaterialListPreferenceDialogFragment();
            Bundle b = new Bundle(1);
            b.putString(ARG_KEY, key);
            fragment.setArguments(b);
            return fragment;
        }

        private MaterialListPreference getListPreference() {
            return (MaterialListPreference) getPreference();
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            MaterialListPreference preference = getListPreference();
            mClickedDialogEntryIndex = preference.findIndexOfValue(preference.getValue());

            return new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(preference.getDialogTitle())
                    .setIcon(preference.getDialogIcon())
                    .setPositiveButton(preference.getPositiveButtonText(), this)
                    .setNegativeButton(preference.getNegativeButtonText(), this)
                    .setSingleChoiceItems(preference.getEntries(), mClickedDialogEntryIndex, (dialog, which) -> {
                        mClickedDialogEntryIndex = which;
                        // For ListPreference, clicking an item usually closes the dialog and saves.
                        MaterialListPreferenceDialogFragment.this.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        dialog.dismiss();
                    })
                    .create();
        }

        @Override
        public void onDialogClosed(boolean positiveResult) {
            MaterialListPreference preference = getListPreference();
            if (positiveResult && mClickedDialogEntryIndex >= 0 && preference.getEntryValues() != null) {
                String value = preference.getEntryValues()[mClickedDialogEntryIndex].toString();
                if (preference.callChangeListener(value)) {
                    preference.setValue(value);
                }
            }
        }
    }
}
