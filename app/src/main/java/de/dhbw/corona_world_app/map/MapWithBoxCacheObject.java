package de.dhbw.corona_world_app.map;

import java.time.LocalDateTime;
import java.util.List;

import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.datastructure.Displayable;
import de.dhbw.corona_world_app.datastructure.displayables.ISOCountry;

public class MapWithBoxCacheObject<T extends Displayable, R extends Displayable> extends MapCacheObject<T> {

    private final Country<R> mapBoxValue;

    public MapWithBoxCacheObject(LocalDateTime creationTime, List<Country<T>> dataList, Country<R> mapBoxValue) {
        super(creationTime, dataList);
        this.mapBoxValue = mapBoxValue;
    }

    public Country<R> getMapBoxValue() {
        return mapBoxValue;
    }
}
