package com.cityzipcorp.customer.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import com.cityzipcorp.customer.callbacks.OptOutDialogCallback;

/**
 * Created by anilpathak on 05/09/17.
 */

public class UiUtils {
    public Activity activity;
    private AVLoadingIndicatorDialog avLoadingIndicatorDialog;
    private ProgressDialog progress;
    private Dialog dialog;

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
    }

    public void dismissDialog() {
        if (null != avLoadingIndicatorDialog) {
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
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    public void conformationDialog(String message, final DialogCallback dialogCallback) {
        if (activity == null) {
            return;
        }
        try {
            if (dialog == null) {
                dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_conformation_pop_up);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                Button btnOk = dialog.findViewById(R.id.btn_ok);
                TextView textView = dialog.findViewById(R.id.txt_message);
                textView.setText(message);
                Button btnCancel = dialog.findViewById(R.id.btn_cancel);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        dialogCallback.onYes();
                        dialog = null;
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        dialog = null;
                    }
                });
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void optOutConformationDialog(String message, final OptOutDialogCallback dialogCallback) {
        if (activity == null) {
            return;
        }
        try {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_conformation_pop_up);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
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
                    dialogCallback.onNo();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void notifyDialog(String message, final DialogCallback dialogCallback) throws WindowManager.BadTokenException {
        if (activity == null) {
            return;
        }
        try {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_notify_pop_up);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            Button btnOk = dialog.findViewById(R.id.btn_ok);
            TextView textView = dialog.findViewById(R.id.txt_message);
            textView.setText(message);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    dialogCallback.onYes();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void noInternetDialog() {
        if (activity == null) {
            return;
        }
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("No Internet Connection");
            builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit");

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
