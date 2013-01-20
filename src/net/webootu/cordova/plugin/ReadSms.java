package net.webootu.cordova.plugin;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Most Cordova plugins have all their functionality in one file, therefore the same
 * pattern will be applied here. execute(...) method will be delegating to other
 * private methods, based on action.
 * Some ideas from this SO post: {@link http://stackoverflow.com/questions/5946262/read-only-specificonly-from-particular-number-inbox-messages-and-display-throu}
 */
public class ReadSms extends CordovaPlugin {
    private static final String TAG = "ReadSmsPlugin";
    private static final String GET_TEXTS_ACTION = "GetTexts";

    // Defaults
    private static final Integer READ_ALL = -1;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "Inside ReadSms plugin.");

        JSONObject result = new JSONObject();

        if (args.length() == 0) {
            result.put("error", "No phone number provided.");
            callbackContext.success(result);
            return false;
        }

        String phoneNumber = args.getString(0);
        result.put("phone_number", phoneNumber);

        Integer numberOfTexts = READ_ALL; // Default
        if (args.length() >= 2) { // We want numberOfTexts to be the second one
            Log.d(TAG, "Setting maximum number of texts to retrieve.");
            try {
                numberOfTexts = Integer.valueOf(args.getString(1));
            } catch (NumberFormatException nfe) {
                String errorMessage =  String.format("Input provided (%s) is not a number",
                        args.getString(1));
                Log.e(TAG, errorMessage);
                result.put("error", errorMessage);
                return false;
            }
            if (numberOfTexts <= 0) {
                numberOfTexts = READ_ALL;
            }
        }

        if (action.equals("") || action.equals(GET_TEXTS_ACTION)) {
            Log.d(TAG, "Get texts from specified number.");
            JSONArray readResults = readTextsFrom(phoneNumber, numberOfTexts);
            Log.d(TAG, "read results: " + readResults.toString());
            result.put("texts", readResults);
            callbackContext.success(result);
        } else {
            Log.e(TAG, "Unknown action provided.");
            result.put("error", "Unknown action provided.");
            callbackContext.success(result);
            return false;
        }

        return true;
    }

    private JSONArray readTextsFrom(String numberToCheck, Integer numberOfTexts
            ) throws JSONException {
        ContentResolver contentResolver = cordova.getActivity().getContentResolver();
        String[] smsNo = new String[] { numberToCheck };

        String sortOrder = "date DESC"
                + ((numberOfTexts == READ_ALL) ? "" : " limit " + numberOfTexts);

        Cursor cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null,
                "address=?", smsNo, sortOrder);

        JSONArray results = new JSONArray();
        while (cursor.moveToNext()) {
            JSONObject current = new JSONObject();
            try {
                current.put("time_received", cursor.getString(4));
                current.put("message", cursor.getString(11));
            } catch (JSONException e) {
                e.printStackTrace();
                current.put("error", new String("Error reading text"));
            }
            results.put(current);
        }

        return results;
    }
}
