package com.example.bluehound;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.osmdroid.util.GeoPoint;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class Utilities {

    private static GeoPoint location;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static boolean replaceFlag = false;
    private static int replaceID = 0;
    private static ArrayList<BluetoothDevice> pairDevices;
    private static ArrayList<String> pairDeviceNames;
    private static ArrayList<String> connectDeviceNames= new ArrayList<>();
    private static ArrayList<String> connectedDeviceInfo = new ArrayList<>();
    static void insertFragment(AppCompatActivity activity, Fragment fragment, String tag) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container_view with this fragment
        transaction.replace(R.id.fragment_container_view, fragment, tag);

        //add the transaction to the back stack so the user can navigate back except for the HomeFragment
        if (!(fragment instanceof HomeFragment)) {
            transaction.addToBackStack(tag);
        }

        // Commit the transaction
        transaction.commit();
    }

    static void setUpToolbar(AppCompatActivity activity, String title) {
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar == null) {
            //create a toolbar that act as SupportActionBar
            Toolbar toolbar = new Toolbar(activity);
            activity.setSupportActionBar(toolbar);
        } else {
            activity.getSupportActionBar().setTitle(title);
            activity.getSupportActionBar().setLogo(R.drawable.ic_dog_running);
            activity.getSupportActionBar().setDisplayUseLogoEnabled(true);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * Utility to convert a drawable into a bitmap (to store the android icon as a bitmap)
     * @param drawable the drawable of the android icon
     * @return the bitmap of the drawable
     */
    public static Bitmap drawableToBitmap(Drawable drawable){
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Utility Method that get the Bitmap from the URI where the image is stored
     * @param activity activity when the method is executed
     * @param currentPhotoUri the URI for the image to get
     * @return the bitmap contained in the URI
     */
    public static Bitmap getImageBitmap(Activity activity, Uri currentPhotoUri){
        ContentResolver resolver = activity.getApplicationContext()
                .getContentResolver();
        try {
            InputStream stream = resolver.openInputStream(currentPhotoUri);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setLocation(GeoPoint geoPoint) {
        location = geoPoint;
    }
    public static GeoPoint getLocation() {
        return location;
    }

    public static void setReplaceID(int id) {
        replaceID = id;
    }

    public static int getReplaceID() {
        return replaceID;
    }

    public static void setReplaceFlag(boolean b) {
        replaceFlag = b;
    }


    public static boolean getReplaceFlag() {
        return replaceFlag;
    }

    public static void setPairedDevices(ArrayList<BluetoothDevice> bluetoothDevices) {
        pairDevices = bluetoothDevices;
    }
    public static void setPairedDeviceNames(ArrayList<String> bluetoothDeviceNames) {
        pairDeviceNames = bluetoothDeviceNames;
    }
    public static ArrayList<String> getPairDeviceNames() {
        return pairDeviceNames;
    }

    public static ArrayList<String> getConnectDeviceNames() {
        return connectDeviceNames;
    }

    public static void setConnectDeviceNames(String connectDeviceNames) {
        Utilities.connectDeviceNames.add(connectDeviceNames);
    }

    public static void resetConnectDeviceNames() {
        Utilities.connectDeviceNames.clear();
    }
    public static ArrayList<String> getConnectedDeviceInfo() {
        return connectedDeviceInfo;
    }
    public static void setConnectedDeviceInfo(String connectedDeviceInfo) {
        Utilities.connectedDeviceInfo.add(connectedDeviceInfo);
    }
}
