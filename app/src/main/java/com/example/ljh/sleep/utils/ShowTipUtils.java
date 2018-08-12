package com.example.ljh.sleep.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * 封装显示提示消息的方法
 */
public class ShowTipUtils {
//    private static AlertDialog.Builder alertBuilder;
//    private static AlertDialog alertDialog;

    /**
     * showToast
     * @param context
     * @param string
     */
    public static void toastShort(Context context,String string){
        if(context != null){
            Toast.makeText(context,string,Toast.LENGTH_SHORT).show();
        }
    }

    public static void toastLong(Context context,String string){
        if(context != null) {
            Toast.makeText(context, string, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 显示对话提示框
     * @param context
     * @param string   内容
     * @param callBack 回调
     * @btCount 按钮数量判断是否设置"取消"按钮
     */
    public static void showAlertDialog(Context context,String string,int btCount,final AlertDialogCallback callBack) {
        if (context instanceof Activity) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(string);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    callBack.positive();
                }
            });
            //btCount == 2则设置取消按钮
            if (btCount == 2) {
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callBack.negative();
                    }
                });
            }
            builder.setCancelable(false);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public interface AlertDialogCallback {
        void positive();
        void negative();
    }
}
