package de.dhbw.corona_world_app;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.dhbw.corona_world_app.datastructure.ChartType;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.DataException;
import de.dhbw.corona_world_app.datastructure.Enum64BitEncoder;
import de.dhbw.corona_world_app.datastructure.ISOCountry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Enum64BitEncoderTest {

    @Test
    public void enumToEncodedStringForISOCountry_isCorrect() {
        Enum64BitEncoder<ISOCountry> test = new Enum64BitEncoder<>(ISOCountry.class);
        List<ISOCountry> testData = Arrays.asList(ISOCountry.values());
        //map entries must have same size as ISO Countries values
        assertEquals(test.enumToEncodedString.size(), testData.size());
        //each ISOCountry in ISOCountry values must have an entry in enumToEncodedString
        for (ISOCountry country : testData) {
            assertTrue(test.enumToEncodedString.containsKey(country));
        }
    }

    @Test
    public void encodeISOCountry_isCorrect() {
        Enum64BitEncoder<ISOCountry> test = new Enum64BitEncoder<>(ISOCountry.class);
        List<ISOCountry> testData = Arrays.asList(ISOCountry.values());
        List<String> result = test.encodeListOfEnums(testData);
        //list should have the same length
        assertEquals(testData.size(), result.size());
        //list order should stay the same
        //Encoder should have mapped every entry to its encodedString
        for (int i = 0; i < testData.size(); i++) {
            assertEquals(test.enumToEncodedString.get(testData.get(i)), result.get(i));
        }

        //decoding the encoded should give the original Dataset
        List<ISOCountry> originalList = test.decodeListOfEnums(result);
        for (int i = 0; i < testData.size(); i++) {
            assertEquals(testData.get(i), originalList.get(i));
        }
    }

    @Test
    public void decodeISOCountry_isCorrect() {
        Enum64BitEncoder<ISOCountry> test = new Enum64BitEncoder<>(ISOCountry.class);
        //assuming encode is correct
        List<String> validTestData = test.encodeListOfEnums(Arrays.asList(ISOCountry.values()));
        HashSet<String> validTestDataSet = new HashSet<>(validTestData);
        //create a List of Strings with invalid ISOCodes
        List<String> invalidTestData = new LinkedList<>();
        String invalidString;
        int maxNumberOfPossibleEncodedStrings = (int) Math.pow(Enum64BitEncoder.BITS, Math.ceil(Math.log(validTestData.size()) / Math.log(Enum64BitEncoder.BITS)));
        for (int i = 0; i < maxNumberOfPossibleEncodedStrings; ++i) {
            invalidString = test.encodeIntegerToString(i);
            if (!validTestDataSet.contains(invalidString)) {
                invalidTestData.add(invalidString);
            }
        }
        //check if valid TestData is decoded
        List<ISOCountry> result = test.decodeListOfEnums(validTestData);
        //size should be the same
        assertEquals(validTestData.size(), result.size());
        //order should remain the same
        for (int i = 0; i < ISOCountry.values().length; i++) {
            assertEquals(ISOCountry.values()[i], result.get(i));
        }
        //encoding the decoded Countries should lead to the original Dataset
        List<String> originalData = test.encodeListOfEnums(result);
        for (int i = 0; i < validTestData.size(); i++) {
            assertEquals(validTestData.get(i), originalData.get(i));
        }

        //checking if Invalid Data is recognized
        for (int i = 0; i < invalidTestData.size(); i++) {
            try {
                test.decodeListOfEnums(Collections.singletonList(invalidTestData.get(i)));
                fail();
            } catch (DataException ignored) {

            }
        }
    }

    @Test
    public void enumToEncodedStringForCriteria_isCorrect() {
        Enum64BitEncoder<Criteria> test = new Enum64BitEncoder<>(Criteria.class);
        List<Criteria> testData = Arrays.asList(Criteria.values());
        //map entries must have same size as Criteria values
        assertEquals(test.enumToEncodedString.size(), testData.size());
        //each Criteria in Criteria values must have an entry in enumToEncodedString
        for (Criteria criteria : testData) {
            assertTrue(test.enumToEncodedString.containsKey(criteria));
        }
    }

    @Test
    public void encodeCriteria_isCorrect() {
        Enum64BitEncoder<Criteria> test = new Enum64BitEncoder<>(Criteria.class);
        List<Criteria> testData = Arrays.asList(Criteria.values());
        List<String> result = test.encodeListOfEnums(testData);
        //list should have the same length
        assertEquals(testData.size(), result.size());
        //list order should stay the same
        //Encoder should have mapped every entry to its encodedString
        for (int i = 0; i < testData.size(); i++) {
            assertEquals(test.enumToEncodedString.get(testData.get(i)), result.get(i));
        }

        //decoding the encoded should give the original Dataset
        List<Criteria> originalList = test.decodeListOfEnums(result);
        for (int i = 0; i < testData.size(); i++) {
            assertEquals(testData.get(i), originalList.get(i));
        }
    }

    @Test
    public void decodeCriteria_isCorrect() {
        Enum64BitEncoder<Criteria> test = new Enum64BitEncoder<>(Criteria.class);
        //assuming encode is correct
        List<String> validTestData = test.encodeListOfEnums(Arrays.asList(Criteria.values()));
        HashSet<String> validTestDataSet = new HashSet<>(validTestData);
        //create a List of Strings with invalid Criteria
        List<String> invalidTestData = new LinkedList<>();
        String invalidString;
        int maxNumberOfPossibleEncodedStrings = (int) Math.pow(Enum64BitEncoder.BITS, Math.ceil(Math.log(validTestData.size()) / Math.log(Enum64BitEncoder.BITS)));
        for (int i = 0; i < maxNumberOfPossibleEncodedStrings; ++i) {
            invalidString = test.encodeIntegerToString(i);
            if (!validTestDataSet.contains(invalidString)) {
                invalidTestData.add(invalidString);
            }
        }
        //check if valid TestData is decoded
        List<Criteria> result = test.decodeListOfEnums(validTestData);
        //size should be the same
        assertEquals(validTestData.size(), result.size());
        //order should remain the same
        for (int i = 0; i < Criteria.values().length; i++) {
            assertEquals(Criteria.values()[i], result.get(i));
        }
        //encoding the decoded Criteria should lead to the original Dataset
        List<String> originalData = test.encodeListOfEnums(result);
        for (int i = 0; i < validTestData.size(); i++) {
            assertEquals(validTestData.get(i), originalData.get(i));
        }

        //checking if Invalid Data is recognized
        for (int i = 0; i < invalidTestData.size(); i++) {
            try {
                test.decodeListOfEnums(Collections.singletonList(invalidTestData.get(i)));
                fail();
            } catch (DataException ignored) {

            }
        }
    }

    @Test
    public void enumToEncodedStringForChartType_isCorrect() {
        Enum64BitEncoder<ChartType> test = new Enum64BitEncoder<>(ChartType.class);
        List<ChartType> testData = Arrays.asList(ChartType.values());
        //map entries must have same size as ChartType values
        assertEquals(test.enumToEncodedString.size(), testData.size());
        //each ChartType in ChartType values must have an entry in enumToEncodedString
        for (ChartType chartType : testData) {
            assertTrue(test.enumToEncodedString.containsKey(chartType));
        }
    }

    @Test
    public void encodeChartType_isCorrect() {
        Enum64BitEncoder<ChartType> test = new Enum64BitEncoder<>(ChartType.class);
        List<ChartType> testData = Arrays.asList(ChartType.values());
        List<String> result = test.encodeListOfEnums(testData);
        //list should have the same length
        assertEquals(testData.size(), result.size());
        //list order should stay the same
        //Encoder should have mapped every entry to its encodedString
        for (int i = 0; i < testData.size(); i++) {
            assertEquals(test.enumToEncodedString.get(testData.get(i)), result.get(i));
        }

        //decoding the encoded should give the original Dataset
        List<ChartType> originalList = test.decodeListOfEnums(result);
        for (int i = 0; i < testData.size(); i++) {
            assertEquals(testData.get(i), originalList.get(i));
        }
    }

    @Test
    public void decodeChartType_isCorrect() {
        Enum64BitEncoder<ChartType> test = new Enum64BitEncoder<>(ChartType.class);
        //assuming encode is correct
        List<String> validTestData = test.encodeListOfEnums(Arrays.asList(ChartType.values()));
        HashSet<String> validTestDataSet = new HashSet<>(validTestData);
        //create a List of Strings with invalid ChartType
        List<String> invalidTestData = new LinkedList<>();
        String invalidString;
        int maxNumberOfPossibleEncodedStrings = (int) Math.pow(Enum64BitEncoder.BITS, Math.ceil(Math.log(validTestData.size()) / Math.log(Enum64BitEncoder.BITS)));
        for (int i = 0; i < maxNumberOfPossibleEncodedStrings; ++i) {
            invalidString = test.encodeIntegerToString(i);
            if (!validTestDataSet.contains(invalidString)) {
                invalidTestData.add(invalidString);
            }
        }
        //check if valid TestData is decoded
        List<ChartType> result = test.decodeListOfEnums(validTestData);
        //size should be the same
        assertEquals(validTestData.size(), result.size());
        //order should remain the same
        for (int i = 0; i < ChartType.values().length; i++) {
            assertEquals(ChartType.values()[i], result.get(i));
        }
        //encoding the decoded ChartType should lead to the original Dataset
        List<String> originalData = test.encodeListOfEnums(result);
        for (int i = 0; i < validTestData.size(); i++) {
            assertEquals(validTestData.get(i), originalData.get(i));
        }

        //checking if Invalid Data is recognized
        for (int i = 0; i < invalidTestData.size(); i++) {
            try {
                test.decodeListOfEnums(Collections.singletonList(invalidTestData.get(i)));
                fail();
            } catch (DataException ignored) {

            }
        }
    }
}
