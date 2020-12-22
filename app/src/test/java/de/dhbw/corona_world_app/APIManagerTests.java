package de.dhbw.corona_world_app;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.api.API;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.ISOCountry;

import static org.junit.Assert.*;


public class APIManagerTests {

    @Test
    public void testMakeAPICall() throws IOException {
        APIManager manager = new APIManager(true,false);
        manager.disableLogsForTesting();
        if(manager.createAPICall("https://google.de")!=null) {
            assertNotNull(manager.createAPICall("https://api.covid19api.com/summary"));
            System.out.println(manager.createAPICall("https://api.covid19api.com/summary"));
        } else {
            throw new RuntimeException("No Connection to the Internet found!");
        }
    }

    @Test
    public void testGetDataOneCountry() throws IOException {
        APIManager manager = new APIManager(true,false);
        manager.disableLogsForTesting();
        if(manager.createAPICall("https://google.de")!=null) {
            List<ISOCountry> clist = new ArrayList<>();
            clist.add(ISOCountry.Germany);
            List<Criteria> criteriaList = new ArrayList<>();
            criteriaList.add(Criteria.DEATHS);
            criteriaList.add(Criteria.INFECTED);
            criteriaList.add(Criteria.RECOVERED);
            criteriaList.add(Criteria.POPULATION);
            assertNotNull(manager.getData(clist,criteriaList,null));
            System.out.println(manager.getData(clist,criteriaList,null));
        } else {
            throw new RuntimeException("No Connection to the Internet found!");
        }
    }

    @Test
    public void testGetDataFiveCountries() throws IOException {
        APIManager manager = new APIManager(true,false);
        manager.disableLogsForTesting();
        if(manager.createAPICall("https://google.de")!=null) {
            List<ISOCountry> clist = new ArrayList<>();
            clist.add(ISOCountry.Germany);
            clist.add(ISOCountry.France);
            clist.add(ISOCountry.United_States_of_America);
            clist.add(ISOCountry.Chile);
            clist.add(ISOCountry.Greece);
            List<Criteria> criteriaList = new ArrayList<>();
            criteriaList.add(Criteria.DEATHS);
            criteriaList.add(Criteria.INFECTED);
            criteriaList.add(Criteria.RECOVERED);
            criteriaList.add(Criteria.POPULATION);
            assertNotNull(manager.getData(clist,criteriaList,null));
            System.out.println(manager.getData(clist,criteriaList,null));
        } else {
            throw new RuntimeException("No Connection to the Internet found!");
        }
    }

    @Test
    public void testGetDataWorld() throws IOException {
        APIManager manager = new APIManager(true,false);
        manager.disableLogsForTesting();
        if(manager.createAPICall("https://google.de")!=null) {
            assertNotNull(manager.getDataWorld(API.HEROKU));
            System.out.println(manager.getDataWorld(API.HEROKU));
        } else {
            throw new RuntimeException("No Connection to the Internet found!");
        }
    }

    @Test
    public void testGetAllCountriesPopData() throws IOException {
        APIManager manager = new APIManager(true, false);
        manager.disableLogsForTesting();
        if(manager.createAPICall("https://google.de")!=null){
            assertNotNull(manager.getAllCountriesPopData());
            System.out.println(manager.getAllCountriesPopData());
        }
    }
}
