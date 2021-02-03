package de.dhbw.corona_world_app.datastructure;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;

import de.dhbw.corona_world_app.datastructure.displayables.ISOCountry;

public class TimeFramedCountry implements Serializable, Comparable<TimeFramedCountry> {

    private ISOCountry country;

    private LocalDate[] dates;

    private int[] deaths;

    private int[] infected;

    private int[] recovered;

    private int[] active;

    private long population;

    private double[] pop_inf_ratio;

    public TimeFramedCountry() {
    }

    public ISOCountry getCountry() {
        return country;
    }

    public void setCountry(ISOCountry country) {
        this.country = country;
    }

    public LocalDate[] getDates() {
        return dates;
    }

    public void setDates(LocalDate[] dates) {
        this.dates = dates;
    }

    public int[] getDeaths() {
        return deaths;
    }

    public void setDeaths(int[] deaths) {
        this.deaths = deaths;
    }

    public int[] getInfected() {
        return infected;
    }

    public void setInfected(int[] infected) {
        this.infected = infected;
    }

    public int[] getRecovered() {
        return recovered;
    }

    public void setRecovered(int[] recovered) {
        this.recovered = recovered;
    }

    public long getPopulation() {
        return population;
    }

    public void setPopulation(long population) {
        this.population = population;
        for (int i = 0; i < infected.length; i++) {
            this.pop_inf_ratio[i] = (double) infected[i] / population;
        }
    }

    public double getPop_inf_ratio(int i) {
        return (double) getInfected()[i] / getPopulation();
    }

    public void setPop_inf_ratio(double[] pop_inf_ratio) {
        this.pop_inf_ratio = pop_inf_ratio;
    }

    public int[] getActive() {
        return active;
    }

    public void setActive(int[] active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "TimeframedCountry{" +
                "country=" + country +
                ", dates=" + Arrays.toString(dates) +
                ", deaths=" + Arrays.toString(deaths) +
                ", infected=" + Arrays.toString(infected) +
                ", recovered=" + Arrays.toString(recovered) +
                ", population=" + population +
                ", pop_inf_ratio=" + Arrays.toString(pop_inf_ratio) +
                '}';
    }

    @Override
    public int compareTo(TimeFramedCountry o) {
        int infectedAvg = 0;
        int deathsAvg = 0;
        int recoveredAvg = 0;
        if (o.getInfected().length == this.infected.length && o.getDeaths().length == this.deaths.length && o.getRecovered().length == this.recovered.length && o.getDates().length == this.dates.length) {
            if (o.getInfected().length > 1) {
                for (int i = 0; i < o.getInfected().length; i++) {
                    infectedAvg += o.getInfected()[i] - this.infected[i];
                }
                infectedAvg = infectedAvg / o.getInfected().length;
            }
            if (o.getDeaths().length > 1) {
                for (int i = 0; i < o.getDeaths().length; i++) {
                    deathsAvg += o.getDeaths()[i] - this.deaths[i];
                }
                deathsAvg = deathsAvg / o.getDeaths().length;
            }
            if (o.getRecovered().length > 1) {
                for (int i = 0; i < o.getRecovered().length; i++) {
                    recoveredAvg += o.getRecovered()[i] - this.recovered[i];
                }
                recoveredAvg = recoveredAvg / o.getRecovered().length;
            }
            return infectedAvg * 10 + deathsAvg * 5 + recoveredAvg;
        } else {
            throw new IllegalArgumentException("The input TimeFramedCountry is of different length than this object!");
        }
    }
}
