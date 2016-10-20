package utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by Lenovo on 2016/7/24.
 */
public class AlertUtilts {
    public static void showDialog(final Activity context,String title,String msg,DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context).setCancelable(false).setTitle(title)//设置对话框标题
                .setMessage(msg)//设置显示的内容
                .setPositiveButton("确定", okListener).show();//在按键响应事件中显示此对话框
    }

    public static void showDialog(final Activity context,String title,String msg,DialogInterface.OnClickListener okListener,DialogInterface.OnClickListener cancleListener) {
        new AlertDialog.Builder(context).setCancelable(false).setTitle(title)//设置对话框标题
                .setMessage(msg).setNegativeButton("确定",okListener)//设置显示的内容
                .setPositiveButton("取消", cancleListener).show();//在按键响应事件中显示此对话框
    }

}
