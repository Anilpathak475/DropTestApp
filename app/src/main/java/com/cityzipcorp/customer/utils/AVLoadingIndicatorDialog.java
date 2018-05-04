package com.cityzipcorp.customer.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cityzipcorp.customer.R;


/**
 * Created by Jack Wang on 2016/5/6.
 */
public class AVLoadingIndicatorDialog extends AlertDialog {

    private TextView mMessageView;

    public AVLoadingIndicatorDialog(Context context) {
        super(context);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.progress_avld, null);
        mMessageView = view.findViewById(R.id.message);
        setView(view);
    }

    @Override
    public void show() {
        super.show();
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.getWindow().setDimAmount(0.1f);
        this.setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    @Override
    public void setMessage(CharSequence message) {
        mMessageView.setText(message);
    }
}
