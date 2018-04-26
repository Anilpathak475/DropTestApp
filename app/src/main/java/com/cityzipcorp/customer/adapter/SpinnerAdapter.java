package com.cityzipcorp.customer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.model.Group;
import com.cityzipcorp.customer.model.Shift;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anilpathak on 02/01/18.
 */

public class SpinnerAdapter<T> extends ArrayAdapter<T> {

    private List<T> objectList = new ArrayList<>();
    private int viewId;
    private Context context;
    private LayoutInflater layoutInflater;

    public SpinnerAdapter(@NonNull Context context, int resource, List<T> objects) {
        super(context, resource, objects);
        objectList = objects;
        viewId = resource;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return objectList.size();
    }

    @Nullable
    @Override
    public T getItem(int position) {
        String spinnerText = "";
        Object object = objectList.get(position);
        if (object instanceof Group) {
            Group group = (Group) object;
            spinnerText = group.getName();
        } else {
            Shift shift = (Shift) object;
            spinnerText = shift.getName();
        }
        return (T) spinnerText;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(viewId, null);
        } else {
            convertView.setTag(position);
        }

        TextView textView = convertView.findViewById(R.id.spinner_text);
        Object object = objectList.get(position);
        if (object instanceof Group) {
            Group group = (Group) object;
            textView.setText(group.getName());
        } else {
            Shift shift = (Shift) object;
            textView.setText(shift.getName());
        }

        return convertView;
    }

}
