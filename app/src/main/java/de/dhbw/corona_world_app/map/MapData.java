package de.dhbw.corona_world_app.map;

import android.util.Base64;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.datastructure.Country;

public class MapData {

    private static final String TAG = MapData.class.getName();

    String WebViewStart = "<html><head><title>World Map</title><script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>" +
            "  <style>\n" +
            "         @import url('https://fonts.googleapis.com/css?family=Open+Sans');\n" +
            "         *, *:before, *:after { font-style: normal !important; }\n" +
            "         body { position: relative !important; }\n" +
            "         form { background-color: #ffffff; }\n" +
            "         #ii { margin-top: 80px }\n" +
            "         .panel { display: flex; flex-wrap:wrap; justify-content: center; align-items: center; }\n" +
            "         #chart { height: 70vh; width: 96vw; }\n" +
            "         .group.group:after, .chart.chart:after, .root.root:after { color: #FFFFFF; }\n" +
            "         div.google-visualization-tooltip {position: absolute !important; background-color: #FFFFFF; border-radius: 6px; max-width: device-width; max-height: device-height; font-size: 4px}\n" +
            "         div.google-visualization-tooltip > ul > li > span { color: gold; }\n" +
            "         #groupOpt { display:none; }\n" +
            "         #groupOpt.on { display:block;}\n" +
            "  </style>" +
            "  <script type=\"text/javascript\">" +
            "  window.goToStats = (country) => {console.log(country);};" +
            "  google.charts.load('current', {" +
            "  'packages':['geochart']," +
            "  'mapsApiKey': '" + MapsKey.apiKey + "'});" +
            "  google.charts.setOnLoadCallback(drawWorldMap);" +
            "  function drawWorldMap() {" +
            "  var dataTable = new google.visualization.DataTable();" +
            "  dataTable.addColumn('string', 'Country');" +
            "  dataTable.addColumn('number', 'Infected Population');" +
            "  dataTable.addRows([";

    String WebViewEnd = "  ]);" +
            "  var chart = new google.visualization.GeoChart(document.getElementById('geochart-colors'));" +
            "  google.visualization.events.addListener(chart, 'select', function () {\n" +
            "     var selectedItem = chart.getSelection()[0];" +
            "     if(selectedItem != null){" +
            "        var true_selected = dataTable.getValue(selectedItem.row, 0);" +
            "        console.log(true_selected);" +
            "     } else {" +
            "        console.log(\"Nothing was selected.\");" +
            "     }" +
            "  });" +
            "  var options = {" +
            "    colorAxis: {colors: ['#3bff35', '#e31b23']}," +
            "    backgroundColor: '#22748f'," +
            "    datalessRegionColor: '#ffffff'," +
            "    defaultColor: '#f5f5f5'," +
            "    tooltip: {trigger: 'none'}," +
            "    resolution: 'countries'," +
            "    legend: 'none'," +
            "  };" +
            "  chart.draw(dataTable, options);};</script></head>" +
            "<body style='margin:0;padding:0;'><div id=\"geochart-colors\" style=\"width: 100%; height: 100%;\"></div></body></html>";

    public String putEntries(List<Country> entryList) {
        StringBuilder builder = new StringBuilder(entryList.size() * 100);
        Logger.logV(TAG,"Putting entries into StringBuilder...");
        if(entryList.size() > 0) {
            Country country = entryList.get(0);
            builder.append("['").append(country.getISOCountry().getISOCode()).append("',").append(getPercentValueOfDouble(country.getPop_inf_ratio())).append("]");
            for (int i = 1; i < entryList.size(); i++) {
                Country country1 = entryList.get(i);
                builder.append(",['").append(country1.getISOCountry().getISOCode()).append("',").append(getPercentValueOfDouble(country1.getPop_inf_ratio())).append("]");
            }
            Logger.logV(TAG, "Encoding and returning finished WebString...");
        } else {
            throw new IllegalArgumentException("An empty List was given as input!");
        }
        return Base64.encodeToString((WebViewStart + builder.toString() + WebViewEnd).getBytes(), Base64.NO_PADDING);
    }

    public double getPercentValueOfDouble(double number){
        return 100 * number;
    }

}
