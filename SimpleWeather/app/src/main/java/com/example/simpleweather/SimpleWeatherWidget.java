package com.example.simpleweather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class SimpleWeatherWidget extends AppWidgetProvider {

//    static SharedPreferences settings;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        //The weather in "city" on "date0" is
        //"condition0".
//        String widgetText = settings.getString("day0Date", "").replace("Today: ","Weather for ") + settings.getString("day0Weather", "error") + settings.getString("day0TempNow", "error") + settings.getString("day0TempStats", "");
//        "Weather in " + settings.getString("city", "") + " for "
//                + settings.getString("day0Date", "") + "\n"
//                +
                String widgetText = settings.getString("day0Condition", "") + "  \u2022  " + "PoP: " + settings.getString("day0Pop", "") + "  \u2022  " + settings.getString("day0mm", "") + "\n"
                +"Now: "+settings.getString("day0TempNow","") + "  \u2022  " + "Feels: " + settings.getString("day0Feels", "") + "  \u2022  " + "Hum: " + settings.getString("day0Humidity", "")  + "\n"
                +"High: " + settings.getString("day0High", "") + "  \u2022  " + "Low: " + settings.getString("day0Low", "") + "  \u2022  " + "Avg: " + settings.getString("day0Avg", "");
//        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                /* context = */ context,
                /* requestCode = */ 0,
                /* intent = */ intent,
                /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.simple_weather_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them\\\\
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}