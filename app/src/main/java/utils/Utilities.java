package utils;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class Utilities {

    private static String TAG = "Utilities";

    public static boolean isEmpty(EditText editText) {
        if (editText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    public static boolean isEmpty(TextView textView) {
        if (textView.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    public static boolean isPastCurrentDay(int yearSelected, int monthSelected, int daySelected) {
        boolean result = false;

        int currYear, currMonth, currDay;

        Calendar cal = Calendar.getInstance();

        currYear = cal.get(Calendar.YEAR);
        currMonth = cal.get(Calendar.MONTH);
        currDay = cal.get(Calendar.DAY_OF_MONTH);

        if (currYear > yearSelected) {
            result = true;
        }
        else if (currMonth > monthSelected) {
            result = true;
        }
        else if (currDay > daySelected) {
            result = true;
        }

        return result;
    }

    public static boolean isToday(int yearSelected, int monthSelected, int daySelected) {
        boolean result = false;

        int currYear, currMonth, currDay;

        Calendar cal = Calendar.getInstance();

        currYear = cal.get(Calendar.YEAR);
        currMonth = cal.get(Calendar.MONTH) + 1;
        currDay = cal.get(Calendar.DAY_OF_MONTH);

        Log.d(TAG, "Date Selected: " + yearSelected + "-" + monthSelected + "-" + daySelected);
        Log.d(TAG, "Curr Date: " + currYear + "-" + currMonth + "-" + currDay);

        if (currYear == yearSelected && currMonth == monthSelected && currDay == daySelected) {
            result = true;
        }

        return result;
    }

    public static boolean isPastCurrentTime(int hourSelected, int minuteSelected) {

        boolean result = false;
        boolean isAM;

        int currHour, currMinute;

        Calendar cal = Calendar.getInstance();

        currHour = cal.get(Calendar.HOUR);
        currMinute = cal.get(Calendar.MINUTE);

        isAM = (cal.get(Calendar.AM_PM) == Calendar.AM) ? true : false;
        if (!isAM) {
            currHour += 12;
        }

        Log.d(TAG, "Hr selected: " + hourSelected + ", Min selected: " + minuteSelected);
        Log.d(TAG, "Curr Hr: " + currHour + ", Curr Min: " + currMinute);

        if (currHour > hourSelected) {
            result = true;
        }
        else if (currMinute > minuteSelected) {
            result = true;
        }

        return result;
    }

}
