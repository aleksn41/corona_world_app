package de.dhbw.corona_world_app.datastructure;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;

public class TimeframedCountry implements Serializable, Comparable<TimeframedCountry> {

    private ISOCountry country;

    private LocalDate[] dates;

    private int[] deaths;

    private int[] infected;

    private int[] recovered;

    private long population;

    private double[] pop_inf_ratio;

    public TimeframedCountry() {
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
    public int compareTo(TimeframedCountry o) {
        int infectedAvg = 0;
        int deathsAvg = 0;
        int recoveredAvg = 0;
        if (((TimeframedCountry) o).getInfected().length == this.infected.length && ((TimeframedCountry) o).getDeaths().length == this.deaths.length && ((TimeframedCountry) o).getRecovered().length == this.recovered.length && ((TimeframedCountry) o).getDates().length == this.dates.length) {
            if (((TimeframedCountry) o).getInfected().length > 1) {
                for (int i = 0; i < ((TimeframedCountry) o).getInfected().length; i++) {
                    infectedAvg += ((TimeframedCountry) o).getInfected()[i] - this.infected[i];
                }
                infectedAvg = infectedAvg / ((TimeframedCountry) o).getInfected().length;
            }
            if (((TimeframedCountry) o).getDeaths().length > 1) {
                for (int i = 0; i < ((TimeframedCountry) o).getDeaths().length; i++) {
                    deathsAvg += ((TimeframedCountry) o).getDeaths()[i] - this.deaths[i];
                }
                deathsAvg = deathsAvg / ((TimeframedCountry) o).getDeaths().length;
            }
            if (((TimeframedCountry) o).getRecovered().length > 1) {
                for (int i = 0; i < ((TimeframedCountry) o).getRecovered().length; i++) {
                    recoveredAvg += ((TimeframedCountry) o).getRecovered()[i] - this.recovered[i];
                }
                recoveredAvg = recoveredAvg / ((TimeframedCountry) o).getRecovered().length;
            }
            return infectedAvg * 10 + deathsAvg * 5 + recoveredAvg;
        } else {
            throw new IllegalArgumentException("The input TimeframedCountry is of different length than this object!");
        }
    }
}
