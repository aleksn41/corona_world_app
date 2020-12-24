package de.dhbw.corona_world_app.map;

import android.util.Base64;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapData {

    String WebViewStart = "<html>\n" +
            "\t<head>\n" +
            "\t\t<title>World Map</title>\n" +
            "\t\t<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n" +
            "\t\t<script type=\"text/javascript\">\n" +
            "\t\t\tgoogle.charts.load('visualization', {\n" +
            "\t\t\t  'packages':['geochart'],\n" +
            "\t\t\t  // Note: you will need to get a mapsApiKey for your project.\n" +
            "\t\t\t  // See: https://developers.google.com/chart/interactive/docs/basic_load_libs#load-settings\n" +
            "\t\t\t  // https://developers.google.com/maps/documentation/javascript/get-api-key\n" +
            "\t\t\t  'mapsApiKey': '"+ MapsKey.apiKey+"'\n" +
            "\t\t\t});\n" +
            "\t\t\tgoogle.charts.setOnLoadCallback(drawRegionsMap);\n" +
            "\t\t\t\n" +
            "\t\t\tfunction drawRegionsMap() {\n" +
            "\t\t\t  var data = google.visualization.arrayToDataTable([\n" +
            "\t\t\t  ['Country','Infected/Healthy-Ratio']\n";

            String WebViewEnd = "\t\t\t  ]);\n" +
            "\t\t\t\n" +
            "\t\t\t  var options = {\n" +
            "\t\t\t    colorAxis: {colors: ['#00853f', 'black', '#e31b23']},\n" +
            "\t\t\t    backgroundColor: '#81d4fa',\n" +
            "\t\t\t    datalessRegionColor: '#f8bbd0',\n" +
            "\t\t\t    defaultColor: '#f5f5f5',\n" +
            "\t\t\t  };\n" +
            "\t\t\t\n" +
            "\t\t\t  var chart = new google.visualization.GeoChart(document.getElementById('geochart-colors'));\n" +
            "\t\t\t  chart.draw(data);\n" +
            "\t\t\t};\n" +
            "\t\t</script>\n" +
            "\t</head>\n" +
            "\t<body>\n" +
                    "\t<h1>World Map</h1>" +
            "\t\t<div id=\"geochart-colors\" style=\"width: 900px; height: 500px;\"></div>\n" +
            "\t</body>\n" +
            "</html>";

            public String putEntry(String countryName, double infected_healthy_ratio){
                return WebViewStart + ",[" + countryName + "," + infected_healthy_ratio + "]" + WebViewEnd;
            }

            //todo use StringBuilder
            public String putEntries(Map<String, Double> entryMap){
                List<Map.Entry<String,Double>> entryList = new ArrayList<>(entryMap.entrySet());
                String returnString = "";
                for (Map.Entry<String,Double> entry: entryList) {
                    returnString += ",['"+entry.getKey()+"',"+entry.getValue()+"]";
                }
                //System.out.println("Return="+returnString);
                return Base64.encodeToString((WebViewStart + returnString + WebViewEnd).getBytes(), Base64.NO_PADDING);
                //return WebViewStart + ",['Germany',100]" + WebViewEnd;
            }
}
