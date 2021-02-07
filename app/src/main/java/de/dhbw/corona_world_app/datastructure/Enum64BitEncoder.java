package de.dhbw.corona_world_app.datastructure;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This Class is used to encode/decode Enums using 64 Digits (Base64)
 * @param <T> the Enum Type that is supposed to be encoded/decoded
 * @author Aleksandr Stankoski
 */
public class Enum64BitEncoder<T extends Enum<T>> {
    private final T[] enumConstants;
    private Map<T, String> enumToEncodedString;

    private static final int BITS = 64;
    //choosing these Digits as they are always one index apart in ascii, which makes decoding faster
    private static final char[] DIGITS = "0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmno".toCharArray();

    public Enum64BitEncoder(Class<T> enumClass) {
        enumConstants = enumClass.getEnumConstants();
        init();
    }

    //creating map that maps an enum to an encoded String, needed for performance improvement
    private void init() {
        enumToEncodedString = IntStream.range(0, enumConstants.length).parallel().boxed().collect(Collectors.toConcurrentMap(i -> enumConstants[i], this::encodeIntegerToString));
    }

    /**
     * Encodes every Enum in the List to an encoded String
     * @param enumList the List of enums to encode
     * @return {@link List<String>} A List of equal size containing the encoded strings in the same order as the enumList
     */
    public List<String> encodeListOfEnums(List<T> enumList) {
        return enumList.parallelStream().map(e -> enumToEncodedString.get(e)).collect(Collectors.toList());
    }

    /**
     * Decodes every String in the List to the corresponding Enum
     * @param encodedStringsList the List of Strings to decode
     * @return {@link List<T>} A List of equal size containing the decoded enums in the same order as the encodedStringsList
     * @throws DataException if any String in the List cannot be decoded, as its invalid
     */
    public List<T> decodeListOfEnums(List<String> encodedStringsList) throws DataException {
        return encodedStringsList.parallelStream().map(s ->
        {
            int decodedInt = decodeEncodedStringToInt(s);
            if (decodedInt >= enumConstants.length)
                throw new DataException("cannot decode String: " + s + ", value too high");
            return enumConstants[decodedInt];
        }).collect(Collectors.toList());
    }

    //based on Integer.toString (is not used since Character.MAX_RADIX<64)
    //only used for ints>=0
    private String encodeIntegerToString(int i) {
        if (i < 0) throw new IllegalArgumentException();
        char[] buf = new char[33];
        int charPos = 32;
        while (i >= BITS) {
            buf[charPos--] = DIGITS[i % BITS];
            i = i / BITS;
        }
        buf[charPos] = DIGITS[i];
        return new String(buf, charPos, (33 - charPos));
    }

    private int decodeEncodedStringToInt(String s) throws DataException {
        if(s.length()==0)throw new DataException("cannot decode empty String");
        int res = 0;
        for (int i = 0; i < s.length()-1; i++) {
            if (s.charAt(i) < DIGITS[0] || s.charAt(i) > DIGITS[BITS - 1])
                throw new DataException("encoded String has character not in base: "+s.charAt(i));
            res += s.charAt(i) - DIGITS[0];
            res *= BITS;
        }
        return res+s.charAt(s.length()-1)-DIGITS[0];
    }

    public int getMaxPossibleEncodedStringSize(){
        return (int)Math.ceil(Math.log(enumConstants.length)/Math.log(BITS));
    }
}
