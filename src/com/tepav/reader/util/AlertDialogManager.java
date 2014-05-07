package com.tepav.reader.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.tepav.reader.R;
import com.tepav.reader.activity.Login;

/**
 * Author   : kanilturgut
 * Date     : 05/05/14
 * Time     : 10:23
 */
public class AlertDialogManager {

    /**
     * Function to display simple Alert Dialog
     *
     * @param context - application context
     * @param title   - alert dialog title
     * @param message - alert message
     * @param status  - success/failure (used to set icon)
     *                - pass null if you don't want icon
     */
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        if (status != null)
            // Setting alert dialog icon
            alertDialog.setIcon((status) ? R.drawable.ic_action_accept : R.drawable.ic_action_cancel);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    /**
     * Function to display simple Alert Dialog
     *
     * @param context - application context
     * @param title   - alert dialog title
     * @param message - alert message
     * @param status  - success/failure (used to set icon)
     *                - pass null if you don't want icon
     */
    public void showLoginDialog(final Context context, String title, String message, Boolean status) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        if (status != null)
            // Setting alert dialog icon
            alertDialog.setIcon((status) ? R.drawable.ic_action_accept : R.drawable.ic_action_warning);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                context.startActivity(new Intent(context, Login.class));
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

}
