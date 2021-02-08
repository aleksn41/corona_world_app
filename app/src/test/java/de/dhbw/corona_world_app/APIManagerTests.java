package de.dhbw.corona_world_app;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.api.API;
import de.dhbw.corona_world_app.api.TooManyRequestsException;
import de.dhbw.corona_world_app.api.UnavailableException;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.TimeFramedCountry;
import de.dhbw.corona_world_app.datastructure.displayables.GermanyState;
import de.dhbw.corona_world_app.datastructure.displayables.ISOCountry;

import static org.junit.Assert.*;

public class APIManagerTests {

    @Before
    public void getAPIManger() {
        APIManager.setSettings(false);
    }

    @Test
    public void testMakeAPICall() throws IOException {
        APIManager.disableLogsForTesting();
        APIManager.createAPICall("https://google.de");
        String returnString = APIManager.createAPICall("https://api.covid19api.com/summary");
        assertNotNull(returnString);
        System.out.println(returnString);
    }

    @Test
    public void testGetDataOneCountry() throws Throwable {
        APIManager.disableLogsForTesting();
        APIManager.createAPICall("https://google.de");
        List<ISOCountry> clist = new ArrayList<>();
        clist.add(ISOCountry.Germany);
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.DEATHS);
        criteriaList.add(Criteria.INFECTED);
        criteriaList.add(Criteria.RECOVERED);
        criteriaList.add(Criteria.POPULATION);
        List<Country<ISOCountry>> returnList = APIManager.getData(clist, criteriaList);
        assertNotNull(returnList);
        System.out.println(returnList);
    }

    @Test
    public void testGetDataOneCountryTimeFrame() throws Throwable {
        APIManager.disableLogsForTesting();
        APIManager.createAPICall("https://google.de");
        List<ISOCountry> clist = new ArrayList<>();
        clist.add(ISOCountry.Germany);
        clist.add(ISOCountry.Democratic_Republic_Congo);
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.DEATHS);
        criteriaList.add(Criteria.INFECTED);
        criteriaList.add(Criteria.RECOVERED);
        criteriaList.add(Criteria.POPULATION);
        List<TimeFramedCountry> returnList = APIManager.getData(clist, criteriaList, LocalDate.of(2020, 6, 3), LocalDate.of(2020, 6, 3));
        assertNotNull(returnList);
        System.out.println(returnList);
    }

    @Test
    public void testGetDataTenCountries() throws Throwable {
        APIManager.disableLogsForTesting();
        APIManager.createAPICall("https://google.de");
        List<ISOCountry> clist = new ArrayList<>();
        clist.add(ISOCountry.Germany);
        clist.add(ISOCountry.France);
        clist.add(ISOCountry.United_States_of_America);
        clist.add(ISOCountry.Chile);
        clist.add(ISOCountry.Greece);
        clist.add(ISOCountry.Belize);
        clist.add(ISOCountry.Martinique);
        clist.add(ISOCountry.C_te_d_Ivoire);
        clist.add(ISOCountry.Sao_Tome_and_Principe);
        clist.add(ISOCountry.Democratic_Republic_Congo);
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.DEATHS);
        criteriaList.add(Criteria.INFECTED);
        criteriaList.add(Criteria.RECOVERED);
        criteriaList.add(Criteria.POPULATION);
        List<Country<ISOCountry>> returnList = APIManager.getData(clist, criteriaList);
        assertNotNull(returnList);
        System.out.println(returnList);
    }

    @Test
    public void testGetDataWorld() throws Throwable {
        APIManager.disableLogsForTesting();
        APIManager.createAPICall("https://google.de");
        List<Country<ISOCountry>> returnList = APIManager.getDataWorld(API.HEROKU);
        assertNotNull(returnList);
        System.out.println(returnList);
    }

    @Test
    public void testGetAllCountriesPopData() throws Throwable {
        APIManager.disableLogsForTesting();
        APIManager.createAPICall("https://google.de");
        Map<ISOCountry, Long> returnMap = APIManager.getAllCountriesPopData();
        assertNotNull(returnMap);
        System.out.println(returnMap);
    }

    @Test
    public void testGetDataGermany() throws Throwable {
        APIManager.disableLogsForTesting();
        List<Country<GermanyState>> countries = APIManager.getDataGermany(API.ARCGIS);
        assertNotNull(countries);
        System.out.println(countries);
    }

    //Heads up! This may take a while as it makes 10 sec pauses every 10 requests.
    public void testAPIAllGetEveryCountry() throws InterruptedException, TooManyRequestsException, ExecutionException, UnavailableException {
        APIManager.disableLogsForTesting();
        List<ISOCountry> countryList = Arrays.asList(ISOCountry.values());
        List<ISOCountry> blackList = new ArrayList<>();
        blackList.add(ISOCountry.World);
        blackList.add(ISOCountry.Aland_Islands);
        blackList.add(ISOCountry.American_Samoa);
        blackList.add(ISOCountry.Antarctica);
        blackList.add(ISOCountry.Anguilla);
        blackList.add(ISOCountry.Aruba);
        blackList.add(ISOCountry.Bermuda);
        blackList.add(ISOCountry.Bonaire_Sint_Eustatius_and_Saba);
        blackList.add(ISOCountry.Bouvet_Island);
        blackList.add(ISOCountry.British_Indian_Ocean_Territory);
        blackList.add(ISOCountry.Cura_ao);
        blackList.add(ISOCountry.Christmas_Island);
        blackList.add(ISOCountry.Cocos);
        blackList.add(ISOCountry.Cook_Islands);
        blackList.add(ISOCountry.Falkland_Islands);
        blackList.add(ISOCountry.Faroe_Islands);
        blackList.add(ISOCountry.French_Guiana);
        blackList.add(ISOCountry.French_Polynesia);
        blackList.add(ISOCountry.French_Southern_Territories);
        blackList.add(ISOCountry.Guam);
        blackList.add(ISOCountry.Gibraltar);
        blackList.add(ISOCountry.Greenland);
        blackList.add(ISOCountry.Guadeloupe);
        blackList.add(ISOCountry.Guernsey);
        blackList.add(ISOCountry.Hong_Kong);
        blackList.add(ISOCountry.Cayman_Islands);
        blackList.add(ISOCountry.Heard_Island_and_McDonald_Islands);
        blackList.add(ISOCountry.Holy_See);
        blackList.add(ISOCountry.Isle_of_Man);
        blackList.add(ISOCountry.Jersey);
        blackList.add(ISOCountry.Kiribati);
        blackList.add(ISOCountry.Macao);
        blackList.add(ISOCountry.Martinique);
        blackList.add(ISOCountry.Mayotte);
        blackList.add(ISOCountry.Montserrat);
        blackList.add(ISOCountry.New_Caledonia);
        blackList.add(ISOCountry.R_union);
        blackList.add(ISOCountry.Saint_Barth_lemy);
        blackList.add(ISOCountry.Saint_Martin);
        blackList.add(ISOCountry.Saint_Pierre_and_Miquelon);
        blackList.add(ISOCountry.Sint_Maarten);
        blackList.add(ISOCountry.Turks_and_Caicos_Islands);
        blackList.add(ISOCountry.British_Virgin_Islands);
        blackList.add(ISOCountry.Wallis_and_Futuna);
        blackList.add(ISOCountry.Western_Sahara);
        blackList.add(ISOCountry.North_Korea);
        blackList.add(ISOCountry.Nauru);
        blackList.add(ISOCountry.Niue);
        blackList.add(ISOCountry.Norfolk_Island);
        blackList.add(ISOCountry.Northern_Mariana_Islands);
        blackList.add(ISOCountry.Palau);
        blackList.add(ISOCountry.Pitcairn);
        blackList.add(ISOCountry.Puerto_Rico);
        blackList.add(ISOCountry.Saint_Helena_Ascension_and_Tristan_da_Cunha);
        blackList.add(ISOCountry.South_Georgia_and_the_South_Sandwich_Islands);
        blackList.add(ISOCountry.Svalbard_and_Jan_Mayen);
        blackList.add(ISOCountry.Tokelau);
        blackList.add(ISOCountry.Tonga);
        blackList.add(ISOCountry.Turkmenistan);
        blackList.add(ISOCountry.Tuvalu);
        blackList.add(ISOCountry.United_States_Minor_Outlying_Islands);
        blackList.add(ISOCountry.US_Virgin_Islands);
        blackList.add(ISOCountry.Republic_of_Kosovo);
        countryList = countryList.stream().filter(c -> !blackList.contains(c)).collect(Collectors.toList());
        int cnt = 0;
        int countriesNotAvailable = 0;
        for (ISOCountry country: countryList) {
            try {
                List<TimeFramedCountry> timeList = APIManager.getData(Collections.singletonList(country), Collections.singletonList(Criteria.INFECTED), LocalDate.now().minusDays(2), null);
                if(timeList.get(0).getCountry() == null || timeList.get(0).getInfected().length < 2){
                    System.out.println(country.toString());
                    countriesNotAvailable++;
                }
            } catch (JSONException e){
                e.printStackTrace();
                System.out.println(country.toString());
                countriesNotAvailable++;
            }
            //needed so that the api doesn't block the requests
            if(cnt++%10==0) Thread.sleep(5000);
        }
        assertEquals(0, countriesNotAvailable);
        System.out.println("Count of countries not available = "+countriesNotAvailable);
        Thread.sleep(10000);
    }
}
