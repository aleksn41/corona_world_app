package de.dhbw.corona_world_app.map;

import android.util.Base64;

import java.util.List;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.datastructure.Country;

public class MapData {

    private static final String TAG = MapData.class.getName();

    private Resolution resolution;

    String WebViewStart = "<html><head><title>World Map</title><script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>" +
            "  <script type=\"text/javascript\">" +
            "  openStats = (country) => {jsinterface.setISOCountry(country)};" +
            "  google.charts.load('current', {" +
            "  'packages':['geochart']," +
            "  'mapsApiKey': '" + MapsKey.apiKey + "'});" +
            "  google.charts.setOnLoadCallback(drawMap);" +
            "  function drawMap() {" +
            "  var dataTable = new google.visualization.DataTable();" +
            "  dataTable.addColumn('string', 'Country');" +
            "  dataTable.addColumn('number', 'Infected-Healthy Ratio');" +
            "  dataTable.addRows([";

    public MapData(){
        this.resolution = Resolution.WOLRD;
    }

    public MapData(Resolution resolution){
        this.resolution = resolution;
    }

    public String putEntries(List<Country> entryList) {
        StringBuilder builder = new StringBuilder(entryList.size() * 100);
        Logger.logV(TAG, "Putting entries into StringBuilder...");
        if (entryList.size() > 0) {
            Country country = entryList.get(0);
            builder.append("['").append(country.getName().getISOCode()).append("',").append(getPercentValueOfDouble(country.getPop_inf_ratio())).append("]");
            for (int i = 1; i < entryList.size(); i++) {
                Country country1 = entryList.get(i);
                builder.append(",['").append(country1.getName().getISOCode()).append("',").append(getPercentValueOfDouble(country1.getPop_inf_ratio())).append("]");
            }
            Logger.logV(TAG, "Encoding and returning finished WebString...");
        } else {
            throw new IllegalArgumentException("An empty List was given as input!");
        }
        return Base64.encodeToString((WebViewStart + builder.toString() + getWebViewEnd()).getBytes(), Base64.NO_PADDING);
    }

    public enum Resolution{
        WOLRD("countries", "world"),
        GERMANY("provinces", "DE"),
        ;
        String value;
        String region;

        Resolution(String value, String region){
            this.value = value;
            this.region = region;
        }

        public String getValue() {
            return value;
        }

        public String getRegion() {
            return region;
        }
    }

    private String getWebViewEnd(){
        return "  ]);" +
                "  var chart = new google.visualization.GeoChart(document.getElementById('geochart-colors'));" +
                "  google.visualization.events.addListener(chart, 'select', function () {\n" +
                "     var selectedItem = chart.getSelection()[0];" +
                "     if(selectedItem != null){" +
                "        var true_selected = dataTable.getValue(selectedItem.row, 0);" +
                "        openStats(true_selected);" +
                "     } else {" +
                "        openStats(null);" +
                "     }" +
                "  });" +
                "  var options = {" +
                "    colorAxis: {colors: ['#3bff35', '#ddaf3d', '#dd7a3d', '#dd583d', '#cb2626'], values:[0.1, 1, 3, 5, 7]}," +
                "    backgroundColor: '#22748f'," +
                "    datalessRegionColor: '#ffffff'," +
                "    defaultColor: '#f5f5f5'," +
                "    tooltip: {trigger: 'none'}," +
                "    resolution: '"+this.resolution.value+"'," +
                "    region: '"+this.resolution.region+"'," +
                "    legend: 'none'," +
                "  };" +
                "  chart.draw(dataTable, options);};</script></head>" +
                "<body style='margin:0;padding:0;'><div id=\"geochart-colors\" style=\"width: 100%; height: 100%;\"></div></body></html>";
    }

    public void setResolution(Resolution resolution){
        this.resolution = resolution;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public double getPercentValueOfDouble(double number) {
        return 100 * number;
    }

}
