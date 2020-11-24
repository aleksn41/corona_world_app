package de.dhbw.corona_world_app;

import org.junit.Test;

import de.dhbw.corona_world_app.api.APIManager;

import static org.junit.Assert.*;


public class APIManagerTests {

    @Test
    public void testMakeAPICall(){
        APIManager manager = new APIManager(true,false);
        manager.disableLogsForTesting();
        if(manager.createAPICall("https://google.de")!=null) {
            assertNotNull(manager.createAPICall("https://api.covid19api.com/summary"));
            System.out.println(manager.createAPICall("https://api.covid19api.com/summary"));
        } else {
            throw new RuntimeException("No Connection to the Internet found!");
        }
    }
}
