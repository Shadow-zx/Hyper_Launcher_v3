package com.ashmeet.hyperlauncher.fragments;

import android.content.Context;

import com.kdt.mcgui.ProgressLayout;

import net.kdt.pojavlaunch.instances.InstanceInstaller;
import net.kdt.pojavlaunch.instances.Instances;
import net.kdt.pojavlaunch.modloaders.ForgelikeUtils;
import net.kdt.pojavlaunch.modloaders.ModloaderListenerProxy;
import com.ashmeet.hyperlauncher.screens.layouts.modloader.ModloaderVersionGroup;
import com.ashmeet.hyperlauncher.screens.layouts.modloader.ModloaderVersionItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ForgelikeInstallFragment extends ModVersionListFragment<List<String>> {
    private final ForgelikeUtils mUtils;
    public ForgelikeInstallFragment(ForgelikeUtils utils, String mFragmentTag) {
        super(mFragmentTag);
        this.mUtils = utils;
    }

    @Override
    public List<String> loadVersionList() throws IOException {
        return mUtils.downloadVersions();
    }

    @Override
    public List<ModloaderVersionGroup<Object>> mapToGroups(List<String> forgeVersions) {
        List<String> gameVersions = new ArrayList<>();
        List<List<String>> loaderVersions = new ArrayList<>();

        for(String version : forgeVersions) {
            if(mUtils.shouldSkipVersion(version)) continue;
            String gameVersion = mUtils.processVersionString(version);
            List<String> versionList;
            int gameVersionIndex = gameVersions.indexOf(gameVersion);
            if(gameVersionIndex != -1) {
                versionList = loaderVersions.get(gameVersionIndex);
            } else {
                versionList = new ArrayList<>();
                gameVersions.add(gameVersion);
                loaderVersions.add(versionList);
            }
            versionList.add(version);
        }

        if(mUtils.isVersionOrderInversed()) {
            for (List<String> versionList : loaderVersions) {
                Collections.reverse(versionList);
            }
            Collections.reverse(loaderVersions);
            Collections.reverse(gameVersions);
        }

        List<ModloaderVersionGroup<Object>> groups = new ArrayList<>();
        for (int i = 0; i < gameVersions.size(); i++) {
            List<ModloaderVersionItem<Object>> items = new ArrayList<>();
            for (String v : loaderVersions.get(i)) {
                items.add(new ModloaderVersionItem<>(v, v));
            }
            groups.add(new ModloaderVersionGroup<>(gameVersions.get(i), items));
        }
        return groups;
    }

    @Override
    public Runnable createDownloadTask(Object selectedVersion, ModloaderListenerProxy listenerProxy) {
        return ()->createInstance((String) selectedVersion, listenerProxy);
    }

    @Override
    public void onDownloadFinished(Context context, File downloadedFile) {
    }

    private void createInstance(String selectedVersion, ModloaderListenerProxy listenerProxy) {
        try {
            ProgressLayout.setProgress(ProgressLayout.INSTALL_MODPACK, 0);
            InstanceInstaller instanceInstaller = mUtils.createInstaller(selectedVersion);
            Instances.createInstance(instance -> {
                instance.name = mUtils.getName();
                instance.icon = mUtils.getIconName();
                instance.installer = instanceInstaller;
            }, selectedVersion);
            ProgressLayout.clearProgress(ProgressLayout.INSTALL_MODPACK);
            instanceInstaller.start();
            listenerProxy.onDownloadFinished(null);
        }catch (IOException e) {
            listenerProxy.onDownloadError(e);
        }
    }
}
