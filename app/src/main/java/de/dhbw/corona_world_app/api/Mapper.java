package de.dhbw.corona_world_app.api;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.dhbw.corona_world_app.datastructure.ISOCountry;

public class Mapper {

    private Map<String, ISOCountry> herokuToStandardMap;

    private Map<String, ISOCountry> restcountriesToStandardMap;

    private Map<APIs,Map<String,ISOCountry>> apisToMap;
    //for faster reverse mapping (so that the reverse map must not be initialized every on every function call)
    private Map<ISOCountry, String> reverseMap;
    //saves which api the reverse map is from
    private APIs reverseMapAPI;

    private List<String> blackList;

    public Mapper(){
        herokuToStandardMap = new HashMap<>();
        restcountriesToStandardMap = new HashMap<>();
        apisToMap = new HashMap<>();
        blackList = new LinkedList<>();
        apisToMap.put(APIs.HEROKU,herokuToStandardMap);
        apisToMap.put(APIs.RESTCOUNTRIES,restcountriesToStandardMap);
        initializeMap(APIs.HEROKU);
        reverseMap = getReverseMap(herokuToStandardMap);
        reverseMapAPI = APIs.HEROKU;
        blackList.add("Republic of Kosovo");
        blackList.add("Channel_Islands");
        blackList.add("World");
        blackList.add("Diamond_Princess");
        blackList.add("Vatican_City");
        blackList.add("MS_Zaandam");
    }

    public void initializeMap(APIs api){
        if(apisToMap.get(api).isEmpty()) {
            switch (api) {
                case HEROKU:
                    herokuToStandardMap.put("USA", ISOCountry.United_States_of_America);
                    herokuToStandardMap.put("Russia",ISOCountry.Russian_Federation);
                    herokuToStandardMap.put("UK",ISOCountry.United_Kingdom_of_Great_Britain_and_Northern_Ireland);
                    herokuToStandardMap.put("UAE",ISOCountry.United_Arab_Emirates);
                    herokuToStandardMap.put("Palestine",ISOCountry.Palestine_State_of);
                    herokuToStandardMap.put("North_Macedonia",ISOCountry.Macedonia);
                    herokuToStandardMap.put("S._Korea",ISOCountry.South_Korea);
                    herokuToStandardMap.put("Ivory_Coast",ISOCountry.C_te_d_Ivoire);
                    herokuToStandardMap.put("DRC",ISOCountry.Democratic_Republic_Congo);
                    herokuToStandardMap.put("Syria",ISOCountry.Syrian_Arab_Republic);
                    herokuToStandardMap.put("Réunion",ISOCountry.R_union);
                    herokuToStandardMap.put("Eswatini",ISOCountry.Swaziland);
                    herokuToStandardMap.put("CAR",ISOCountry.Central_African_Republic);
                    herokuToStandardMap.put("Curaçao",ISOCountry.Cura_ao);
                    herokuToStandardMap.put("Guinea-Bissau",ISOCountry.Guinea_Bissau);
                    herokuToStandardMap.put("Vietnam",ISOCountry.Viet_Nam);
                    herokuToStandardMap.put("Turks_and_Caicos",ISOCountry.Turks_and_Caicos_Islands);
                    herokuToStandardMap.put("Taiwan",ISOCountry.Taiwan_Province_of_China);
                    herokuToStandardMap.put("Faeroe_Islands",ISOCountry.Faroe_Islands);
                    herokuToStandardMap.put("Tanzania",ISOCountry.Tanzania_United_Republic_of);
                    herokuToStandardMap.put("Caribbean_Netherlands",ISOCountry.Bonaire_Sint_Eustatius_and_Saba);
                    herokuToStandardMap.put("St._Barth",ISOCountry.Saint_Barth_lemy);
                    herokuToStandardMap.put("Brunei",ISOCountry.Brunei_Darussalam);
                    herokuToStandardMap.put("St._Vincent_Grenadines",ISOCountry.Saint_Vincent_and_the_Grenadines);
                    herokuToStandardMap.put("Laos",ISOCountry.Lao_People_s_Democratic_Republic);
                    herokuToStandardMap.put("Timor-Leste",ISOCountry.Timor_Leste);
                    herokuToStandardMap.put("Saint_Pierre_Miquelon", ISOCountry.Saint_Pierre_and_Miquelon);
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

    public boolean isInBlacklist(String countryName){
        return blackList.contains(countryName);
    }

    public boolean isInMap(APIs api, String countryToBeChecked){
        initializeMap(api);
        return apisToMap.get(api).containsKey(countryToBeChecked);
    }

    public boolean isInReverseMap(APIs api, ISOCountry countryToBeChecked){
        initReverseMap(api);
        return reverseMap.containsKey(countryToBeChecked);
    }

    private void initReverseMap(APIs api){
        if(apisToMap.get(api).isEmpty()){
            initializeMap(api);
        }
        if(!reverseMapAPI.equals(api)){
            reverseMap = getReverseMap(apisToMap.get(api));
            reverseMapAPI = api;
        }
    }

    public String mapISOCountryToName(APIs api, ISOCountry countryToBeMapped){
        Map<ISOCountry,String> reverseMap = getReverseMap(apisToMap.get(api));
        return reverseMap.get(countryToBeMapped);
    }

    public ISOCountry mapNameToISOCountry(APIs api, String countryToBeMapped){
        ISOCountry country;
        initializeMap(api);
        country = apisToMap.get(api).get(countryToBeMapped);
        return country;
    }

    //normalizes a country name by removing commas and replacing spaces with underscores
    public String normalize(String countryName){
        return countryName.replace(",","").replace(" ","_");
    }

    private <Ke, Va> Map<Va,Ke> getReverseMap(Map<Ke,Va> inMap){
        Map<Va,Ke> reverseMap = new HashMap<>();
        for(Map.Entry<Ke, Va> entry : inMap.entrySet()){
            reverseMap.put(entry.getValue(), entry.getKey());
        }
        return reverseMap;
    }
}
