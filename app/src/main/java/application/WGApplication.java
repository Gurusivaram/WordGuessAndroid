package application;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;

public class WGApplication extends Application {
    private static WGApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Boolean isNetworkConnected() {
        if (instance != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) instance.getSystemService(CONNECTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network activeNetwork = connectivityManager.getActiveNetwork();
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
            return connectivityManager.getActiveNetworkInfo().isConnected();
        }
        return true;
    }
}
