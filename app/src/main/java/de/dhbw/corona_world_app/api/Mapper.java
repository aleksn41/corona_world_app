package de.dhbw.corona_world_app.api;

import java.util.HashMap;
import java.util.Map;

import de.dhbw.corona_world_app.datastructure.ISOCountry;

public class Mapper {

    private Map<String, ISOCountry> herokuToStandardMap;

    private Map<String, ISOCountry> restcountriesToStandardMap;

    private Map<APIs,Map<String,ISOCountry>> apisToMap;

    public Mapper(){
        herokuToStandardMap = new HashMap<>();
        restcountriesToStandardMap = new HashMap<>();
        apisToMap = new HashMap<>();
        apisToMap.put(APIs.HEROKU,herokuToStandardMap);
        apisToMap.put(APIs.RESTCOUNTRIES,restcountriesToStandardMap);
    }

    public void initializeMap(APIs api){
        if(!apisToMap.get(api).isEmpty()) {
            switch (api) {
                case HEROKU:
                    herokuToStandardMap.put("USA", ISOCountry.United_States_of_America);
                    break;
                case RESTCOUNTRIES:
                    restcountriesToStandardMap.put("Åland Islands", ISOCountry.Aland_Islands);
                    restcountriesToStandardMap.put("Bolivia (Plurinational State of)", ISOCountry.Bolivia);
                    restcountriesToStandardMap.put("Cocos (Keeling) Islands",ISOCountry.Cocos);
                    restcountriesToStandardMap.put("Congo (Democratic Republic of the)",ISOCountry.Democratic_Republic_Congo);
                    restcountriesToStandardMap.put("Curaçao",ISOCountry.Cura_ao);
                    restcountriesToStandardMap.put("Czech Republic",ISOCountry.Czechia);
                    restcountriesToStandardMap.put("Falkland Islands (Malvinas)",ISOCountry.Falkland_Islands);
                    restcountriesToStandardMap.put("Virgin Islands (British)",ISOCountry.British_Virgin_Islands);
                    restcountriesToStandardMap.put("Virgin Islands (U.S.)",ISOCountry.US_Virgin_Islands);
                    restcountriesToStandardMap.put("Guinea-Bissau",ISOCountry.Guinea_Bissau);
                    restcountriesToStandardMap.put("Côte d'Ivoire",ISOCountry.C_te_d_Ivoire);
                    restcountriesToStandardMap.put("Iran (Islamic Republic of)",ISOCountry.Iran);
                    restcountriesToStandardMap.put("Lao People's Democratic Republic",ISOCountry.Lao_People_s_Democratic_Republic);
                    restcountriesToStandardMap.put("Macedonia (the former Yugoslav Republic of)",ISOCountry.Macedonia);
                    restcountriesToStandardMap.put("Micronesia (Federated States of)",ISOCountry.Micronesia);
                    restcountriesToStandardMap.put("Moldova (Republic of)",ISOCountry.Moldova);
                    restcountriesToStandardMap.put("Korea (Democratic People's Republic of)",ISOCountry.North_Korea);
                    restcountriesToStandardMap.put("Réunion",ISOCountry.R_union);
                    restcountriesToStandardMap.put("Saint Barthélemy",ISOCountry.Saint_Barth_lemy);
                    restcountriesToStandardMap.put("Saint Martin (French part)",ISOCountry.Saint_Martin);
                    restcountriesToStandardMap.put("Sint Maarten (Dutch part)",ISOCountry.Sint_Maarten);
                    restcountriesToStandardMap.put("Korea (Republic of)",ISOCountry.South_Korea);
                    restcountriesToStandardMap.put("Taiwan",ISOCountry.Taiwan_Province_of_China);
                    restcountriesToStandardMap.put("Timor-Leste",ISOCountry.Timor_Leste);
                    restcountriesToStandardMap.put("Venezuela (Bolivarian Republic of)",ISOCountry.Venezuela);
                    break;
            }
        }
    }

    public boolean isInMap(APIs api, String countryToBeChecked){
        initializeMap(api);
        return apisToMap.get(api).containsKey(countryToBeChecked);
    }

    public ISOCountry mapNameToISOCountry(APIs api, String countryToBeMapped){
        ISOCountry country;
        initializeMap(api);
        country = apisToMap.get(api).get(countryToBeMapped);
        return country;
    }
}
