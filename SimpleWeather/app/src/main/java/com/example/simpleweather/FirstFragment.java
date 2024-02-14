package com.example.simpleweather;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.simpleweather.databinding.FragmentFirstBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private SharedPreferences settings;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
//        settings = requireActivity().getSharedPreferences(requireActivity().getLocalClassName(), Context.MODE_PRIVATE);
        settings = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //get location from file
//        if(settings.getString("location", null) != null){
            EditText editText = requireView().findViewById(R.id.editTextTextPersonName);
            updateTextViews();
            getWeather();
//        }

        binding.weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = requireView().findViewById(R.id.editTextTextPersonName);

                saveSetting("location", editText.getText().toString());
//                clearKeyboard();
                    //causes crash on graph -> back -> get weather
                getWeather();
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_GO){
                    EditText editText = requireView().findViewById(R.id.editTextTextPersonName);
                    saveSetting("location", editText.getText().toString());
                    clearKeyboard();
                    getWeather();
                }

                return true;
            }
        });

        binding.buttonDay0.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                saveSetting("graph", "day0");
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_Graph);
            }
        });

        binding.buttonDay1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                saveSetting("graph", "day1");
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_Graph);
            }
        });

        binding.buttonDay2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                saveSetting("graph", "day2");
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_Graph);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void getWeather(){
        EditText editText = requireView().findViewById(R.id.editTextTextPersonName);
// ...

// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String mainUrl = "http://api.weatherapi.com/v1/forecast.json?key=6b28efa198e647efa92161658222706%20";
        String location = editText.getText().toString().replace(" ", "-");
        String url = mainUrl + "&q=" + location + "&days=3&aqi=no&alerts=no";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONObject location = jsonObject.getJSONObject("location");
                            JSONObject current = jsonObject.getJSONObject("current");
                            JSONArray forecast = jsonObject.getJSONObject("forecast").getJSONArray("forecastday");
                            JSONObject day0 = new JSONObject(forecast.get(0).toString());
                            JSONObject day1 = new JSONObject(forecast.get(1).toString());
                            JSONObject day2 = new JSONObject(forecast.get(2).toString());

                            String day0Sunrise = day0.getJSONObject("astro").get("sunrise").toString();
                            String day1Sunrise = day1.getJSONObject("astro").get("sunrise").toString();
                            String day2Sunrise = day2.getJSONObject("astro").get("sunrise").toString();

                            String day0Sunset = day0.getJSONObject("astro").get("sunset").toString();
                            String day1Sunset = day1.getJSONObject("astro").get("sunset").toString();
                            String day2Sunset = day2.getJSONObject("astro").get("sunset").toString();

//                            building day 0 string
                            String city = location.get("name").toString();
                            String region = location.get("region").toString();
                            String country = location.get("country").toString();
                            String day0Date = day0.get("date").toString();
                            String day0TempNow = current.get("temp_c").toString().split("\\.")[0] + "c";
                            String day0Feels = current.get("feelslike_c").toString().split("\\.")[0] + "c";
                            String day0High = day0.getJSONObject("day").get("maxtemp_c").toString().split("\\.")[0] + "c";
                            String day0Low = day0.getJSONObject("day").get("mintemp_c").toString().split("\\.")[0] + "c";
                            String day0Avg = day0.getJSONObject("day").get("avgtemp_c").toString().split("\\.")[0] + "c";
                            String day0Condition = day0.getJSONObject("day").getJSONObject("condition").get("text").toString().replaceAll("possible", "");
                            String day0mm = day0.getJSONObject("day").get("totalprecip_mm") + "mm";
                            String day0Humidity = current.get("humidity") + "%";
                            String day0Pop;
                            if (day0.getJSONObject("day").getInt("daily_chance_of_rain") > day0.getJSONObject("day").getInt("daily_chance_of_snow")) {
                                day0Pop = day0.getJSONObject("day").get("daily_chance_of_rain") + "%";
                            } else {
                                day0Pop = day0.getJSONObject("day").get("daily_chance_of_snow") + "%";
                            }

                            String day0Wind = "Wind: " + current.get("wind_kph") + "kph @" + current.get("wind_dir") + " | Max: " + day0.getJSONObject("day").get("maxwind_kph") + "kph\n";


//                            building day 1 string
                            String day1Date = day1.get("date").toString();
                            String day1Avg = day1.getJSONObject("day").get("avgtemp_c").toString().split("\\.")[0] + "c";
                            String day1High = day1.getJSONObject("day").get("maxtemp_c").toString().split("\\.")[0] + "c";
                            String day1Low = day1.getJSONObject("day").get("mintemp_c").toString().split("\\.")[0] + "c";
                            String day1Condition = day1.getJSONObject("day").getJSONObject("condition").get("text").toString().replaceAll("possible", "");
                            String day1mm = day1.getJSONObject("day").get("totalprecip_mm") + "mm";
                            String day1Humidity = day1.getJSONObject("day").get("avghumidity") + "%";
                            String day1Pop;
                            if (day1.getJSONObject("day").getInt("daily_chance_of_rain") > day1.getJSONObject("day").getInt("daily_chance_of_snow")) {
                                day1Pop = day1.getJSONObject("day").get("daily_chance_of_rain") + "%";
                            } else {
                                day1Pop = day1.getJSONObject("day").get("daily_chance_of_snow") + "%";
                            }

                            String day1Wind = "Wind: " + day1.getJSONObject("day").get("maxwind_kph") + "kph | Humidity: " + day1.getJSONObject("day").get("avghumidity") + "%\n";


//                            building day 2 string
                            String day2Date = day2.get("date").toString();
                            String day2Avg = day2.getJSONObject("day").get("avgtemp_c").toString().split("\\.")[0] + "c";
                            String day2High = day2.getJSONObject("day").get("maxtemp_c").toString().split("\\.")[0] + "c";
                            String day2Low = day2.getJSONObject("day").get("mintemp_c").toString().split("\\.")[0] + "c";
                            String day2Condition = day2.getJSONObject("day").getJSONObject("condition").get("text").toString().replaceAll("possible", "");
                            String day2mm = day2.getJSONObject("day").get("totalprecip_mm") + "mm";
                            String day2Humidity = day2.getJSONObject("day").get("avghumidity") + "%";
                            String day2Pop;
                            if (day2.getJSONObject("day").getInt("daily_chance_of_rain") > day2.getJSONObject("day").getInt("daily_chance_of_snow")) {
                                day2Pop = day2.getJSONObject("day").get("daily_chance_of_rain") + "%";
                            } else {
                                day2Pop = day2.getJSONObject("day").get("daily_chance_of_snow") + "%";
                            }
                            String day2Wind = "Wind: " + day2.getJSONObject("day").get("maxwind_kph") + "kph | Humidity: " + day2.getJSONObject("day").get("avghumidity") + "%\n";


//                            textViewDay0.setText( day0Location + day0Date + day0Weather + day0TempNow + day0TempStats + day0Wind);
//                            textViewDay1.setText( day1Date + day1Weather + day1TempStats + day1Wind);
//                            textViewDay2.setText( day2Date + day2Weather + day2TempStats + day2Wind);

                            saveSetting("day0", day0.toString());
                            saveSetting("day1", day1.toString());
                            saveSetting("day2", day2.toString());

                            saveSetting("city", city);
                            saveSetting("region", region);
                            saveSetting("country", country);

                            saveSetting("day0Sunrise", day0Sunrise);
                            saveSetting("day1Sunrise", day1Sunrise);
                            saveSetting("day2Sunrise", day2Sunrise);
                            saveSetting("day0Sunset", day0Sunset);
                            saveSetting("day1Sunset", day1Sunset);
                            saveSetting("day2Sunset", day2Sunset);

                            saveSetting("day0Date", day0Date);
                            saveSetting("day0Condition", day0Condition);
                            saveSetting("day0TempNow", day0TempNow);
                            saveSetting("day0Feels", day0Feels);
                            saveSetting("day0High", day0High);
                            saveSetting("day0Low", day0Low);
                            saveSetting("day0Avg", day0Avg);
                            saveSetting("day0Pop", day0Pop);
                            saveSetting("day0mm", day0mm);
                            saveSetting("day0Humidity", day0Humidity);

                            saveSetting("day1Date", day1Date);
                            saveSetting("day1Avg", day1Avg);
                            saveSetting("day1High", day1High);
                            saveSetting("day1Low", day1Low);
                            saveSetting("day1Condition", day1Condition);
                            saveSetting("day1Pop", day1Pop);
                            saveSetting("day1mm", day1mm);
                            saveSetting("day1Humidity", day1Humidity);

                            saveSetting("day2Date", day2Date);
                            saveSetting("day2Avg", day2Avg);
                            saveSetting("day2High", day2High);
                            saveSetting("day2Low", day2Low);
                            saveSetting("day2Condition", day2Condition);
                            saveSetting("day2Pop", day2Pop);
                            saveSetting("day2mm", day2mm);
                            saveSetting("day2Humidity", day2Humidity);

                            //DECOUPLE updateTextViews from updateweather
                            updateTextViews();
                            updateWidgets();

                        } catch (JSONException e) {
//                            textViewDay0.setText("catch: " + e.getMessage());
                            Toast.makeText(requireContext(), "error", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

//                                textView.setText("Response is: " + response);
                    }
                }, error -> {
            Toast errorToast = Toast.makeText(getContext(), "Error, something went wrong.", Toast.LENGTH_LONG);
            errorToast.show();

        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    public void clearKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void saveSetting(String setting, String value){
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(setting, value);
        editor.apply();
    }

    public void updateWidgets(){
        Intent intent = new Intent(requireContext(), SimpleWeatherWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getActivity()).getAppWidgetIds(new ComponentName(getActivity(), SimpleWeatherWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        requireContext().sendBroadcast(intent);
    }

    public void updateTextViews(){
        EditText editText = requireView().findViewById(R.id.editTextTextPersonName);
        TextView textViewCity = requireView().findViewById(R.id.textview_name);
        TextView textViewRegion = requireView().findViewById(R.id.textview_region);
        TextView textViewCountry = requireView().findViewById(R.id.textview_country);
        TextView textViewDate0 = requireView().findViewById(R.id.textview_date_0);
        TextView textViewNow = requireView().findViewById(R.id.textview_temp_now_0);
        TextView textViewFeels = requireView().findViewById(R.id.textview_feels);
        TextView textViewHigh0 = requireView().findViewById(R.id.textview_high_0);
        TextView textViewAvg0 = requireView().findViewById(R.id.textview_avg_0);
        TextView textViewLow0 = requireView().findViewById(R.id.textview_low_0);
        TextView textViewPop0 = requireView().findViewById(R.id.textview_pop_0);
        TextView textViewCondition0 = requireView().findViewById(R.id.textview_condition_0);
        TextView textViewMm0 = requireView().findViewById(R.id.textview_precip_mm_0);
        TextView textViewHumidity0 = requireView().findViewById(R.id.textview_humidity_0);

        TextView textViewDate1 = requireView().findViewById(R.id.textview_date_1);
        TextView textViewAvg1 = requireView().findViewById(R.id.textview_avg_1);
        TextView textViewHigh1 = requireView().findViewById(R.id.textview_high_1);
        TextView textViewLow1 = requireView().findViewById(R.id.textview_low_1);
        TextView textViewPop1 = requireView().findViewById(R.id.textview_pop_1);
        TextView textViewCondition1 = requireView().findViewById(R.id.textview_condition_1);
        TextView textViewMm1 = requireView().findViewById(R.id.textview_precip_mm_1);
        TextView textViewHumidity1 = requireView().findViewById(R.id.textview_humidity_1);

        TextView textViewDate2 = requireView().findViewById(R.id.textview_date_2);
        TextView textViewAvg2 = requireView().findViewById(R.id.textview_avg_2);
        TextView textViewHigh2 = requireView().findViewById(R.id.textview_high_2);
        TextView textViewLow2 = requireView().findViewById(R.id.textview_low_2);
        TextView textViewPop2 = requireView().findViewById(R.id.textview_pop_2);
        TextView textViewCondition2 = requireView().findViewById(R.id.textview_condition_2);
        TextView textViewMm2 = requireView().findViewById(R.id.textview_precip_mm_2);
        TextView textViewHumidity2 = requireView().findViewById(R.id.textview_humidity_2);

        TextView textViewSunrise0 = requireView().findViewById(R.id.textview_sunrise_0);
        TextView textViewSunrise1 = requireView().findViewById(R.id.textview_sunrise_1);
        TextView textViewSunrise2 = requireView().findViewById(R.id.textview_sunrise_2);

        TextView textViewSunset0 = requireView().findViewById(R.id.textview_sunset_0);
        TextView textViewSunset1 = requireView().findViewById(R.id.textview_sunset_1);
        TextView textViewSunset2 = requireView().findViewById(R.id.textview_sunset_2);

        editText.setText(settings.getString("location", ""));

        //                            location output stuff
        textViewCity.setText(settings.getString("city", ""));
        textViewRegion.setText("  \u2022  "+settings.getString("region", "")+"  \u2022  ");
        textViewCountry.setText(settings.getString("country", ""));

        textViewSunrise0.setText(settings.getString("day0Sunrise", ""));
        textViewSunrise1.setText(settings.getString("day1Sunrise", ""));
        textViewSunrise2.setText(settings.getString("day2Sunrise", ""));
        textViewSunset0.setText(settings.getString("day0Sunset", ""));
        textViewSunset1.setText(settings.getString("day1Sunset", ""));
        textViewSunset2.setText(settings.getString("day2Sunset", ""));

        //                            day 0 stuff
        textViewDate0.setText(settings.getString("day0Date", ""));
        textViewNow.setText(settings.getString("day0TempNow", ""));
        textViewFeels.setText(settings.getString("day0Feels", ""));
        textViewHigh0.setText(settings.getString("day0High", ""));
        textViewLow0.setText(settings.getString("day0Low", ""));
        textViewAvg0.setText(settings.getString("day0Avg", ""));
        textViewPop0.setText(settings.getString("day0Pop", ""));
        textViewCondition0.setText(settings.getString("day0Condition", ""));
        textViewMm0.setText(settings.getString("day0mm", ""));
        textViewHumidity0.setText(settings.getString("day0Humidity", ""));

        //                            day1 stuff
        textViewDate1.setText(settings.getString("day1Date", ""));
        textViewAvg1.setText(settings.getString("day1Avg", ""));
        textViewHigh1.setText(settings.getString("day1High", ""));
        textViewLow1.setText(settings.getString("day1Low", ""));
        textViewPop1.setText(settings.getString("day1Pop", ""));
        textViewCondition1.setText(settings.getString("day1Condition", ""));
        textViewMm1.setText(settings.getString("day1mm", ""));
        textViewHumidity1.setText(settings.getString("day1Humidity", ""));

        //                            day2 stuff
        textViewDate2.setText(settings.getString("day2Date", ""));
        textViewAvg2.setText(settings.getString("day2Avg", ""));
        textViewHigh2.setText(settings.getString("day2High", ""));
        textViewLow2.setText(settings.getString("day2Low", ""));
        textViewPop2.setText(settings.getString("day2Pop", ""));
        textViewCondition2.setText(settings.getString("day2Condition", ""));
        textViewMm2.setText(settings.getString("day2mm", ""));
        textViewHumidity2.setText(settings.getString("day2Humidity", ""));
    }

}