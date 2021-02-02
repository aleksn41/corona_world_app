package de.dhbw.corona_world_app.api;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.dhbw.corona_world_app.datastructure.GermanyState;
import de.dhbw.corona_world_app.datastructure.ISOCountry;

public class Mapper {

    private static Map<String, ISOCountry> herokuToStandardMap;

    private static Map<String, ISOCountry> restcountriesToStandardMap;

    private static Map<String, ISOCountry> isoCodeToISOCountryMap;

    private static Map<String, GermanyState> isoCodeToGermany;

    private static Map<API,Map<String,ISOCountry>> apisToMap;
    //for faster reverse mapping (so that the reverse map must not be initialized on every function call)
    private static Map<ISOCountry, String> reverseMap;
    //saves which api the reverse map is from
    private static API reverseMapAPI;

    private static List<String> blackList;

    private static boolean isAlreadyInitiated = false;

    private static void init(){
        if(!isAlreadyInitiated) {
            herokuToStandardMap = new HashMap<>();
            restcountriesToStandardMap = new HashMap<>();
            apisToMap = new HashMap<>();
            blackList = new LinkedList<>();
            apisToMap.put(API.HEROKU, herokuToStandardMap);
            apisToMap.put(API.RESTCOUNTRIES, restcountriesToStandardMap);
            reverseMapAPI = null;
            blackList.add("Channel_Islands");
            blackList.add("World");
            blackList.add("Diamond_Princess");
            blackList.add("Vatican_City");
            blackList.add("MS_Zaandam");
            isAlreadyInitiated = true;
        }
    }

    private static void initializeMap(API api){
        init();
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

    public static ISOCountry mapISOCodeToISOCountry(String isoCode){
        if(isoCode.length() != 2) throw new IllegalArgumentException("The given String is no ISOCode! Example:\"DE\"");
        if(isoCodeToISOCountryMap == null){
            isoCodeToISOCountryMap = new HashMap<>();
            for (ISOCountry country:ISOCountry.values()) {
                isoCodeToISOCountryMap.put(country.getISOCode(), country);
            }
        }
        return isoCodeToISOCountryMap.get(isoCode);
    }

    public static GermanyState mapISOCodeToGermanyState(String isoCode){
        if(isoCode.length() != 4) throw new IllegalArgumentException("The given String is no ISOCode! Example:\"DE\"");
        if(isoCodeToGermany == null){
            isoCodeToGermany = new HashMap<>();
            for (GermanyState state:GermanyState.values()) {
                isoCodeToGermany.put(state.getISOCode(), state);
            }
        }
        return isoCodeToGermany.get(isoCode);
    }

    public static boolean isInBlacklist(String countryName){
        init();
        return blackList.contains(countryName);
    }

    public static boolean isInMap(API api, String countryToBeChecked){
        initializeMap(api);
        return apisToMap.get(api).containsKey(countryToBeChecked);
    }

    public static boolean isInReverseMap(API api, ISOCountry countryToBeChecked){
        initReverseMap(api);
        return reverseMap.containsKey(countryToBeChecked);
    }

    private static void initReverseMap(API api){
        initializeMap(api);
        if(reverseMapAPI==null || !reverseMapAPI.equals(api)){
            reverseMap = getReverseMap(apisToMap.get(api));
            reverseMapAPI = api;
        }
    }

    public static String mapISOCountryToName(API api, ISOCountry countryToBeMapped){
        initReverseMap(api);
        Map<ISOCountry,String> reverseMap = getReverseMap(apisToMap.get(api));
        return denormalizeISOCountryName(reverseMap.get(countryToBeMapped));
    }

    public static ISOCountry mapNameToISOCountry(API api, String countryToBeMapped){
        initializeMap(api);
        return apisToMap.get(api).get(countryToBeMapped);
    }

    //normalizes a country name by removing commas and replacing spaces with underscores
    public static String normalizeCountryName(String countryName){
        return countryName.replace(",","").replace(" ","_");
    }

    //denormalizes an ISOCountry name by replacing all underscores with spaces
    public static String denormalizeISOCountryName(String isoCountryName){
        return isoCountryName.replace("_"," ");
    }

    private static <Ke, Va> Map<Va,Ke> getReverseMap(Map<Ke,Va> inMap){
        Map<Va,Ke> reverseMap = new HashMap<>();
        for(Map.Entry<Ke, Va> entry : inMap.entrySet()){
            reverseMap.put(entry.getValue(), entry.getKey());
        }
        return reverseMap;
    }
}
