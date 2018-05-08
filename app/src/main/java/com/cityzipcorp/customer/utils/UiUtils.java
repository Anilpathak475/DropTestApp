package com.cityzipcorp.customer.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.callbacks.DialogCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Created by anilpathak on 05/09/17.
 */

public class UiUtils {
    private ProgressDialog progress;
    public Activity activity;
    AVLoadingIndicatorDialog avLoadingIndicatorDialog;

    public UiUtils(Activity activity) {
        this.activity = activity;

    }

    public void showDialog(String title, String message) {
        progress = ProgressDialog.show(activity, title,
                message, true);
    }

    public void showProgressDialog() {
        avLoadingIndicatorDialog = new AVLoadingIndicatorDialog(activity);
        avLoadingIndicatorDialog.setMessage(getString(R.string.loading));
        avLoadingIndicatorDialog.show();
       /* progress = ProgressDialog.show(activity, getString(R.string.loading),
                getString(R.string.wait), true);
        progress.setCanceledOnTouchOutside(true);*/
    }

    public void dismissDialog() {
       /* if(progress!=null) {
            if (progress.isShowing()) {
                progress.dismiss();
            }
        }*/

        if (avLoadingIndicatorDialog != null) {
            if (avLoadingIndicatorDialog.isShowing()) {
                avLoadingIndicatorDialog.dismiss();
            }
        }
    }

    public void shortToast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    public String getString(int id) {
        return activity.getString(id);
    }

    public Bitmap getBitmapBySize(int iconResID, int width, int height) {

        Drawable drawable = ContextCompat.getDrawable(activity, iconResID);
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

    public void getAlertDialogWithMessage(String message, final DialogCallback dialogCallback) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_conformation_pop_up);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        /*lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);*/
        Button btnOk = dialog.findViewById(R.id.btn_ok);
        TextView textView = dialog.findViewById(R.id.txt_message);
        textView.setText(message);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialogCallback.onYes();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }
}