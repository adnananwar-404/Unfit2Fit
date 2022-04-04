package com.unfit2fit.unfit2fit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unfit2fit.unfit2fit.R;
import com.unfit2fit.unfit2fit.models.Goal;

import java.util.ArrayList;

public class GoalsContextAdapter extends BaseAdapter {

    private ArrayList<Goal> goals_listView_arrayList;
    private Context context;
    private LayoutInflater layoutInflater;

    public GoalsContextAdapter(ArrayList<Goal> goals_listView_arrayList, Context context) {
        this.goals_listView_arrayList = goals_listView_arrayList;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return goals_listView_arrayList.size();
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
        View v1 = layoutInflater.inflate(R.layout.goals_listview_item, viewGroup, false);
        TextView goal = v1.findViewById(R.id.goals_listView_item_goal);
        TextView current = v1.findViewById(R.id.goals_listView_item_currentValue);
        TextView target = v1.findViewById(R.id.goals_listView_item_targetValue);

        goal.setText(goals_listView_arrayList.get(i).getGoal());
        current.setText(goals_listView_arrayList.get(i).getCurrent());
        target.setText(goals_listView_arrayList.get(i).getTarget());
        return v1;
    }
}
