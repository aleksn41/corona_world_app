package de.dhbw.corona_world_app.statistic;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.datastructure.TimeFramedCountry;

/**
 * This class is used to cache a statistic call it's according data and creation time.
 *
 * @author Thomas Meier
 */
public class StatisticCacheObject implements Serializable {

    LocalDateTime creationTime;

    StatisticCall statisticCall;

    List<TimeFramedCountry> data;

    public StatisticCacheObject(LocalDateTime creationTime, StatisticCall statisticCall, List<TimeFramedCountry> data) {
        this.creationTime = creationTime;
        this.statisticCall = statisticCall;
        this.data = data;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public StatisticCall getStatisticCall() {
        return statisticCall;
    }

    public List<TimeFramedCountry> getData() {
        return data;
    }
}
