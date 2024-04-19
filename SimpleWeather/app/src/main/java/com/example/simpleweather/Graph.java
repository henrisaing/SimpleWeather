package com.example.simpleweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Graph#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Graph extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SharedPreferences settings;
    GraphView graphView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Graph() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Graph.
     */
    // TODO: Rename and change types and number of parameters
    public static Graph newInstance(String param1, String param2) {
        Graph fragment = new Graph();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graph, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            settings = PreferenceManager.getDefaultSharedPreferences(requireActivity());
            JSONObject day;

            switch(settings.getString("graph", "error")){

                case "day1":
                    day = new JSONObject(settings.getString("day1", "error"));
                    break;

                case "day2":
                    day = new JSONObject(settings.getString("day2", "error"));
                    break;
                case "day0":

                default:
                    day = new JSONObject(settings.getString("day0", "error"));
            }
//creates main lines for temp & pop
            DataPoint[] tempPoints = new DataPoint[25];
            DataPoint[] popPoints = new DataPoint[25];
            double[] hours = new double[24];
            double[] temps = new double[24];
            double[] pops = new double[24];
            for(int i =0; i < 24; i++){
                hours[i] = Double.parseDouble(day.getJSONArray("hour").getJSONObject(i).get("time").toString().split(" ")[1].split(":")[0]);
                temps[i] = Double.parseDouble(day.getJSONArray("hour").getJSONObject(i).get("temp_c").toString());
                double rain = Double.parseDouble(day.getJSONArray("hour").getJSONObject(i).get("chance_of_rain").toString());
                double snow = Double.parseDouble(day.getJSONArray("hour").getJSONObject(i).get("chance_of_snow").toString());
                if(rain > snow){
                    pops[i] = rain;
                }else{
                    pops[i] = snow;
                }
                double popD = Double.parseDouble(String.valueOf(pops[i]));
                double popAdjusted = popD / 100 * 50;

                tempPoints[i] = new DataPoint(hours[i], temps[i]);
                popPoints[i] = new DataPoint(hours[i], popAdjusted);
            }
            tempPoints[24] = new DataPoint(24.0, temps[23]);
            popPoints[24] = new DataPoint(24.0, popPoints[23].getY());

            graphView = requireView().findViewById(R.id.idGraphView);
            LineGraphSeries<DataPoint> seriesTemps = new LineGraphSeries<DataPoint>(tempPoints);
            LineGraphSeries<DataPoint> seriesPops = new LineGraphSeries<DataPoint>(popPoints);

            //sunrise line block
            double sunriseHour = Double.parseDouble(day.getJSONObject("astro").get("sunrise").toString().split(":")[0]);
            double sunriseMinute = (Double.parseDouble(day.getJSONObject("astro").get("sunrise").toString().split(" ")[0].split(":")[1])) / 60;
            double sunriseTime;
            if(day.getJSONObject("astro").get("sunrise").toString().split(" ")[1].equals("PM")){
                sunriseHour += 12;
            }
            sunriseTime = sunriseHour + sunriseMinute;

            LineGraphSeries<DataPoint> seriesRise = new LineGraphSeries<DataPoint>(new DataPoint[]{
                    new DataPoint(sunriseTime, 50),
                    new DataPoint(sunriseTime, -50)
            });

            //sunset line block
            double sunsetHour = Double.parseDouble(day.getJSONObject("astro").get("sunset").toString().split(":")[0]);
            double sunsetMinute = (Double.parseDouble(day.getJSONObject("astro").get("sunset").toString().split(" ")[0].split(":")[1])) / 60;
            double sunsetTime;
            if(day.getJSONObject("astro").get("sunset").toString().split(" ")[1].equals("PM")){
                sunsetHour += (double) 12;
            }
            sunsetTime = sunsetHour + sunsetMinute;
            LineGraphSeries<DataPoint> seriesSet = new LineGraphSeries<DataPoint>(new DataPoint[]{
                    new DataPoint( sunsetTime, 50),
                    new DataPoint( sunsetTime, -50)
            });

//            GREEN timeNOW line on graph
            //green line only appears if day.date == current.date
            Date currentTime = Calendar.getInstance().getTime();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String date = df.format(currentTime);
            if(date.equals(day.get("date").toString())){
                double nowHour = Double.parseDouble(currentTime.toString().split(" ")[3].split(":")[0]);
                double nowMinute = (Double.parseDouble(currentTime.toString().split(" ")[3].split(":")[1])) / 60;
                LineGraphSeries<DataPoint> seriesNow = new LineGraphSeries<DataPoint>(new DataPoint[]{
                        new DataPoint( (nowHour + nowMinute), 50),
                        new DataPoint( (nowHour + nowMinute), -50)
                });
                seriesNow.setColor(Color.GREEN);
                seriesNow.setThickness(3);
                graphView.addSeries(seriesNow);
            }



            seriesTemps.setColor(Color.WHITE);
            seriesTemps.setDrawDataPoints(false);
            seriesTemps.setThickness(3);
            seriesTemps.setTitle("Temp");

            seriesPops.setColor(ContextCompat.getColor(requireContext(), R.color.light_blue_200));
            seriesPops.setDrawDataPoints(false);
            seriesPops.setThickness(3);
            seriesPops.setTitle("PoP");

            seriesRise.setColor(Color.WHITE);
            seriesRise.setThickness(3);
            seriesSet.setColor(Color.WHITE);
            seriesSet.setThickness(3);

//        series.setDrawBackground(true);
//        series.setBackgroundColor(Color.argb(150, 0, 171, 188));

            graphView.getGridLabelRenderer().setGridColor(Color.LTGRAY);
            graphView.getGridLabelRenderer().setHorizontalLabelsColor(Color.LTGRAY);
            graphView.getGridLabelRenderer().setHorizontalLabelsAngle(45);
            graphView.getGridLabelRenderer().setVerticalLabelsColor(Color.LTGRAY);
            graphView.getGridLabelRenderer().setNumHorizontalLabels(6);
            graphView.getGridLabelRenderer().setNumVerticalLabels(12);
            graphView.getGridLabelRenderer().reloadStyles();

            graphView.setTitle(day.get("date").toString());
            try{
                String dateString = day.get("date").toString();
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date dateTitle = (Date)formatter.parse(dateString);
                SimpleDateFormat newFormat = new SimpleDateFormat("EEE MMM dd");
                String title = newFormat.format(dateTitle);
                graphView.setTitle(title);
            }catch (Exception e){

            }



//            graphView.setTitle(day.get("date").toString());
            graphView.setTitleColor(Color.WHITE);
            graphView.setTitleTextSize(45);

            graphView.getViewport().setMinY(-50);
            graphView.getViewport().setMaxY(50);
            graphView.getViewport().setMinX(0);
            graphView.getViewport().setMaxX(24);
            graphView.getViewport().setYAxisBoundsManual(true);
            graphView.getViewport().setXAxisBoundsManual(true);

//            graphView.getLegendRenderer().setVisible(true);

            graphView.addSeries(seriesRise);
            graphView.addSeries(seriesSet);

            graphView.addSeries(seriesTemps);
            graphView.addSeries(seriesPops);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}