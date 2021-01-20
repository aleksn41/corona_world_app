package de.dhbw.corona_world_app;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.api.API;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.ISOCountry;

import static org.junit.Assert.*;

public class APIManagerTests {

    @Before
    public void getAPIManger(){
        APIManager.setSettings(false, false);
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
        List<Country> returnList = APIManager.getData(clist,criteriaList,null);
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
        List<Country> returnList = APIManager.getData(clist,criteriaList,null);
        assertNotNull(returnList);
        System.out.println(returnList);
    }

    @Test
    public void testGetDataWorld() throws Throwable {
        APIManager.disableLogsForTesting();
        APIManager.createAPICall("https://google.de");
        List<Country> returnList = APIManager.getDataWorld(API.HEROKU);
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
}
