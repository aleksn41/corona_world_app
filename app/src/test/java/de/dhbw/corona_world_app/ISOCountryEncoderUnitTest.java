package de.dhbw.corona_world_app;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

import de.dhbw.corona_world_app.datastructure.DataException;
import de.dhbw.corona_world_app.datastructure.ISOCountry;
import de.dhbw.corona_world_app.datastructure.ISOCountryEncoder;

public class ISOCountryEncoderUnitTest {

    @Test
    public void ISOCodeToISOCountry_isCorrect(){
        ISOCountryEncoder test= new ISOCountryEncoder();
        List<ISOCountry> testData=Arrays.asList(ISOCountry.values());
        //map entries must have same size as ISO Countries values
        assertEquals(test.isoCodeToCountry.size(),testData.size());
        //each ISOCode of each ISOCountry in ISOCountry values must have an entry in ISOCodeToISOCountry
        for (ISOCountry country : testData) {
            assertTrue(test.isoCodeToCountry.containsKey(country.getISOCode()));
        }
        //each value in ISOCodeToCountry must be in ISOCountry values
        for (ISOCountry value : test.isoCodeToCountry.values()) {
            assertTrue(testData.contains(value));
        }
    }

    @Test
    public void encode_isCorrect() {
        ISOCountryEncoder test = new ISOCountryEncoder();
        List<ISOCountry> testData = Arrays.asList(ISOCountry.values());
        List<String> result = test.encodeIsoCountries(testData);
        //list should have the same length
        assertEquals(testData.size(), result.size());

        //list order should stay the same
        //Encoder should have mapped every entry to its ISOCode
        for (int i = 0; i < testData.size(); i++) {
            assertEquals(testData.get(i).getISOCode(), result.get(i));
        }

        //decoding the encoded should give the original Dataset
        List<ISOCountry> originalList=test.decodeIsoCountries(result);
        for (int i = 0; i < testData.size(); i++) {
            assertEquals(testData.get(i),originalList.get(i));
        }
    }

    @Test
    public void decode_isCorrect() {
        ISOCountryEncoder test = new ISOCountryEncoder();
        //assuming encode is correct
        List<String> validTestData = test.encodeIsoCountries(Arrays.asList(ISOCountry.values()));
        HashSet<String> validTestDataSet=new HashSet<>(validTestData);
        //create a List of Strings with invalid ISOCodes
        List<String> invalidTestData = new LinkedList<>();
        StringBuilder invalidString = new StringBuilder();
        int maxNumberOfUpperLetterStringsWith2Chars = 26*26;
        for (int i = 0; i < maxNumberOfUpperLetterStringsWith2Chars; ++i) {
            invalidString.setLength(0);
            invalidString.append((char)((i%26)+(int)'A'));
            invalidString.append((char)((i/26)+(int)'A'));
            if(!validTestDataSet.contains(invalidString.toString())){
                invalidTestData.add(invalidString.toString());
            }
        }
        //check if valid TestData is decoded
        List<ISOCountry> result=test.decodeIsoCountries(validTestData);
        //size should be the same
        assertEquals(validTestData.size(),result.size());
        //order should remain the same
        for (int i = 0; i < ISOCountry.values().length; i++) {
            assertEquals(ISOCountry.values()[i],result.get(i));
        }
        //encoding the decoded Countries should lead to the original Dataset
        List<String> originalData=test.encodeIsoCountries(result);
        for (int i = 0; i < validTestData.size(); i++) {
            assertEquals(validTestData.get(i),originalData.get(i));
        }

        //checking if Invalid Data is recognized
        for (int i = 0; i < invalidTestData.size(); i++) {
            try{
                test.decodeIsoCountries(Collections.singletonList(invalidTestData.get(i)));
                fail();
            }catch (DataException ignored){

            }
        }
    }
}
