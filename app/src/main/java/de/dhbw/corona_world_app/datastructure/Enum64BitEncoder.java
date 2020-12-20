package de.dhbw.corona_world_app.datastructure;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Enum64BitEncoder<T extends Enum<T>> {
    private final T[] enumConstants;
    public Map<T, String> enumToEncodedString;

    public static final int BITS = 64;
    //choosing these Digits as they are always on index apart in ascii, which makes decoding faster
    public static final char[] DIGITS = "0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmno".toCharArray();

    public Enum64BitEncoder(Class<T> enumClass) {
        enumConstants = enumClass.getEnumConstants();
        init();
    }

    //creating map that maps an enum to an encoded String, needed for performance improvement
    private void init() {
        enumToEncodedString = IntStream.range(0, enumConstants.length).parallel().boxed().collect(Collectors.toConcurrentMap(i -> enumConstants[i], this::encodeIntegerToString));
    }

    public List<String> encodeListOfEnums(List<T> enumList) {
        return enumList.parallelStream().map(e -> enumToEncodedString.get(e)).collect(Collectors.toList());
    }

    public List<T> decodeListOfEnums(List<String> encodedStringsList) throws DataException {
        return encodedStringsList.parallelStream().map(s ->
        {
            int decodedInt = decodeEncodedStringToInt(s);
            if (decodedInt >= enumConstants.length)
                throw new DataException("cannot decode String: " + s + ", value too high");
            return enumConstants[decodeEncodedStringToInt(s)];
        }).collect(Collectors.toList());
    }

    //based on Integer.toString (is not used as Character.MAX_RADIX<64)
    //only used for ints>=0
    public String encodeIntegerToString(int i) {
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

    public int decodeEncodedStringToInt(String s) throws DataException {
        int res = 0;
        for (int i = 0; i < s.length()-1; i++) {
            if (s.charAt(i) < DIGITS[0] || s.charAt(i) > DIGITS[BITS - 1])
                throw new DataException("encoded String has character not in base");
            res += s.charAt(i) - DIGITS[0];
            res *= BITS;
        }
        return res+s.charAt(s.length()-1)-DIGITS[0];
    }
}
