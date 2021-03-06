package com.example.android.sunshine.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;

/**
 * Created by matthiasko on 2/2/16.
 */
public class Handheld implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleClient;

    String mHighTemp;
    String mLowTemp;
    Bitmap mWeatherIcon;
    int mWeatherId;

    private Context mContext;

    public Handheld(Context context) {

        mContext = context;

        googleClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleClient.connect();

        /* where should we disconnect?

        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        */
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    public void updateHighLow(String highTemp, String lowTemp, int weatherId) {

        mHighTemp = highTemp;
        mLowTemp = lowTemp;
        mWeatherId = weatherId;

        //System.out.println("Handheld - highTemp = " + highTemp);
        //System.out.println("Handheld - lowTemp = " + lowTemp);
        System.out.println("Handheld - weatherId = " + weatherId);

        // use weatherId to send correct asset to wear app

        // convert the drawable icon to bitmap
        mWeatherIcon = BitmapFactory.decodeResource(mContext.getResources(),
                Utility.getIconResourceForWeatherCondition(weatherId));

        // create asset from the bitmap/drawable so we can send through the data layer to our
        // wear app

        Asset asset = createAssetFromBitmap(mWeatherIcon);

        // use setUrgent() to update image faster for google play service 8.3.0+
        PutDataMapRequest dataMap = PutDataMapRequest.create("/image").setUrgent();
        dataMap.getDataMap().putAsset("weatherImage", asset);

        // we need to use a timestamp, otherwise onDataChanged will not be called
        // we should test this
        dataMap.getDataMap().putLong("timestamp", System.currentTimeMillis());
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                .putDataItem(googleClient, request);
    }

    // Send a data object when the data layer connection is successful.

    @Override
    public void onConnected(Bundle connectionHint) {

        String WEARABLE_DATA_PATH = "/wearable_data";

        // Create a DataMap object and send it to the data layer
        DataMap dataMap = new DataMap();
        dataMap.putString("high", mHighTemp);
        dataMap.putString("low", mLowTemp);

        // we are just using the weatherId here to check that the correct image is showing up
        // on the wear app (using logs to see if the weatherId matches the image)

        dataMap.putInt("weatherId", mWeatherId);

        //Requires a new thread to avoid blocking the UI
        new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap).start();
    }

    // Placeholders for required connection callbacks
    @Override
    public void onConnectionSuspended(int cause) { }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }

    class SendToDataLayerThread extends Thread {
        String path;
        DataMap dataMap;

        // Constructor for sending data objects to the data layer
        SendToDataLayerThread(String p, DataMap data) {
            path = p;
            dataMap = data;
        }

        public void run() {
            // Construct a DataRequest and send over the data layer
            PutDataMapRequest putDMR = PutDataMapRequest.create(path);
            putDMR.getDataMap().putAll(dataMap);
            PutDataRequest request = putDMR.asPutDataRequest();
            DataApi.DataItemResult result = Wearable.DataApi.putDataItem(googleClient, request).await();
            if (result.getStatus().isSuccess()) {
                Log.v("myTag", "DataMap: " + dataMap + " sent successfully to data layer ");
            }
            else {
                // Log an error
                Log.v("myTag", "ERROR: failed to send DataMap to data layer");
            }
        }
    }
}
