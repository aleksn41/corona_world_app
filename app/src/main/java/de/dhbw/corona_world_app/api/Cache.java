package de.dhbw.corona_world_app.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.datastructure.StatisticCall;

public class Cache {

    private static LocalDateTime lastTimeAccessedLifeDataWorld;

    private static List<Country> cachedWorldData;
    //Country is just a placeholder for the return-class of the according statistic-service
    private static Map<StatisticCall, Country> cachedStatisticsData;

    public static Map<StatisticCall, Country> getCachedStatisticsData() {
        return cachedStatisticsData;
    }

    public static void setCachedStatisticsData(StatisticCall statisticCall, Country country) {
        if(cachedStatisticsData==null) {
            cachedStatisticsData = new HashMap<>();
        }
        Cache.cachedStatisticsData.put(statisticCall, country);
    }

    public static List<Country> getCachedDataList() {
        return cachedWorldData;
    }

    public static void setCachedDataList(List<Country> cachedDataList) {
        Cache.cachedWorldData = cachedDataList;
        setLastTimeAccessedLifeDataWorldToNow();
    }

    public static LocalDateTime getLastTimeAccessedLifeDataWorld() {
        return lastTimeAccessedLifeDataWorld;
    }

    private static void setLastTimeAccessedLifeDataWorldToNow() {
        Cache.lastTimeAccessedLifeDataWorld = LocalDateTime.now();
    }
}
