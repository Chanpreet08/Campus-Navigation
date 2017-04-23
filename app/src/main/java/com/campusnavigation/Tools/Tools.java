package com.campusnavigation.Tools;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by chanpreet on 3/1/17.
 */

public class Tools {
    public static ProgressDialog getProgressDialog(Context context)
    {
        ProgressDialog pd=new ProgressDialog(context);
        pd.setTitle("Loading");
        pd.setMessage("Please Wait....");
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return pd;

    }
}
