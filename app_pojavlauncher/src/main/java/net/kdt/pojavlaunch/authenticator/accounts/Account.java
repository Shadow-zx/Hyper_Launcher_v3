package net.kdt.pojavlaunch.authenticator.accounts;


import android.graphics.BitmapFactory;
import android.util.Log;

import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.authenticator.AuthType;
import net.kdt.pojavlaunch.utils.FileUtils;
import net.kdt.pojavlaunch.utils.JSONUtils;

import com.ashmeet.hyperlauncher.skin.SkinModelType;

import java.io.*;
import java.net.URL;

import android.graphics.Bitmap;

import androidx.annotation.Keep;

import com.google.gson.JsonParseException;

import org.apache.commons.io.IOUtils;

@Keep
public class Account {
    public transient File mSaveLocation;
    public String accessToken = "0"; // access token
    public String profileId = "00000000-0000-0000-0000-000000000000"; // profile UUID, for obtaining skin
    public String username = "Steve";
    public AuthType authType = AuthType.LOCAL;
    public boolean isMicrosoft = false;
    public String refreshToken = "0";
    public String xuid;
    public long expiresAt;
    public String skinPath;
    public String capePath;
    public SkinModelType skinModel;
    private transient Bitmap mFaceCache;
    private transient Bitmap mFaceCache3D;
    private transient boolean mIsUpdatingSkin = false;

    public Account() {}

    public void updateSkinFace() {
        if (mIsUpdatingSkin) return;
        mIsUpdatingSkin = true;
        String skinFaceUrlTemplate = authType.skinUrl;
        if(skinFaceUrlTemplate == null) {
            mIsUpdatingSkin = false;
            return;
        }
        String skinFaceUrl = String.format(skinFaceUrlTemplate, username);
        try {
            Log.i("SkinLoader", "Updating skin face...");
            File skinFile = getSkinFaceFile();
            File skinFile3D = getSkinFaceFile3D();
            
            // Streaming it directly breaks on some devices
            byte[] skinBytes = IOUtils.toByteArray(new URL(skinFaceUrl));
            Bitmap skinBitmap = BitmapFactory.decodeByteArray(skinBytes, 0, skinBytes.length);
            if(skinBitmap == null) {
                mIsUpdatingSkin = false;
                return;
            }
            
            SkinHeadRenderer renderer = new SkinHeadRenderer();
            
            // Render 2D
            Bitmap skinFace = renderer.render2D(100, skinBitmap);
            if(skinFace != null) {
                try(FileOutputStream fileOutputStream = new FileOutputStream(skinFile)) {
                    skinFace.compress(Bitmap.CompressFormat.WEBP, 90, fileOutputStream);
                }
                skinFace.recycle();
            }
            
            // Render 3D
            Bitmap skinFace3D = renderer.render(100, skinBitmap);
            if(skinFace3D != null) {
                try(FileOutputStream fileOutputStream = new FileOutputStream(skinFile3D)) {
                    skinFace3D.compress(Bitmap.CompressFormat.WEBP, 90, fileOutputStream);
                }
                skinFace3D.recycle();
            }
            
            skinBitmap.recycle();
            Log.i("SkinLoader", "Update skin face success");
        } catch (IOException e) {
            // Skin refresh limit, no internet connection, etc...
            // Simply ignore updating skin face
            Log.w("SkinLoader", "Could not update skin face", e);
        } finally {
            mIsUpdatingSkin = false;
        }
    }

    public boolean isLocal(){
        return accessToken.equals("0");
    }
    
    public void save() throws IOException {
        FileUtils.ensureParentDirectory(mSaveLocation);
        JSONUtils.writeToFile(mSaveLocation, this);
    }

    public Account reload() {
        try {
            Account account = JSONUtils.readFromFile(mSaveLocation, Account.class);
            if(account == null) return null;
            account.mSaveLocation = mSaveLocation;
            return account;
        }catch (IOException | JsonParseException e) {
            return null;
        }
     }

    public Bitmap getSkinFace(){
        if(mFaceCache != null) return mFaceCache;
        if(isLocal()) return null;
        File skinFaceFile = getSkinFaceFile();
        if(mFaceCache == null) {
            mFaceCache = BitmapFactory.decodeFile(skinFaceFile.getAbsolutePath());
        }
        return mFaceCache;
    }

    public Bitmap getSkinFace3D(){
        if(isLocal()) return null;
        File skinFaceFile3D = getSkinFaceFile3D();
        if(!skinFaceFile3D.exists()) {
            // Trigger an update in the background if 3D face is missing
            PojavApplication.sExecutorService.execute(this::updateSkinFace);
            return null;
        }
        if(mFaceCache3D == null) {
            mFaceCache3D = BitmapFactory.decodeFile(skinFaceFile3D.getAbsolutePath());
        }
        return mFaceCache3D;
    }

    private File getSkinFaceFile() {
        return new File(Tools.DIR_CACHE,  "skin-face-" + profileId +"-"+authType.name() + ".webp");
    }

    private File getSkinFaceFile3D() {
        return new File(Tools.DIR_CACHE,  "skin-face-3d-" + profileId +"-"+authType.name() + ".webp");
    }
}
