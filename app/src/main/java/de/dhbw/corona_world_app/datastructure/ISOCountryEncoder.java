package de.dhbw.corona_world_app.datastructure;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ISOCountryEncoder {
    public Map<String,ISOCountry> isoCodeToCountry;

    public ISOCountryEncoder() {
        init();
    }

    private void init(){
        isoCodeToCountry=Arrays.stream(ISOCountry.values()).parallel().collect(Collectors.toConcurrentMap(ISOCountry::getISOCode, p->p));
    }

    public List<String> encodeIsoCountries(List<ISOCountry> isoCountries){
        return isoCountries.parallelStream().map(ISOCountry::getISOCode).collect(Collectors.toList());
    }

    public List<ISOCountry> decodeIsoCountries(List<String> isoCountryCodes) throws DataException{
        return isoCountryCodes.parallelStream().map(s -> {
            if(!isoCodeToCountry.containsKey(s))throw new DataException("IsoCode in List is invalid, cannot read: "+s);
            return isoCodeToCountry.get(s);
        }).collect(Collectors.toList());
    }

}
