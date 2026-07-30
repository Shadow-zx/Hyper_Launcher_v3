package net.kdt.pojavlaunch.prefs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import net.ashmeet.hyperlauncher.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.contracts.OpenDocumentWithExtension;
import net.kdt.pojavlaunch.multirt.RTRecyclerViewAdapter;
import net.kdt.pojavlaunch.multirt.Runtime;

public class RuntimeListPreferenceDialogFragment extends PreferenceDialogFragmentCompat implements RTRecyclerViewAdapter.OnRuntimeSelectedListener {

    private RecyclerView mRecyclerView;
    private RTRecyclerViewAdapter mAdapter;
    private final ActivityResultLauncher<Object> mVmInstallLauncher =
            registerForActivityResult(new OpenDocumentWithExtension("xz"), (data)->{
                if(data != null) {
                    Tools.installRuntimeFromUri(getContext(), data);
                    // We can't easily know when it's done, so we'll just refresh after a bit
                    // or better, the user can reopen the dialog.
                    // But let's try to refresh after 2 seconds as a heuristic if the dialog is still open.
                    if (mRecyclerView != null) {
                        mRecyclerView.postDelayed(() -> {
                            if (mAdapter != null) mAdapter.notifyDataSetChanged();
                        }, 2000);
                    }
                }
            });

    public static RuntimeListPreferenceDialogFragment newInstance(String key) {
        RuntimeListPreferenceDialogFragment fragment = new RuntimeListPreferenceDialogFragment();
        Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mRecyclerView = new RecyclerView(requireContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mAdapter = new RTRecyclerViewAdapter();
        mAdapter.setOnRuntimeSelectedListener(this);
        mRecyclerView.setAdapter(mAdapter);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getPreference().getDialogTitle())
                .setView(mRecyclerView)
                .setPositiveButton(R.string.multirt_config_add, (d, w) -> mVmInstallLauncher.launch(null))
                .setNegativeButton(R.string.global_delete, null)
                .create();

        dialog.setOnShowListener(d -> {
            Button deleteButton = ((AlertDialog) d).getButton(AlertDialog.BUTTON_NEGATIVE);
            deleteButton.setOnClickListener(v -> {
                boolean isDeleting = !mAdapter.getIsEditing();
                mAdapter.setIsEditing(isDeleting);
                deleteButton.setText(isDeleting ? R.string.multirt_config_setdefault : R.string.global_delete);
            });
        });

        return dialog;
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        // Handled by adapter callbacks
    }

    @Override
    public void onRuntimeSelected(Runtime runtime) {
        if (getPreference() instanceof RuntimeListPreference) {
            ((RuntimeListPreference) getPreference()).updateSummary();
        }
        dismiss();
    }

    @Override
    public void onRuntimeDeleted() {
        if (getPreference() instanceof RuntimeListPreference) {
            ((RuntimeListPreference) getPreference()).updateSummary();
        }
    }
}
