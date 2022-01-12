package com.example.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.model.Request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class Common {
    public static String ID;

    public static String convertCodeToStatus(int status) {
        if (status == -1)
            return "Đơn hàng đã bị hủy";
        else if (status == 0)
            return "Đơn hàng mới";
        else if (status == 1)
            return "Đang gửi thức ăn";
        else if (status == 2)
            return "Người đặt đã nhận đơn hàng";
        else
            return null;
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static final String USER_KEY = "User";
    public static final String PDW_KEY = "Password";

}
