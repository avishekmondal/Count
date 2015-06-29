package com.utility;

import android.app.Application;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(formKey = "CountClass",
        formUri =  Constant.baseUrl + "appmail"
)

/**
 * Created by Avishek on 5/29/2015.
 */
public class CountClass extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}
