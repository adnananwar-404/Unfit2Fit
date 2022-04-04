package com.unfit2fit.unfit2fit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unfit2fit.unfit2fit.R;
import com.unfit2fit.unfit2fit.models.Goal;
import com.unfit2fit.unfit2fit.models.Measurement;

import java.util.ArrayList;

public class MeasurementAdapter extends BaseAdapter {

    private ArrayList<Measurement> measurements_listView_arrayList;
    private Context context;
    private LayoutInflater layoutInflater;

    public MeasurementAdapter(ArrayList<Measurement> measurements_listView_arrayList, Context context) {
        this.measurements_listView_arrayList = measurements_listView_arrayList;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return measurements_listView_arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v1 = layoutInflater.inflate(R.layout.measurements_listview_item, viewGroup, false);
        TextView measurement = v1.findViewById(R.id.measurements_listView_item_measurement);
        TextView metric = v1.findViewById(R.id.measurements_listView_item_metric);
        TextView value = v1.findViewById(R.id.measurements_listView_item_currentValue);

        measurement.setText(measurements_listView_arrayList.get(i).getMeasurement());
        metric.setText(measurements_listView_arrayList.get(i).getMetric());
        value.setText(measurements_listView_arrayList.get(i).getValue());
        return v1;
    }
}
