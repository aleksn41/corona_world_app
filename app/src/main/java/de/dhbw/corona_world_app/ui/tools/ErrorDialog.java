package de.dhbw.corona_world_app.ui.tools;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import de.dhbw.corona_world_app.R;

/**
 * This is a helper Class used to easily display an Errordialog
 *
 * @author Aleksandr Stankoski
 */
public class ErrorDialog {

    public static void showBasicErrorDialog(Context context, ErrorCode errorCode, DialogInterface.OnClickListener onClickPositiveButton, String positiveButtonText) {
        createBasicErrorDialog(context, errorCode, onClickPositiveButton, positiveButtonText).show();
    }

    public static void showBasicErrorDialog(Context context, ErrorCode errorCode, DialogInterface.OnClickListener onClickPositiveButton) {
        createBasicErrorDialog(context, errorCode, onClickPositiveButton, null).show();
    }

    public static AlertDialog createBasicErrorDialog(Context context, ErrorCode errorCode, DialogInterface.OnClickListener onClickPositiveButton, String positiveButtonText) {
        if (positiveButtonText == null) positiveButtonText = context.getString(android.R.string.ok);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.error_title, errorCode.code, errorCode.toString()));
        builder.setMessage(errorCode.message);
        builder.setPositiveButton(positiveButtonText, onClickPositiveButton);
        return builder.create();
    }

}
