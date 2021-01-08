package de.dhbw.corona_world_app.map;

import android.util.Base64;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.datastructure.Country;

public class MapData {

    private static final String TAG = MapData.class.getName();

    Map<String, String> ISOCodeToDisplayName;

    String WebViewStart = "<html><head><title>World Map</title><script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script><script type=\"text/javascript\">" +
            "  window.goToStats = (country) => {console.log(country);};" +
            "  google.charts.load('current', {" +
            "  'packages':['geochart']," +
            "  'mapsApiKey': '" + MapsKey.apiKey + "'});" +
            "  google.charts.setOnLoadCallback(drawWorldMap);" +
            "  function drawWorldMap() {" +
            "  var dataTable = new google.visualization.DataTable();" +
            "  dataTable.addColumn('string', 'Country');" +
            "  dataTable.addColumn('number', 'Infected Population');" +
            "  dataTable.addColumn({'type': 'string', 'role': 'tooltip', 'p': {'html': true}});\n" +
            "  dataTable.addRows([";

    String WebViewEnd = "  ]);" +
            "  var options = {" +
            "    colorAxis: {colors: ['#3bff35', '#e31b23']}," +
            "    backgroundColor: '#22748f'," +
            "    datalessRegionColor: '#ffffff'," +
            "    defaultColor: '#f5f5f5'," +
            "    trigger: 'selection'," +
            "    resolution: 'countries'," +
            "    legend: 'none'," +
            "    tooltip: {isHtml: true}" +
            "  };" +
            "  function createCustomHTMLContent(ISOCountryName, ratio, totalHealthy, totalInfected, totalDeaths) {\n" +
            "  return '<div style=\"padding:5px 5px 5px 5px;\">' +\n" +
            "      '<table class=\"countryData_layout\">' + '<tr>' +\n" +
            "      '<td>Infected Population: </td>' +\n" +
            "      '<td><b>' + Math.round((ratio + Number.EPSILON) * 1000) / 1000 + '%</b></td>' + '</tr>' + '<tr>' +\n" +
            "      '<td>Total Healthy: </td>' +\n" +
            "      '<td><b>' + totalHealthy + '</b></td>' + '</tr>' + '<tr>' +\n" +
            "      '<td>Total Infected: </td>' +\n" +
            "      '<td><b>' + totalInfected + '</b></td>' + '</tr>' + '<tr>' +\n" +
            "      '<td>Total Deaths: </td>' +\n" +
            "      '<td><b>' + totalDeaths + '</b></td>' + '</tr>' + '</table>' + '</div>' + " +
            "      '<center><button type=\"button\" id=\"goTo\" onClick=\"window.goToStats(\\''+ISOCountryName+'\\')\">Go to statistics page</button></center>'" +
            "  }" +
            "  var chart = new google.visualization.GeoChart(document.getElementById('geochart-colors'));" +
            "  chart.draw(dataTable, options);};</script></head>" +
            "<body style='margin:0;padding:0;'><div id=\"geochart-colors\" style=\"width: 100%; height: 100%;\"></div></body></html>";

    public String putEntries(List<Country> entryList) {
        initISOToDisplayMap();
        StringBuilder builder = new StringBuilder(entryList.size() * 100);
        Logger.logV(TAG,"Putting entries into StringBuilder...");
        Country country = entryList.get(0);
        builder.append("['"+ISOCodeToDisplayName.get(country.getISOCountry().getISOCode())+"',"+ getPercentValueOfDouble(country.getPop_inf_ratio()) +", createCustomHTMLContent('"+country.getISOCountry().name()+"',"+getPercentValueOfDouble(country.getPop_inf_ratio())+","+country.getHealthy()+", "+country.getInfected()+", "+country.getDeaths()+")]");
        for (int i = 1; i < entryList.size(); i++) {
            Country country1 = entryList.get(i);
            builder.append(",['"+ISOCodeToDisplayName.get(country1.getISOCountry().getISOCode())+"',"+ getPercentValueOfDouble(country1.getPop_inf_ratio()) +", createCustomHTMLContent('"+country1.getISOCountry().name()+"',"+getPercentValueOfDouble(country1.getPop_inf_ratio())+","+country1.getHealthy()+", "+country1.getInfected()+", "+country1.getDeaths()+")]");
        }
        Logger.logV(TAG,"Encoding and returning finished WebString...");
        return Base64.encodeToString((WebViewStart + builder.toString() + WebViewEnd).getBytes(), Base64.NO_PADDING);
    }

