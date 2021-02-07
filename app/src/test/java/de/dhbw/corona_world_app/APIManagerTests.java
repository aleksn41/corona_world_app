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

    @Test
    public void testAPIAllGetEveryCountry() throws InterruptedException, TooManyRequestsException, ExecutionException, UnavailableException, JSONException {
        APIManager.disableLogsForTesting();
        ISOCountry[] countryArray = ISOCountry.values();
        int cnt = 0;
        int countriesNotAvailable = 0;
        for (ISOCountry country: countryArray) {
            List<TimeFramedCountry> timeList = APIManager.getData(Collections.singletonList(country), Collections.singletonList(Criteria.INFECTED), null, null);
            if(timeList.get(0).getCountry() == null){
                System.out.println(country.toString());
                countriesNotAvailable++;
            }
            if(cnt++%10==0) Thread.sleep(1500);
        }
        System.out.println("Count of countries not available = "+countriesNotAvailable);
    }
}
