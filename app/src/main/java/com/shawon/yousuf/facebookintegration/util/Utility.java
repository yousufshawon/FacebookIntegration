package com.shawon.yousuf.facebookintegration.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by user on 4/20/2016.
 */
public class Utility {

    public static void showSimpleAlertDialog(Context context, String title, String message){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null);

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }

}
