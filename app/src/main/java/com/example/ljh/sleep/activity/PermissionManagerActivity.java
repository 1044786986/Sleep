package com.example.ljh.sleep.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.example.ljh.sleep.callback.AlertDialogCallback;
import com.example.ljh.sleep.callback.PermissionResultCallback;
import com.example.ljh.sleep.utils.ShowTipUtils;

public class PermissionManagerActivity extends AppCompatActivity{
    public static final int RESULT_CODE = 10000;
    public static final int CODE_WRITE_EXTERNAL_STORAGE = 0;
    public static final String permissions[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final int permissionCodes[] = {CODE_WRITE_EXTERNAL_STORAGE};
    private PermissionResultCallback permissionResultCallback;

    public void applyPermission(String permissions[],PermissionResultCallback callback){
        permissionResultCallback = callback;
        if(!checkPermissions(permissions)){
            ActivityCompat.requestPermissions(this,permissions,RESULT_CODE);
        }else{
            permissionResultCallback.onSuccess();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == RESULT_CODE){
            if(!checkPermissionResult(grantResults)){
                String string = "应用缺少必要权限,某些功能可能无法使用,请到设置中心授权";
                ShowTipUtils.showAlertDialog(this, string, 2, new ShowTipUtils.AlertDialogCallback() {
                    @Override
                    public void positive() {
                        toSetting();
                    }

                    @Override
                    public void negative() {
                        permissionResultCallback.onFailed();
                    }
                });
            }else{
                permissionResultCallback.onSuccess();
            }
        }
    }

    private void toSetting(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private boolean checkPermissions(String permissions[]){
        for(String string:permissions){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            ContextCompat.checkSelfPermission(this,string) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    private boolean checkPermissionResult(int grantResults[]){
        if(grantResults.length <= 0){
            return false;
        }
        for(int grant:grantResults){
            if(grant != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}
