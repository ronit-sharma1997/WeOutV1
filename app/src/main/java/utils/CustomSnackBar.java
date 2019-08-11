package utils;

import android.content.Context;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.app.WeOut.R;
import com.google.android.material.snackbar.Snackbar;

/**
 * Class to represent a Snack Bar and to provide customizations such as message displayed and color.
 */
public class CustomSnackBar {

    public CustomSnackBar() {

    }

    // Function to display a snackbar with a set message
    public void display(View view, Context context, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        View messageBarView = snackbar.getView();
        messageBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.lightBlue));
        snackbar.show();
    }

    public void display(View view, Context context, String message, int colorID) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        View messageBarView = snackbar.getView();
        messageBarView.setBackgroundColor(ContextCompat.getColor(context, colorID));
        snackbar.show();
    }
}
