package de.dhbw.corona_world_app.map;

import android.util.Base64;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapData {

    String WebViewStart =
            "<html><head><title>World Map</title><script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script><script type=\"text/javascript\">" +
            "google.charts.load('visualization', {" +
            "  'packages':['geochart']," +
            "  'mapsApiKey': '" + MapsKey.apiKey + "'});" +
            "google.charts.setOnLoadCallback(drawRegionsMap);" +
            "function drawRegionsMap() {" +
            "  var data = google.visualization.arrayToDataTable([" +
            "  ['Country','Infected/Healthy-Ratio']";

            String WebViewEnd = "  ]);" +
            "  var options = {" +
            "   colorAxis: {colors: ['#ffffff', '#e31b23']}," +
            "    backgroundColor: '#22748f'," +
            "    datalessRegionColor: '#ffffff'," +
            "    defaultColor: '#f5f5f5'," +
            "    trigger: 'selection'," +
            "    resolution: 'countries',"+
            "    legend: 'none'" +
                    "};" +
            "  var chart = new google.visualization.GeoChart(document.getElementById('geochart-colors'));" +
            "  chart.draw(data, options);};</script></head>" +
            "<body style='margin:0;padding:0;'><div id=\"geochart-colors\" style=\"width: 100%; height: 100%;\"></div></body></html>";

            public String putEntry(String countryName, double infected_healthy_ratio){
                return WebViewStart + ",[" + countryName + "," + infected_healthy_ratio + "]" + WebViewEnd;
            }

            public String putEntries(Map<String, Double> entryMap){
                StringBuilder builder = new StringBuilder();
                List<Map.Entry<String,Double>> entryList = new ArrayList<>(entryMap.entrySet());
                for (Map.Entry<String,Double> entry: entryList) {
                    builder.append(",['"+entry.getKey()+"',"+entry.getValue()+"]");
                }

                return Base64.encodeToString((WebViewStart + builder.toString() + WebViewEnd).getBytes(), Base64.NO_PADDING);
            }
}
