package de.dhbw.corona_world_app.ui.tools;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class ErrorDialog {


    public static AlertDialog createBasicErrorDialog(Context context, String title, String message, DialogInterface.OnClickListener onClickPositiveButton,String positiveButtonText){
        if(positiveButtonText==null)positiveButtonText= context.getString(android.R.string.ok);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonText, onClickPositiveButton);
        return builder.create();
    }
    public static AlertDialog createBasicErrorDialog(Context context, String title, String message, DialogInterface.OnClickListener onClickPositiveButton){
        return createBasicErrorDialog(context, title, message, onClickPositiveButton,null);
    }

    public static AlertDialog createTwoOptionErrorDialog(Context context,String title,String message,DialogInterface.OnClickListener onClickPositiveButton,DialogInterface.OnClickListener onClickNegativeButton, String positiveButtonText,String negativeButtonText){
        if(positiveButtonText==null)positiveButtonText= context.getString(android.R.string.ok);
        if(negativeButtonText==null)negativeButtonText= context.getString(android.R.string.cancel);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonText, onClickPositiveButton);
        builder.setNegativeButton(negativeButtonText,onClickNegativeButton);
        return builder.create();
    }

    public static AlertDialog createTwoOptionErrorDialog(Context context,String title,String message,DialogInterface.OnClickListener onClickPositiveButton,DialogInterface.OnClickListener onClickNegativeButton){
        return createTwoOptionErrorDialog(context, title, message, onClickPositiveButton, onClickNegativeButton,null,null);
    }
}
