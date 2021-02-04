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
import de.dhbw.corona_world_app.datastructure.displayables.ISOCountry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Enum64BitEncoderTest {

    //needed in Order to generate falsely encoded Strings
    private static final char[] DIGITS = "0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmno".toCharArray();

    @Test
    public void encode_isCorrect(){
        encodeTest(ISOCountry.class);
        encodeTest(ChartType.class);
        encodeTest(Criteria.class);
    }

    @Test
    public void decode_isCorrect(){
        decodeTest(ISOCountry.class);
        decodeTest(ChartType.class);
        decodeTest(Criteria.class);
    }

    public <T extends Enum<T>> void encodeTest(Class<T> tClass) {
            Enum64BitEncoder<T> test = new Enum64BitEncoder<>(tClass);
            List<T> testData = Arrays.asList(tClass.getEnumConstants());
            List<String> result = test.encodeListOfEnums(testData);
            //list should have the same length
            assertEquals(testData.size(), result.size());
            //the length of the encoded String should at most have Math.ceil(Log64(Enum.values().length)) characters
            for (String s : result) {
                assertTrue(s.length()<=test.getMaxPossibleEncodedStringSize());
            }
            //list order should stay the same
            //decoding the encoded should give the original Dataset
            List<T> originalList = test.decodeListOfEnums(result);
            for (int i = 0; i < testData.size(); i++) {
                assertEquals(testData.get(i), originalList.get(i));
            }
    }

    public <T extends Enum<T>> void decodeTest(Class<T> tClass) {
        Enum64BitEncoder<T> test = new Enum64BitEncoder<>(tClass);
        //assuming encode is correct
        List<String> validTestData = test.encodeListOfEnums(Arrays.asList(tClass.getEnumConstants()));
        HashSet<String> validTestDataSet = new HashSet<>(validTestData);
        //create a List of Strings with invalid ISOCodes
        List<String> invalidTestData = new LinkedList<>();
        String invalidString;
        int maxNumberOfPossibleEncodedStrings = (int) Math.pow(64, test.getMaxPossibleEncodedStringSize());
        for (int i = 0; i < maxNumberOfPossibleEncodedStrings; ++i) {
            invalidString = encodeIntegerToString(i);
            if (!validTestDataSet.contains(invalidString)) {
                invalidTestData.add(invalidString);
            }
        }
        //check if valid TestData is decoded
        List<T> result = test.decodeListOfEnums(validTestData);
        //size should be the same
        assertEquals(validTestData.size(), result.size());
        //order should remain the same
        for (int i = 0; i < tClass.getEnumConstants().length; i++) {
            assertEquals(tClass.getEnumConstants()[i], result.get(i));
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

    private String encodeIntegerToString(int i) {
        if (i < 0) throw new IllegalArgumentException();
        char[] buf = new char[33];
        int charPos = 32;
        while (i >= 64) {
            buf[charPos--] = DIGITS[i % 64];
            i = i / 64;
        }
        buf[charPos] = DIGITS[i];
        return new String(buf, charPos, (33 - charPos));
    }
}
