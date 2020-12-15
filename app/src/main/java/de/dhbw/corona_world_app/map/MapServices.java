package de.dhbw.corona_world_app.map;

public class MapServices {

    String WebView = "<html>\n" +
            "\t<head>\n" +
            "\t\t<title>Google GeoChart English Country Names Sample</title>\n" +
            "\t\t<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n" +
            "\t\t<script type=\"text/javascript\">\n" +
            "\t\t\tgoogle.charts.load('current', {\n" +
            "\t\t\t  'packages':['geochart'],\n" +
            "\t\t\t  // Note: you will need to get a mapsApiKey for your project.\n" +
            "\t\t\t  // See: https://developers.google.com/chart/interactive/docs/basic_load_libs#load-settings\n" +
            "\t\t\t  // https://developers.google.com/maps/documentation/javascript/get-api-key\n" +
            "\t\t\t  'mapsApiKey': '"+APIKeyGetter.apiKey+"'\n" +
            "\t\t\t});\n" +
            "\t\t\tgoogle.charts.setOnLoadCallback(drawRegionsMap);\n" +
            "\t\t\t\n" +
            "\t\t\tfunction drawRegionsMap() {\n" +
            "\t\t\t  var data = google.visualization.arrayToDataTable([\n" +
            "\t\t\t    ['Country'],\n" +
            "\t\t\t\t['Afghanistan'],\t //\tAF\n" +
            "\t\t\t\t['Åland Islands'],\t //\tAX\n" +
            "\t\t\t\t['Albania'],\t //\tAL\n" +
            "\t\t\t\t['Algeria'],\t //\tDZ\n" +
            "\t\t\t\t['American Samoa'],\t //\tAS\n" +
            "\t\t\t\t['Andorra'],\t //\tAD\n" +
            "\t\t\t\t['Angola'],\t //\tAO\n" +
            "\t\t\t\t['Anguilla'],\t //\tAI\n" +
            "\t\t\t\t['Antarctica'],\t //\tAQ\n" +
            "\t\t\t\t['Antigua and Barbuda'],\t //\tAG\n" +
            "\t\t\t\t['Argentina'],\t //\tAR\n" +
            "\t\t\t\t['Armenia'],\t //\tAM\n" +
            "\t\t\t\t['Aruba'],\t //\tAW\n" +
            "\t\t\t\t['Australia'],\t //\tAU\n" +
            "\t\t\t\t['Austria'],\t //\tAT\n" +
            "\t\t\t\t['Azerbaijan'],\t //\tAZ\n" +
            "\t\t\t\t['Bahamas'],\t //\tBS\n" +
            "\t\t\t\t['Bahrain'],\t //\tBH\n" +
            "\t\t\t\t['Bangladesh'],\t //\tBD\n" +
            "\t\t\t\t['Barbados'],\t //\tBB\n" +
            "\t\t\t\t['Belarus'],\t //\tBY\n" +
            "\t\t\t\t['Belgium'],\t //\tBE\n" +
            "\t\t\t\t['Belize'],\t //\tBZ\n" +
            "\t\t\t\t['Benin'],\t //\tBJ\n" +
            "\t\t\t\t['Bermuda'],\t //\tBM\n" +
            "\t\t\t\t['Bhutan'],\t //\tBT\n" +
            "\t\t\t\t['Bolivia (Plurinational State of)'],\t //\tBO\n" +
            "\t\t\t\t['Bonaire, Sint Eustatius and Saba'],\t //\tBQ\n" +
            "\t\t\t\t['Bosnia and Herzegovina'],\t //\tBA\n" +
            "\t\t\t\t['Botswana'],\t //\tBW\n" +
            "\t\t\t\t['Bouvet Island'],\t //\tBV\n" +
            "\t\t\t\t['Brazil'],\t //\tBR\n" +
            "\t\t\t\t['British Indian Ocean Territory'],\t //\tIO\n" +
            "\t\t\t\t['Brunei Darussalam'],\t //\tBN\n" +
            "\t\t\t\t['Bulgaria'],\t //\tBG\n" +
            "\t\t\t\t['Burkina Faso'],\t //\tBF\n" +
            "\t\t\t\t['Burundi'],\t //\tBI\n" +
            "\t\t\t\t['Cabo Verde'],\t //\tCV\n" +
            "\t\t\t\t['Cambodia'],\t //\tKH\n" +
            "\t\t\t\t['Cameroon'],\t //\tCM\n" +
            "\t\t\t\t['Canada'],\t //\tCA\n" +
            "\t\t\t\t['Cayman Islands'],\t //\tKY\n" +
            "\t\t\t\t['Central African Republic'],\t //\tCF\n" +
            "\t\t\t\t['Chad'],\t //\tTD\n" +
            "\t\t\t\t['Chile'],\t //\tCL\n" +
            "\t\t\t\t['China'],\t //\tCN\n" +
            "\t\t\t\t['Christmas Island'],\t //\tCX\n" +
            "\t\t\t\t['Cocos (Keeling) Islands'],\t //\tCC\n" +
            "\t\t\t\t['Colombia'],\t //\tCO\n" +
            "\t\t\t\t['Comoros'],\t //\tKM\n" +
            "\t\t\t\t['Congo'],\t //\tCG\n" +
            "\t\t\t\t['Congo (Democratic Republic of the)'],\t //\tCD\n" +
            "\t\t\t\t['Cook Islands'],\t //\tCK\n" +
            "\t\t\t\t['Costa Rica'],\t //\tCR\n" +
            "\t\t\t\t['Côte d\\'Ivoire'],\t //\tCI\n" +
            "\t\t\t\t['Croatia'],\t //\tHR\n" +
            "\t\t\t\t['Cuba'],\t //\tCU\n" +
            "\t\t\t\t['Curaçao'],\t //\tCW\n" +
            "\t\t\t\t['Cyprus'],\t //\tCY\n" +
            "\t\t\t\t['Czechia'],\t //\tCZ\n" +
            "\t\t\t\t['Denmark'],\t //\tDK\n" +
            "\t\t\t\t['Djibouti'],\t //\tDJ\n" +
            "\t\t\t\t['Dominica'],\t //\tDM\n" +
            "\t\t\t\t['Dominican Republic'],\t //\tDO\n" +
            "\t\t\t\t['Ecuador'],\t //\tEC\n" +
            "\t\t\t\t['Egypt'],\t //\tEG\n" +
            "\t\t\t\t['El Salvador'],\t //\tSV\n" +
            "\t\t\t\t['Equatorial Guinea'],\t //\tGQ\n" +
            "\t\t\t\t['Eritrea'],\t //\tER\n" +
            "\t\t\t\t['Estonia'],\t //\tEE\n" +
            "\t\t\t\t['Ethiopia'],\t //\tET\n" +
            "\t\t\t\t['Falkland Islands (Malvinas)'],\t //\tFK\n" +
            "\t\t\t\t['Faroe Islands'],\t //\tFO\n" +
            "\t\t\t\t['Fiji'],\t //\tFJ\n" +
            "\t\t\t\t['Finland'],\t //\tFI\n" +
            "\t\t\t\t['France'],\t //\tFR\n" +
            "\t\t\t\t['French Guiana'],\t //\tGF\n" +
            "\t\t\t\t['French Polynesia'],\t //\tPF\n" +
            "\t\t\t\t['French Southern Territories'],\t //\tTF\n" +
            "\t\t\t\t['Gabon'],\t //\tGA\n" +
            "\t\t\t\t['Gambia'],\t //\tGM\n" +
            "\t\t\t\t['Georgia'],\t //\tGE\n" +
            "\t\t\t\t['Germany'],\t //\tDE\n" +
            "\t\t\t\t['Ghana'],\t //\tGH\n" +
            "\t\t\t\t['Gibraltar'],\t //\tGI\n" +
            "\t\t\t\t['Greece'],\t //\tGR\n" +
            "\t\t\t\t['Greenland'],\t //\tGL\n" +
            "\t\t\t\t['Grenada'],\t //\tGD\n" +
            "\t\t\t\t['Guadeloupe'],\t //\tGP\n" +
            "\t\t\t\t['Guam'],\t //\tGU\n" +
            "\t\t\t\t['Guatemala'],\t //\tGT\n" +
            "\t\t\t\t['Guernsey'],\t //\tGG\n" +
            "\t\t\t\t['Guinea'],\t //\tGN\n" +
            "\t\t\t\t['Guinea-Bissau'],\t //\tGW\n" +
            "\t\t\t\t['Guyana'],\t //\tGY\n" +
            "\t\t\t\t['Haiti'],\t //\tHT\n" +
            "\t\t\t\t['Heard Island and McDonald Islands'],\t //\tHM\n" +
            "\t\t\t\t['Holy See'],\t //\tVA\n" +
            "\t\t\t\t['Honduras'],\t //\tHN\n" +
            "\t\t\t\t['Hong Kong'],\t //\tHK\n" +
            "\t\t\t\t['Hungary'],\t //\tHU\n" +
            "\t\t\t\t['Iceland'],\t //\tIS\n" +
            "\t\t\t\t['India'],\t //\tIN\n" +
            "\t\t\t\t['Indonesia'],\t //\tID\n" +
            "\t\t\t\t['Iran (Islamic Republic of)'],\t //\tIR\n" +
            "\t\t\t\t['Iraq'],\t //\tIQ\n" +
            "\t\t\t\t['Ireland'],\t //\tIE\n" +
            "\t\t\t\t['Isle of Man'],\t //\tIM\n" +
            "\t\t\t\t['Israel'],\t //\tIL\n" +
            "\t\t\t\t['Italy'],\t //\tIT\n" +
            "\t\t\t\t['Jamaica'],\t //\tJM\n" +
            "\t\t\t\t['Japan'],\t //\tJP\n" +
            "\t\t\t\t['Jersey'],\t //\tJE\n" +
            "\t\t\t\t['Jordan'],\t //\tJO\n" +
            "\t\t\t\t['Kazakhstan'],\t //\tKZ\n" +
            "\t\t\t\t['Kenya'],\t //\tKE\n" +
            "\t\t\t\t['Kiribati'],\t //\tKI\n" +
            "\t\t\t\t['Korea (Democratic People\\'s Republic of)'],\t //\tKP\n" +
            "\t\t\t\t['Korea (Republic of)'],\t //\tKR\n" +
            "\t\t\t\t['Kuwait'],\t //\tKW\n" +
            "\t\t\t\t['Kyrgyzstan'],\t //\tKG\n" +
            "\t\t\t\t['Lao People\\'s Democratic Republic'],\t //\tLA\n" +
            "\t\t\t\t['Latvia'],\t //\tLV\n" +
            "\t\t\t\t['Lebanon'],\t //\tLB\n" +
            "\t\t\t\t['Lesotho'],\t //\tLS\n" +
            "\t\t\t\t['Liberia'],\t //\tLR\n" +
            "\t\t\t\t['Libya'],\t //\tLY\n" +
            "\t\t\t\t['Liechtenstein'],\t //\tLI\n" +
            "\t\t\t\t['Lithuania'],\t //\tLT\n" +
            "\t\t\t\t['Luxembourg'],\t //\tLU\n" +
            "\t\t\t\t['Macao'],\t //\tMO\n" +
            "\t\t\t\t['Macedonia (the former Yugoslav Republic of)'],\t //\tMK\n" +
            "\t\t\t\t['Madagascar'],\t //\tMG\n" +
            "\t\t\t\t['Malawi'],\t //\tMW\n" +
            "\t\t\t\t['Malaysia'],\t //\tMY\n" +
            "\t\t\t\t['Maldives'],\t //\tMV\n" +
            "\t\t\t\t['Mali'],\t //\tML\n" +
            "\t\t\t\t['Malta'],\t //\tMT\n" +
            "\t\t\t\t['Marshall Islands'],\t //\tMH\n" +
            "\t\t\t\t['Martinique'],\t //\tMQ\n" +
            "\t\t\t\t['Mauritania'],\t //\tMR\n" +
            "\t\t\t\t['Mauritius'],\t //\tMU\n" +
            "\t\t\t\t['Mayotte'],\t //\tYT\n" +
            "\t\t\t\t['Mexico'],\t //\tMX\n" +
            "\t\t\t\t['Micronesia (Federated States of)'],\t //\tFM\n" +
            "\t\t\t\t['Moldova (Republic of)'],\t //\tMD\n" +
            "\t\t\t\t['Monaco'],\t //\tMC\n" +
            "\t\t\t\t['Mongolia'],\t //\tMN\n" +
            "\t\t\t\t['Montenegro'],\t //\tME\n" +
            "\t\t\t\t['Montserrat'],\t //\tMS\n" +
            "\t\t\t\t['Morocco'],\t //\tMA\n" +
            "\t\t\t\t['Mozambique'],\t //\tMZ\n" +
            "\t\t\t\t['Myanmar'],\t //\tMM\n" +
            "\t\t\t\t['Namibia'],\t //\tNA\n" +
            "\t\t\t\t['Nauru'],\t //\tNR\n" +
            "\t\t\t\t['Nepal'],\t //\tNP\n" +
            "\t\t\t\t['Netherlands'],\t //\tNL\n" +
            "\t\t\t\t['New Caledonia'],\t //\tNC\n" +
            "\t\t\t\t['New Zealand'],\t //\tNZ\n" +
            "\t\t\t\t['Nicaragua'],\t //\tNI\n" +
            "\t\t\t\t['Niger'],\t //\tNE\n" +
            "\t\t\t\t['Nigeria'],\t //\tNG\n" +
            "\t\t\t\t['Niue'],\t //\tNU\n" +
            "\t\t\t\t['Norfolk Island'],\t //\tNF\n" +
            "\t\t\t\t['Northern Mariana Islands'],\t //\tMP\n" +
            "\t\t\t\t['Norway'],\t //\tNO\n" +
            "\t\t\t\t['Oman'],\t //\tOM\n" +
            "\t\t\t\t['Pakistan'],\t //\tPK\n" +
            "\t\t\t\t['Palau'],\t //\tPW\n" +
            "\t\t\t\t['Palestine, State of'],\t //\tPS\n" +
            "\t\t\t\t['Panama'],\t //\tPA\n" +
            "\t\t\t\t['Papua New Guinea'],\t //\tPG\n" +
            "\t\t\t\t['Paraguay'],\t //\tPY\n" +
            "\t\t\t\t['Peru'],\t //\tPE\n" +
            "\t\t\t\t['Philippines'],\t //\tPH\n" +
            "\t\t\t\t['Pitcairn'],\t //\tPN\n" +
            "\t\t\t\t['Poland'],\t //\tPL\n" +
            "\t\t\t\t['Portugal'],\t //\tPT\n" +
            "\t\t\t\t['Puerto Rico'],\t //\tPR\n" +
            "\t\t\t\t['Qatar'],\t //\tQA\n" +
            "\t\t\t\t['Réunion'],\t //\tRE\n" +
            "\t\t\t\t['Romania'],\t //\tRO\n" +
            "\t\t\t\t['Russian Federation'],\t //\tRU\n" +
            "\t\t\t\t['Rwanda'],\t //\tRW\n" +
            "\t\t\t\t['Saint Barthélemy'],\t //\tBL\n" +
            "\t\t\t\t['Saint Helena, Ascension and Tristan da Cunha'],\t //\tSH\n" +
            "\t\t\t\t['Saint Kitts and Nevis'],\t //\tKN\n" +
            "\t\t\t\t['Saint Lucia'],\t //\tLC\n" +
            "\t\t\t\t['Saint Martin (French part)'],\t //\tMF\n" +
            "\t\t\t\t['Saint Pierre and Miquelon'],\t //\tPM\n" +
            "\t\t\t\t['Saint Vincent and the Grenadines'],\t //\tVC\n" +
            "\t\t\t\t['Samoa'],\t //\tWS\n" +
            "\t\t\t\t['San Marino'],\t //\tSM\n" +
            "\t\t\t\t['Sao Tome and Principe'],\t //\tST\n" +
            "\t\t\t\t['Saudi Arabia'],\t //\tSA\n" +
            "\t\t\t\t['Senegal'],\t //\tSN\n" +
            "\t\t\t\t['Serbia'],\t //\tRS\n" +
            "\t\t\t\t['Seychelles'],\t //\tSC\n" +
            "\t\t\t\t['Sierra Leone'],\t //\tSL\n" +
            "\t\t\t\t['Singapore'],\t //\tSG\n" +
            "\t\t\t\t['Sint Maarten (Dutch part)'],\t //\tSX\n" +
            "\t\t\t\t['Slovakia'],\t //\tSK\n" +
            "\t\t\t\t['Slovenia'],\t //\tSI\n" +
            "\t\t\t\t['Solomon Islands'],\t //\tSB\n" +
            "\t\t\t\t['Somalia'],\t //\tSO\n" +
            "\t\t\t\t['South Africa'],\t //\tZA\n" +
            "\t\t\t\t['South Georgia and the South Sandwich Islands'],\t //\tGS\n" +
            "\t\t\t\t['South Sudan'],\t //\tSS\n" +
            "\t\t\t\t['Spain'],\t //\tES\n" +
            "\t\t\t\t['Sri Lanka'],\t //\tLK\n" +
            "\t\t\t\t['Sudan'],\t //\tSD\n" +
            "\t\t\t\t['Suriname'],\t //\tSR\n" +
            "\t\t\t\t['Svalbard and Jan Mayen'],\t //\tSJ\n" +
            "\t\t\t\t['Swaziland'],\t //\tSZ\n" +
            "\t\t\t\t['Sweden'],\t //\tSE\n" +
            "\t\t\t\t['Switzerland'],\t //\tCH\n" +
            "\t\t\t\t['Syrian Arab Republic'],\t //\tSY\n" +
            "\t\t\t\t['Taiwan, Province of China[a]'],\t //\tTW\n" +
            "\t\t\t\t['Tajikistan'],\t //\tTJ\n" +
            "\t\t\t\t['Tanzania, United Republic of'],\t //\tTZ\n" +
            "\t\t\t\t['Thailand'],\t //\tTH\n" +
            "\t\t\t\t['Timor-Leste'],\t //\tTL\n" +
            "\t\t\t\t['Togo'],\t //\tTG\n" +
            "\t\t\t\t['Tokelau'],\t //\tTK\n" +
            "\t\t\t\t['Tonga'],\t //\tTO\n" +
            "\t\t\t\t['Trinidad and Tobago'],\t //\tTT\n" +
            "\t\t\t\t['Tunisia'],\t //\tTN\n" +
            "\t\t\t\t['Turkey'],\t //\tTR\n" +
            "\t\t\t\t['Turkmenistan'],\t //\tTM\n" +
            "\t\t\t\t['Turks and Caicos Islands'],\t //\tTC\n" +
            "\t\t\t\t['Tuvalu'],\t //\tTV\n" +
            "\t\t\t\t['Uganda'],\t //\tUG\n" +
            "\t\t\t\t['Ukraine'],\t //\tUA\n" +
            "\t\t\t\t['United Arab Emirates'],\t //\tAE\n" +
            "\t\t\t\t['United Kingdom of Great Britain and Northern Ireland'],\t //\tGB\n" +
            "\t\t\t\t['United States of America'],\t //\tUS\n" +
            "\t\t\t\t['United States Minor Outlying Islands'],\t //\tUM\n" +
            "\t\t\t\t['Uruguay'],\t //\tUY\n" +
            "\t\t\t\t['Uzbekistan'],\t //\tUZ\n" +
            "\t\t\t\t['Vanuatu'],\t //\tVU\n" +
            "\t\t\t\t['Venezuela (Bolivarian Republic of)'],\t //\tVE\n" +
            "\t\t\t\t['Viet Nam'],\t //\tVN\n" +
            "\t\t\t\t['Virgin Islands (British)'],\t //\tVG\n" +
            "\t\t\t\t['Virgin Islands (U.S.)'],\t //\tVI\n" +
            "\t\t\t\t['Wallis and Futuna'],\t //\tWF\n" +
            "\t\t\t\t['Western Sahara'],\t //\tEH\n" +
            "\t\t\t\t['Yemen'],\t //\tYE\n" +
            "\t\t\t\t['Zambia'],\t //\tZM\n" +
            "\t\t\t\t['Zimbabwe']\t //\tZW\n" +
            "\t\t\t\n" +
            "\t\t\t  ]);\n" +
            "\t\t\t\n" +
            "\t\t\t  var options = {\n" +
            "\t\t\t           colorAxis: {colors: ['#00853f', 'black', '#e31b23']},\n" +
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
            "\t\t<div id=\"geochart-colors\" style=\"width: 100%; height: 100%;\"></div>\n" +
            "\t</body>\n" +
            "</html>";
}
