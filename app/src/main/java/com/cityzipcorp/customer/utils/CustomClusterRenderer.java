package com.cityzipcorp.customer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.model.NodalStop;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by: Anton Shkurenko (tonyshkurenko)
 * Project: ClusterManagerDemo
 * Date: 6/7/16
 * Code style: SquareAndroid (https://github.com/square/java-code-styles)
 * Follow me: @tonyshkurenko
 */
public class CustomClusterRenderer extends DefaultClusterRenderer<NodalStop> {

    private final Context mContext;
    private String selectedNodalStopId = "";

    public CustomClusterRenderer(Context context, GoogleMap map, String selectedNodalStopId,
                                 ClusterManager<NodalStop> clusterManager) {
        super(context, map, clusterManager);
        if (selectedNodalStopId != null)
            this.selectedNodalStopId = selectedNodalStopId;
        mContext = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(NodalStop item,
                                               MarkerOptions markerOptions) {
        BitmapDescriptor markerDescriptor;
        if (selectedNodalStopId.equalsIgnoreCase(item.getId())) {
            markerDescriptor = bitmapDescriptorFromVector(mContext, R.drawable.nodal_pin_selected);
        } else {
            markerDescriptor = bitmapDescriptorFromVector(mContext, R.drawable.nodal_pin);

        }
        markerOptions.icon(markerDescriptor).snippet(item.getStop().getName());
    }

    private Bitmap getBitmapBySize(int iconResID, int width, int height) {

        Drawable drawable = ContextCompat.getDrawable(mContext, iconResID);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, width, height, false);

        return bitmapResized;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}