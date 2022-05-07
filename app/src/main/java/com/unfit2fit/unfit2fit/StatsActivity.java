package com.unfit2fit.unfit2fit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.common.primitives.Ints;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.unfit2fit.unfit2fit.models.Goal;
import com.unfit2fit.unfit2fit.models.Measurement;
import com.unfit2fit.unfit2fit.models.Stats;
import com.unfit2fit.unfit2fit.wrappers.GoalsListWrapper;
import com.unfit2fit.unfit2fit.wrappers.MeasurementsListWrapper;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatsActivity extends AppCompatActivity {

    private static final String TAG = "StatsActivity ";

    BottomNavigationView bottomNavigationView;

    private FirebaseAuth myAuth;
    private FirebaseFirestore myDatabase;

    boolean isEmpty = false;

    Spinner spinner;
    ArrayAdapter spinnerAdapter;

    private Stats weightStats, waistStats, stepsStats, chestStats, calorieIntakeStats;
    private ArrayList<Number> tempYValues;
    private String goal;

    private MeasurementsListWrapper measurementsListWrapper;
    private GoalsListWrapper goalsListWrapper;

    Button displayGraph;

    List<String> spinnerItems;

    private String userID, chosenStat;
    XYPlot plot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.stats);

        displayGraph = findViewById(R.id.stats_displayGraph);

        myAuth = FirebaseAuth.getInstance();
        myDatabase = FirebaseFirestore.getInstance();

        userID = myAuth.getCurrentUser().getUid();

        spinnerItems = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.goals_DL)));
        spinner = (Spinner) findViewById(R.id.stats_Spinner);

        spinnerAdapter = new ArrayAdapter(StatsActivity.this, R.layout.support_simple_spinner_dropdown_item, spinnerItems);
        spinner.setAdapter(spinnerAdapter);


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.goals:
                        startActivity(new Intent(StatsActivity.this, GoalsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.measurements:
                        startActivity(new Intent(StatsActivity.this, MeasurementActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(StatsActivity.this, HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.stats:
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(StatsActivity.this, ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        loadData();

        displayGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chosenStat = spinner.getSelectedItem().toString();
                processData();
                if(isEmpty)
                {
                    Toast.makeText(StatsActivity.this, "Not Enough Values To Graph " + chosenStat, Toast.LENGTH_SHORT).show();
                }
                else{
                    setGraph();
                }
            }
        });


        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.stats_plot);
//
//        // create a couple arrays of y-values to plot:
//        final Number[] domainLabels = {1, 2, 3, 6, 7, 8, 9, 10, 13, 14};
//        Number[] series2Numbers = {5, 2, 10, 5, 20, 10, 40, 20, 80, 40};
//
//        // turn the above arrays into XYSeries':
//        // (Y_VALS_ONLY means use the element index as the x value)
//        XYSeries series2 = new SimpleXYSeries(
//                Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");
//
//
//        // create formatters to use for drawing a series using LineAndPointRenderer
//        LineAndPointFormatter series2Format = new LineAndPointFormatter(Color.BLUE, Color.YELLOW, Color.GRAY, null);
//
//        // add an "dash" effect to the series2 line:
//        series2Format.getLinePaint().setPathEffect(new DashPathEffect(new float[] {
//
//                // always use DP when specifying pixel sizes, to keep things consistent across devices:
//                PixelUtils.dpToPix(20),
//                PixelUtils.dpToPix(15)}, 0));
//
//        // add some smoothing to the lines:
//        series2Format.setInterpolationParams(
//                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
//
//        // add series to the plot:
//        plot.addSeries(series2, series2Format);
//
//        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
//            @Override
//            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
//                int i = Math.round(((Number) obj).floatValue());
//                return toAppendTo.append(domainLabels[i]);
//            }
//            @Override
//            public Object parseObject(String source, ParsePosition pos) {
//                return null;
//            }
//        });


    }





    private void loadData()
    {

        DocumentReference df_measurements = myDatabase.collection("measurements").document(userID);

        df_measurements.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    Log.d(TAG, "Listen Failed. ", error);
                    return;
                }

                if(value != null && value.exists())
                {
                    MeasurementsListWrapper tempWrapper =  new MeasurementsListWrapper();
                    tempWrapper  = value.toObject(MeasurementsListWrapper.class);

                    measurementsListWrapper = tempWrapper;

                    loadDataIntoObjects();

                }
                else
                {
                    Toast.makeText(StatsActivity.this, "No Values", Toast.LENGTH_SHORT).show();

                }
            }
        });


        DocumentReference df_goals = myDatabase.collection("goals").document(userID);

        df_goals.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    Log.d(TAG, "Listen Failed. ", error);
                    return;
                }

                if(value != null && value.exists())
                {
                    GoalsListWrapper tempWrapper =  new GoalsListWrapper();
                    tempWrapper  = value.toObject(GoalsListWrapper.class);

                    goalsListWrapper = tempWrapper;

                    loadGoalsData();
                }
                else
                {
                    Toast.makeText(StatsActivity.this, "No Values", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }





    private void loadDataIntoObjects()
    {
        weightStats = new Stats("Weight", measurementsListWrapper.getWeightList());
        waistStats = new Stats("Waist", measurementsListWrapper.getWaistList());
        chestStats = new Stats("Chest", measurementsListWrapper.getChestList());
        stepsStats = new Stats("Steps", measurementsListWrapper.getStepsList());
        calorieIntakeStats = new Stats("Calorie Intake", measurementsListWrapper.getCalorieIntakeList());

    }

    private void processData()
    {
        tempYValues = new ArrayList<>();

        if(chosenStat.equals("Weight"))
        {
            if(!weightStats.getMeasurements().isEmpty() && weightStats.getMeasurements().size() >= 2){
                for(int i=0; i < weightStats.getMeasurements().size(); i++)
                {
                    tempYValues.add(Integer.parseInt(weightStats.getMeasurements().get(i).getValue()));
                }
                goal = weightStats.getGoal();
                isEmpty = false;
            }
            else{
                isEmpty = true;
            }

        }
        else if(chosenStat.equals("Steps"))
        {
            if(!stepsStats.getMeasurements().isEmpty() && stepsStats.getMeasurements().size() >= 2){
                for(int i=0; i < stepsStats.getMeasurements().size(); i++)
                {
                    tempYValues.add(Integer.parseInt(stepsStats.getMeasurements().get(i).getValue()));
                }
                goal = stepsStats.getGoal();
                isEmpty = false;
            }
            else{
                isEmpty = true;
            }
        }
        else if(chosenStat.equals("Waist"))
        {
            if(!waistStats.getMeasurements().isEmpty() && waistStats.getMeasurements().size() >= 2){
                for(int i=0; i < waistStats.getMeasurements().size(); i++)
                {
                    tempYValues.add(Integer.parseInt(waistStats.getMeasurements().get(i).getValue()));
                }
                goal = waistStats.getGoal();
                isEmpty = false;
            }
            else{
                isEmpty = true;
            }
        }
        else if(chosenStat.equals("Chest"))
        {
            if(!chestStats.getMeasurements().isEmpty() && chestStats.getMeasurements().size() >= 2){
                for(int i=0; i < chestStats.getMeasurements().size(); i++)
                {
                    tempYValues.add(Integer.parseInt(chestStats.getMeasurements().get(i).getValue()));
                }
                goal = chestStats.getGoal();
                isEmpty = false;
            }
            else{
                isEmpty = true;
            }
        }
        else if(chosenStat.equals("Calorie Intake"))
        {
            if(!calorieIntakeStats.getMeasurements().isEmpty() && calorieIntakeStats.getMeasurements().size() >= 2){
                for(int i=0; i < calorieIntakeStats.getMeasurements().size(); i++)
                {
                    tempYValues.add(Integer.parseInt(calorieIntakeStats.getMeasurements().get(i).getValue()));
                }
                goal = calorieIntakeStats.getGoal();
                isEmpty = false;
            }
            else{
                isEmpty = true;
            }
        }
    }


    private void setGraph()
    {

        plot.clear();


        Number[] yValues = new Number[tempYValues.size()];
        Number[] yGoalValues = new Number[tempYValues.size()];

        for(int i=0; i < tempYValues.size(); i++)
        {
            yValues[i] = tempYValues.get(i);

        }

        XYSeries currentValues = new SimpleXYSeries(Arrays.asList(yValues), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, chosenStat);

        //LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.BLUE, Color.TRANSPARENT, null);
        LineAndPointFormatter currentValuesFormat = new LineAndPointFormatter(Color.RED, Color.BLUE, null, null);


        //change the line width
        Paint paint = currentValuesFormat.getLinePaint();
        paint.setStrokeWidth(10);
        currentValuesFormat.setLinePaint(paint);


        currentValuesFormat.getVertexPaint().setStrokeWidth(PixelUtils.dpToPix(10));

        if(goal != null)
        {
            for(int i=0; i < tempYValues.size(); i++)
            {
                yGoalValues[i] = Integer.parseInt(goal);
            }

            XYSeries goalValues = new SimpleXYSeries(Arrays.asList(yGoalValues), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Goal");
            LineAndPointFormatter goalValuesFormat = new LineAndPointFormatter(Color.GREEN, Color.GREEN, null, null);

            Paint paint2 = goalValuesFormat.getLinePaint();
            paint2.setStrokeWidth(10);
            goalValuesFormat.setLinePaint(paint2);

            goalValuesFormat.getVertexPaint().setStrokeWidth(PixelUtils.dpToPix(10));

            // add an "dash" effect to the series2 line:
            goalValuesFormat.getLinePaint().setPathEffect(new DashPathEffect(new float[] {
                    //always use DP when specifying pixel sizes, to keep things consistent across devices:
                    PixelUtils.dpToPix(20),
                    PixelUtils.dpToPix(15)}, 0));

            plot.addSeries(goalValues, goalValuesFormat);
        }

        plot.addSeries(currentValues, currentValuesFormat);
        plot.redraw();

    }

    private void loadGoalsData()
    {
        if(!goalsListWrapper.getGoalsList().isEmpty())
        {
            for(int i=0; i < goalsListWrapper.getGoalsList().size(); i++)
            {
                if(goalsListWrapper.getGoalsList().get(i).getGoal().equals("Weight"))
                {
                    weightStats.setGoal(goalsListWrapper.getGoalsList().get(i).getTarget());
                }
                else if(goalsListWrapper.getGoalsList().get(i).getGoal().equals("Calorie Intake"))
                {
                    calorieIntakeStats.setGoal(goalsListWrapper.getGoalsList().get(i).getTarget());
                }
                else if(goalsListWrapper.getGoalsList().get(i).getGoal().equals("Steps"))
                {
                    stepsStats.setGoal(goalsListWrapper.getGoalsList().get(i).getTarget());
                }
                else if(goalsListWrapper.getGoalsList().get(i).getGoal().equals("Waist"))
                {
                    waistStats.setGoal(goalsListWrapper.getGoalsList().get(i).getTarget());
                }
                else if(goalsListWrapper.getGoalsList().get(i).getGoal().equals("Chest"))
                {
                    chestStats.setGoal(goalsListWrapper.getGoalsList().get(i).getTarget());
                }
            }
        }
        else{
            Toast.makeText(StatsActivity.this, "No Goals", Toast.LENGTH_SHORT).show();
        }
    }


}