    public double getPercentValueOfDouble(double number){
        return 100 * number;
    }

    //todo -> some entries with data are missing in the map
    private void initISOToDisplayMap(){
        if(ISOCodeToDisplayName==null) {
            Logger.logV(TAG, "Initiating ISOCodeToDisplay map...");
            ISOCodeToDisplayName = new HashMap<>();
            ISOCodeToDisplayName.put("AF","Afghanistan");
            ISOCodeToDisplayName.put("AX","Åland Islands");
            ISOCodeToDisplayName.put("AL","Albania");
            ISOCodeToDisplayName.put("DZ","Algeria");
            ISOCodeToDisplayName.put("AS","American Samoa");
            ISOCodeToDisplayName.put("AD","Andorra");
            ISOCodeToDisplayName.put("AO","Angola");
            ISOCodeToDisplayName.put("AI","Anguilla");
            ISOCodeToDisplayName.put("AQ","Antarctica");
            ISOCodeToDisplayName.put("AG","Antigua and Barbuda");
            ISOCodeToDisplayName.put("AR","Argentina");
            ISOCodeToDisplayName.put("AM","Armenia");
            ISOCodeToDisplayName.put("AW","Aruba");
            ISOCodeToDisplayName.put("AU","Australia");
            ISOCodeToDisplayName.put("AT","Austria");
            ISOCodeToDisplayName.put("AZ","Azerbaijan");
            ISOCodeToDisplayName.put("BS","Bahamas");
            ISOCodeToDisplayName.put("BH","Bahrain");
            ISOCodeToDisplayName.put("BD","Bangladesh");
            ISOCodeToDisplayName.put("BB","Barbados");
            ISOCodeToDisplayName.put("BY","Belarus");
            ISOCodeToDisplayName.put("BE","Belgium");
            ISOCodeToDisplayName.put("BZ","Belize");
            ISOCodeToDisplayName.put("BJ","Benin");
            ISOCodeToDisplayName.put("BM","Bermuda");
            ISOCodeToDisplayName.put("BT","Bhutan");
            ISOCodeToDisplayName.put("BO","Bolivia");
            ISOCodeToDisplayName.put("BQ","Bonaire, Sint Eustatius and Saba");
            ISOCodeToDisplayName.put("BA","Bosnia and Herzegovina");
            ISOCodeToDisplayName.put("BW","Botswana");
            ISOCodeToDisplayName.put("BV","Bouvet Island");
            ISOCodeToDisplayName.put("BR","Brazil");
            ISOCodeToDisplayName.put("IO","British Indian Ocean Territory");
            ISOCodeToDisplayName.put("BN","Brunei Darussalam");
            ISOCodeToDisplayName.put("BG","Bulgaria");
            ISOCodeToDisplayName.put("BF","Burkina Faso");
            ISOCodeToDisplayName.put("BI","Burundi");
            ISOCodeToDisplayName.put("KH","Cambodia");
            ISOCodeToDisplayName.put("CM","Cameroon");
            ISOCodeToDisplayName.put("CA","Canada");
            ISOCodeToDisplayName.put("CV","Cape Verde");
            ISOCodeToDisplayName.put("KY","Cayman Islands");
            ISOCodeToDisplayName.put("CF","Central African Republic");
            ISOCodeToDisplayName.put("TD","Chad");
            ISOCodeToDisplayName.put("CL","Chile");
            ISOCodeToDisplayName.put("CN","China");
            ISOCodeToDisplayName.put("CX","Christmas Island");
            ISOCodeToDisplayName.put("CC","Cocos (Keeling) Islands");
            ISOCodeToDisplayName.put("CO","Colombia");
            ISOCodeToDisplayName.put("KM","Comoros");
            ISOCodeToDisplayName.put("CG","CG");
            ISOCodeToDisplayName.put("CD","CD");
            ISOCodeToDisplayName.put("CK","Cook Islands");
            ISOCodeToDisplayName.put("CR","Costa Rica");
            ISOCodeToDisplayName.put("CI","Ivory Coast");
            ISOCodeToDisplayName.put("HR","Croatia");
            ISOCodeToDisplayName.put("CU","Cuba");
            ISOCodeToDisplayName.put("CW","Curaçao");
            ISOCodeToDisplayName.put("CY","Cyprus");
            ISOCodeToDisplayName.put("CZ","Czech Republic");
            ISOCodeToDisplayName.put("DK","Denmark");
            ISOCodeToDisplayName.put("DJ","Djibouti");
            ISOCodeToDisplayName.put("DM","Dominica");
            ISOCodeToDisplayName.put("DO","Dominican Republic");
            ISOCodeToDisplayName.put("EC","Ecuador");
            ISOCodeToDisplayName.put("EG","Egypt");
            ISOCodeToDisplayName.put("SV","El Salvador");
            ISOCodeToDisplayName.put("GQ","Equatorial Guinea");
            ISOCodeToDisplayName.put("ER","Eritrea");
            ISOCodeToDisplayName.put("EE","Estonia");
            ISOCodeToDisplayName.put("ET","Ethiopia");
            ISOCodeToDisplayName.put("FK","Falkland Islands");
            ISOCodeToDisplayName.put("FO","Faroe Islands");
            ISOCodeToDisplayName.put("FJ","Fiji");
            ISOCodeToDisplayName.put("FI","Finland");
            ISOCodeToDisplayName.put("FR","France");
            ISOCodeToDisplayName.put("GF","French Guiana");
            ISOCodeToDisplayName.put("PF","French Polynesia");
            ISOCodeToDisplayName.put("TF","French Southern Territories");
            ISOCodeToDisplayName.put("GA","Gabon");
            ISOCodeToDisplayName.put("GM","Gambia");
            ISOCodeToDisplayName.put("GE","Georgia");
            ISOCodeToDisplayName.put("DE","Germany");
            ISOCodeToDisplayName.put("GH","Ghana");
            ISOCodeToDisplayName.put("GI","Gibraltar");
            ISOCodeToDisplayName.put("GR","Greece");
            ISOCodeToDisplayName.put("GL","Greenland");
            ISOCodeToDisplayName.put("GD","Grenada");
            ISOCodeToDisplayName.put("GP","Guadeloupe");
            ISOCodeToDisplayName.put("GU","Guam");
            ISOCodeToDisplayName.put("GT","Guatemala");
            ISOCodeToDisplayName.put("GG","Guernsey");
            ISOCodeToDisplayName.put("GN","Guinea");
            ISOCodeToDisplayName.put("GW","Guinea-Bissau");
            ISOCodeToDisplayName.put("GY","Guyana");
            ISOCodeToDisplayName.put("HT","Haiti");
            ISOCodeToDisplayName.put("HM","Heard Island and McDonald Islands");
            ISOCodeToDisplayName.put("VA","Holy See (Vatican City State)");
            ISOCodeToDisplayName.put("HN","Honduras");
            ISOCodeToDisplayName.put("HK","Hong Kong");
            ISOCodeToDisplayName.put("HU","Hungary");
            ISOCodeToDisplayName.put("IS","Iceland");
            ISOCodeToDisplayName.put("IN","India");
            ISOCodeToDisplayName.put("ID","Indonesia");
            ISOCodeToDisplayName.put("IR","Iran");
            ISOCodeToDisplayName.put("IQ","Iraq");
            ISOCodeToDisplayName.put("IE","Ireland");
            ISOCodeToDisplayName.put("IM","Isle of Man");
            ISOCodeToDisplayName.put("IL","Israel");
            ISOCodeToDisplayName.put("IT","Italy");
            ISOCodeToDisplayName.put("JM","Jamaica");
            ISOCodeToDisplayName.put("JP","Japan");
            ISOCodeToDisplayName.put("JE","Jersey");
            ISOCodeToDisplayName.put("JO","Jordan");
            ISOCodeToDisplayName.put("KZ","Kazakhstan");
            ISOCodeToDisplayName.put("KE","Kenya");
            ISOCodeToDisplayName.put("KI","Kiribati");
            ISOCodeToDisplayName.put("KP","Korea (Democratic People\\'s Republic of)");
            ISOCodeToDisplayName.put("KR","South Korea");
            ISOCodeToDisplayName.put("KW","Kuwait");
            ISOCodeToDisplayName.put("KG","Kyrgyzstan");
            ISOCodeToDisplayName.put("LA","Laos");
            ISOCodeToDisplayName.put("LV","Latvia");
            ISOCodeToDisplayName.put("LB","Lebanon");
            ISOCodeToDisplayName.put("LS","Lesotho");
            ISOCodeToDisplayName.put("LR","Liberia");
            ISOCodeToDisplayName.put("LY","Libya");
            ISOCodeToDisplayName.put("LI","Liechtenstein");
            ISOCodeToDisplayName.put("LT","Lithuania");
            ISOCodeToDisplayName.put("LU","Luxembourg");
            ISOCodeToDisplayName.put("MO","Macao");
            ISOCodeToDisplayName.put("MK","Macedonia");
            ISOCodeToDisplayName.put("MG","Madagascar");
            ISOCodeToDisplayName.put("MW","Malawi");
            ISOCodeToDisplayName.put("MY","Malaysia");
            ISOCodeToDisplayName.put("MV","Maldives");
            ISOCodeToDisplayName.put("ML","Mali");
            ISOCodeToDisplayName.put("MT","Malta");
            ISOCodeToDisplayName.put("MH","Marshall Islands");
            ISOCodeToDisplayName.put("MQ","Martinique");
            ISOCodeToDisplayName.put("MR","Mauritania");
            ISOCodeToDisplayName.put("MU","Mauritius");
            ISOCodeToDisplayName.put("YT","Mayotte");
            ISOCodeToDisplayName.put("MX","Mexico");
            ISOCodeToDisplayName.put("FM","Micronesia (Federated States of)");
            ISOCodeToDisplayName.put("MD","Moldova");
            ISOCodeToDisplayName.put("MC","Monaco");
            ISOCodeToDisplayName.put("MN","Mongolia");
            ISOCodeToDisplayName.put("ME","Montenegro");
            ISOCodeToDisplayName.put("MS","Montserrat");
            ISOCodeToDisplayName.put("MA","Morocco");
            ISOCodeToDisplayName.put("MZ","Mozambique");
            ISOCodeToDisplayName.put("MM","Myanmar");
            ISOCodeToDisplayName.put("NA","Namibia");
            ISOCodeToDisplayName.put("NR","Nauru");
            ISOCodeToDisplayName.put("NP","Nepal");
            ISOCodeToDisplayName.put("NL","Netherlands");
            ISOCodeToDisplayName.put("NC","New Caledonia");
            ISOCodeToDisplayName.put("NZ","New Zealand");
            ISOCodeToDisplayName.put("NI","Nicaragua");
            ISOCodeToDisplayName.put("NE","Niger");
            ISOCodeToDisplayName.put("NG","Nigeria");
            ISOCodeToDisplayName.put("NU","Niue");
            ISOCodeToDisplayName.put("NF","Norfolk Island");
            ISOCodeToDisplayName.put("MP","Northern Mariana Islands");
            ISOCodeToDisplayName.put("NO","Norway");
            ISOCodeToDisplayName.put("OM","Oman");
            ISOCodeToDisplayName.put("PK","Pakistan");
            ISOCodeToDisplayName.put("PW","Palau");
            ISOCodeToDisplayName.put("PS","Palestine, State of");
            ISOCodeToDisplayName.put("PA","Panama");
            ISOCodeToDisplayName.put("PG","Papua New Guinea");
            ISOCodeToDisplayName.put("PY","Paraguay");
            ISOCodeToDisplayName.put("PE","Peru");
            ISOCodeToDisplayName.put("PH","Philippines");
            ISOCodeToDisplayName.put("PN","Pitcairn");
            ISOCodeToDisplayName.put("PL","Poland");
            ISOCodeToDisplayName.put("PT","Portugal");
            ISOCodeToDisplayName.put("PR","Puerto Rico");
            ISOCodeToDisplayName.put("QA","Qatar");
            ISOCodeToDisplayName.put("RE","Reunion");
            ISOCodeToDisplayName.put("RO","Romania");
            ISOCodeToDisplayName.put("RU","Russia");
            ISOCodeToDisplayName.put("RW","Rwanda");
            ISOCodeToDisplayName.put("BL","Saint Barthélemy");
            ISOCodeToDisplayName.put("SH","Saint Helena, Ascension and Tristan da Cunha");
            ISOCodeToDisplayName.put("KN","Saint Kitts and Nevis");
            ISOCodeToDisplayName.put("LC","Saint Lucia");
            ISOCodeToDisplayName.put("MF","Saint Martin (French part)");
            ISOCodeToDisplayName.put("PM","Saint Pierre and Miquelon");
            ISOCodeToDisplayName.put("VC","Saint Vincent and the Grenadines");
            ISOCodeToDisplayName.put("WS","Samoa");
            ISOCodeToDisplayName.put("SM","San Marino");
            ISOCodeToDisplayName.put("ST","Sao Tome and Principe");
            ISOCodeToDisplayName.put("SA","Saudi Arabia");
            ISOCodeToDisplayName.put("SN","Senegal");
            ISOCodeToDisplayName.put("RS","Serbia");
            ISOCodeToDisplayName.put("SC","Seychelles");
            ISOCodeToDisplayName.put("SL","Sierra Leone");
            ISOCodeToDisplayName.put("SG","Singapore");
            ISOCodeToDisplayName.put("SX","Sint Maarten (Dutch part)");
            ISOCodeToDisplayName.put("SK","Slovakia");
            ISOCodeToDisplayName.put("SI","Slovenia");
            ISOCodeToDisplayName.put("SB","Solomon Islands");
            ISOCodeToDisplayName.put("SO","Somalia");
            ISOCodeToDisplayName.put("ZA","South Africa");
            ISOCodeToDisplayName.put("GS","South Georgia and the South Sandwich Islands");
            ISOCodeToDisplayName.put("SS","SS");
            ISOCodeToDisplayName.put("ES","Spain");
            ISOCodeToDisplayName.put("LK","Sri Lanka");
            ISOCodeToDisplayName.put("SD","Sudan");
            ISOCodeToDisplayName.put("SR","Suriname");
            ISOCodeToDisplayName.put("SJ","Svalbard");
            ISOCodeToDisplayName.put("SZ","Swaziland");
            ISOCodeToDisplayName.put("SE","Sweden");
            ISOCodeToDisplayName.put("CH","Switzerland");
            ISOCodeToDisplayName.put("SY","Syria");
            ISOCodeToDisplayName.put("TW","Taiwan");
            ISOCodeToDisplayName.put("TJ","Tajikistan");
            ISOCodeToDisplayName.put("TZ","Tanzania");
            ISOCodeToDisplayName.put("TH","Thailand");
            ISOCodeToDisplayName.put("TL","TL");
            ISOCodeToDisplayName.put("TG","Togo");
            ISOCodeToDisplayName.put("TK","Tokelau");
            ISOCodeToDisplayName.put("TO","Tonga");
            ISOCodeToDisplayName.put("TT","Trinidad and Tobago");
            ISOCodeToDisplayName.put("TN","Tunisia");
            ISOCodeToDisplayName.put("TR","Turkey");
            ISOCodeToDisplayName.put("TM","Turkmenistan");
            ISOCodeToDisplayName.put("TC","Turks and Caicos Islands");
            ISOCodeToDisplayName.put("TV","Tuvalu");
            ISOCodeToDisplayName.put("UG","Uganda");
            ISOCodeToDisplayName.put("UA","Ukraine");
            ISOCodeToDisplayName.put("AE","United Arab Emirates");
            ISOCodeToDisplayName.put("GB","United Kingdom");
            ISOCodeToDisplayName.put("US","United States");
            ISOCodeToDisplayName.put("UM","United States Minor Outlying Islands");
            ISOCodeToDisplayName.put("UY","Uruguay");
            ISOCodeToDisplayName.put("UZ","Uzbekistan");
            ISOCodeToDisplayName.put("VU","Vanuatu");
            ISOCodeToDisplayName.put("VE","Venezuela");
            ISOCodeToDisplayName.put("VN","Vietnam");
            ISOCodeToDisplayName.put("VG","Virgin Islands (British)");
            ISOCodeToDisplayName.put("VI","Virgin Islands (U.S.)");
            ISOCodeToDisplayName.put("WF","Wallis and Futuna");
            ISOCodeToDisplayName.put("EH","Western Sahara");
            ISOCodeToDisplayName.put("YE","Yemen");
            ISOCodeToDisplayName.put("ZM","Zambia");
            ISOCodeToDisplayName.put("ZW","Zimbabwe");
            ISOCodeToDisplayName.put("XK","Kosovo");
        }
    }
}
