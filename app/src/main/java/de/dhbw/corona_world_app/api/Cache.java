package de.dhbw.corona_world_app.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.datastructure.StatisticCall;

public class Cache {

    private static LocalDateTime lastTimeAccessedLifeDataWorld;

    private static List<Country> cachedWorldData;
    //Country is just a placeholder for the return-class of the according statistic-service
    private static Map<StatisticCall, Country> cachedStatisticsData;

    public static void init(){
        if(cachedWorldData==null || cachedStatisticsData==null) {
            cachedWorldData = new ArrayList<>();
            cachedStatisticsData = new HashMap<>();
        }
    }

    public static Map<StatisticCall, Country> getCachedStatisticsData() {
        return cachedStatisticsData;
    }

    public static void setCachedStatisticsData(Map<StatisticCall, Country> cachedStatisticsData) {
        init();
        Cache.cachedStatisticsData = cachedStatisticsData;
    }

    public static List<Country> getCachedDataList() {
        return cachedWorldData;
    }

    public static void setCachedDataList(List<Country> cachedDataList) {
        init();
        Cache.cachedWorldData = cachedDataList;
    }

    public static LocalDateTime getLastTimeAccessedLifeDataWorld() {
        return lastTimeAccessedLifeDataWorld;
    }

    public static void setLastTimeAccessedLifeDataWorldToNow() {
        init();
        Cache.lastTimeAccessedLifeDataWorld = LocalDateTime.now();
    }
}
