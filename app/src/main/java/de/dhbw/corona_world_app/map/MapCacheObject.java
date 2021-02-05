package de.dhbw.corona_world_app.map;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.datastructure.Displayable;

public class MapCacheObject<T extends Displayable> implements Serializable {

    private final LocalDateTime creationTime;

    private final List<Country<T>> dataList;

    public MapCacheObject(LocalDateTime creationTime, List<Country<T>> dataList) {
        this.creationTime = creationTime;
        this.dataList = dataList;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public List<Country<T>> getDataList() {
        return dataList;
    }
}
