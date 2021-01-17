package de.dhbw.corona_world_app.datastructure;

import java.io.Serializable;
import java.time.LocalDate;

public class TimeframedCountry implements Serializable {

    private ISOCountry country;

    private LocalDate[] dates;

    private int[] deaths;

    private int[] infected;

    private int[] recovered;

    private long population;

    private double[] pop_inf_ratio;

    public TimeframedCountry(){
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
        return (double) getInfected()[i]/getPopulation();
    }

    public void setPop_inf_ratio(double[] pop_inf_ratio) {
        this.pop_inf_ratio = pop_inf_ratio;
    }
}
