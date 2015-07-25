package rohan.darshan.abhi.pothole;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class UpdateReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DARSHANROHAN", "Broadcast reciever called");
        /*ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isConnected = (activeNetInfo != null) && (activeNetInfo.isConnectedOrConnecting());
        Intent in = new Intent(context, service.class);

        if (isConnected) {
            Log.i("DARSHANROHAN", "connecte" + isConnected);
            context.startService(in);
        } else {
            context.stopService(in);
            Log.i("DARSHANROHAN", "not connecte" + isConnected);
        }*/

        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        Intent in = new Intent(context, service.class);

        if (wifi.isAvailable() || mobile.isAvailable()) {
            // Do something
            context.startService(in);

            Log.d("DARSHANROHAN", "Connected");

        } else {
            context.stopService(in);

            Log.d("DARSHANROHAN", "Not Connected");
        }
    }
}
