package com.anand.android.passwordwallet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {

    private static final String TAG = "DriveServiceHelper";
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    Activity activity;
    private Drive mDriveService;
    private String id;

    public String getId() {
        return id;
    }

    public DriveServiceHelper(Activity activity) {
        this.activity = activity;
    }

    public Task<String> createFileSql() {
        return Tasks.call(mExecutor, () -> {

            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(activity);
            GoogleAccountCredential credential = GoogleAccountCredential.
                    usingOAuth2(activity, Collections.singleton(DriveScopes.DRIVE_FILE));

            assert acct != null;
            credential.setSelectedAccount(acct.getAccount());

            mDriveService = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(),
                    credential).setApplicationName(activity.getPackageName()).build();

            @SuppressLint("SdCardPath") String filePath = activity.getDatabasePath("SyncToDrive.db").getAbsolutePath();
            File fileMetaData = new File();
            fileMetaData.setName("SyncToDrive.db");
            fileMetaData.setParents(Collections.singletonList("root"));
            fileMetaData.setMimeType("application/vnd.sqlite3");

            java.io.File file = new java.io.File(filePath);

            FileContent mediaContent = new FileContent("application/vnd.sqlite3", file);
            File myFile = null;

            try {
                myFile = mDriveService.files().create(fileMetaData, mediaContent).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (myFile == null) {
                throw new IOException("Null result when requesting file creation");
            }
            id = myFile.getId();
            return myFile.getId();
        });
    }

    public Task<Void> downloadFile(final String fileId) {
        return Tasks.call(mExecutor, () -> {

            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(activity);
            GoogleAccountCredential credential = GoogleAccountCredential.
                    usingOAuth2(activity, Collections.singleton(DriveScopes.DRIVE_FILE));

            assert acct != null;
            credential.setSelectedAccount(acct.getAccount());

            mDriveService = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(),
                    credential).setApplicationName(activity.getPackageName()).build();

            Log.i(TAG, "downloadFile: " + fileId);
            @SuppressLint("SdCardPath") String filePath = activity.getDatabasePath("SyncToDrive.db").getAbsolutePath();
            java.io.File file = new java.io.File(filePath);

            OutputStream outputStream = new FileOutputStream(file);
            mDriveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);

            outputStream.flush();
            outputStream.close();
            return null;

        });
    }


    public Task<Void> deletePreviousFile(final String fileId) {
        return Tasks.call(mExecutor, () -> {
            Log.i(TAG, "deletePreviousFile: delete file " + fileId);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(activity);
            GoogleAccountCredential credential = GoogleAccountCredential.
                    usingOAuth2(activity, Collections.singleton(DriveScopes.DRIVE_FILE));

            assert acct != null;
            credential.setSelectedAccount(acct.getAccount());

            mDriveService = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(),
                    credential).setApplicationName(activity.getPackageName()).build();

            if (fileId != null) {
                mDriveService.files().delete(fileId).execute();
            }
            return null;
        });
    }


}
