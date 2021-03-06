package com.example.android.sunshine.app;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by matthiasko on 2/2/16.
 */
public class ListenerService extends WearableListenerService {

    /*
        The ListenerService class will respond to onDataChanged when data is sent from mobile app
        Then we can send that data to our WeatherWatchFaceService class

    */

    private static final String WEARABLE_DATA_PATH = "/wearable_data";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        DataMap dataMap;
        for (DataEvent event : dataEvents) {

            // Check the data type
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // Check the data path
                String path = event.getDataItem().getUri().getPath();

                if (path.equals(WEARABLE_DATA_PATH)) {

                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    Log.v("myTag", "DataMap received on watch: " + dataMap);

                    String highTemp = dataMap.getString("high");
                    String lowTemp = dataMap.getString("low");

                    int weatherId = dataMap.getInt("weatherId");

                    System.out.println("ListenerService - weatherId = " + weatherId);

                    String highLowTemp = highTemp + "/" + lowTemp;

                    //System.out.println("ListenerService - highTemp = " + highTemp);

                    // get high and low temp and put in message intent so we can update ui
                    // from WeatherWatchFaceService
                    Intent messageIntent = new Intent();
                    messageIntent.setAction(Intent.ACTION_SEND);
                    messageIntent.putExtra("message", highLowTemp);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
                }
            }
        }
    }
}
