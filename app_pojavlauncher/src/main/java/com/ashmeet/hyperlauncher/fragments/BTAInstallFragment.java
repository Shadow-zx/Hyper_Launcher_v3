package com.ashmeet.hyperlauncher.fragments;

import android.content.Context;

import com.ashmeet.hyperlauncher.screens.layouts.modloader.ModloaderVersionGroup;
import com.ashmeet.hyperlauncher.screens.layouts.modloader.ModloaderVersionItem;

import net.ashmeet.hyperlauncher.R;
import net.kdt.pojavlaunch.modloaders.BTADownloadTask;
import net.kdt.pojavlaunch.modloaders.BTAUtils;
import net.kdt.pojavlaunch.modloaders.ModloaderListenerProxy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BTAInstallFragment extends ModVersionListFragment<BTAUtils.BTAVersionList> {
    public static final String TAG = "BTAInstallFragment";

    public BTAInstallFragment() {
        super(TAG);
    }

    @Override
    public int getTitleText() {
        return R.string.select_bta_version;
    }

    @Override
    public int getNoDataMsg() {
        return R.string.modloader_dl_failed_to_load_list;
    }

    @Override
    public BTAUtils.BTAVersionList loadVersionList() throws IOException {
        return BTAUtils.downloadVersionList();
    }

    @Override
    public List<ModloaderVersionGroup<Object>> mapToGroups(BTAUtils.BTAVersionList versionList) {
        List<ModloaderVersionGroup<Object>> groups = new ArrayList<>();
        if(!versionList.testedVersions.isEmpty()) {
            groups.add(createGroup(R.string.bta_installer_available_versions, versionList.testedVersions));
        }
        if(!versionList.untestedVersions.isEmpty()) {
            groups.add(createGroup(R.string.bta_installer_untested_versions, versionList.untestedVersions));
        }
        if(!versionList.nightlyVersions.isEmpty()) {
            groups.add(createGroup(R.string.bta_installer_nightly_versions, versionList.nightlyVersions));
        }
        return groups;
    }

    private ModloaderVersionGroup<Object> createGroup(int titleRes, List<BTAUtils.BTAVersion> versions) {
        List<ModloaderVersionItem<Object>> items = new ArrayList<>();
        for (BTAUtils.BTAVersion version : versions) {
            items.add(new ModloaderVersionItem<>(version.versionName, version));
        }
        return new ModloaderVersionGroup<>(getString(titleRes), items);
    }

    @Override
    public Runnable createDownloadTask(Object selectedVersion, ModloaderListenerProxy listenerProxy) {
        return new BTADownloadTask(listenerProxy, (BTAUtils.BTAVersion) selectedVersion);
    }

    @Override
    public void onDownloadFinished(Context context, File downloadedFile) {
        // We don't have to do anything after the BTADownloadTask ends, so this is a stub
    }
}
