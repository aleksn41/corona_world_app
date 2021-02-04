package de.dhbw.corona_world_app.datastructure;

import java.io.Serializable;

public class Country<T extends Displayable> implements Serializable {

    private T name;

    private int deaths;

    private int infected;

    private int recovered;

    private int active;

    private long population;

    private double pop_inf_ratio;

    public Country() {
    }

    public long getHealthy() {
        return this.population - this.infected;
    }

    public double getPop_inf_ratio() {
        return pop_inf_ratio;
    }

    public void setPop_inf_ratio(double pop_inf_ratio) {
        this.pop_inf_ratio = pop_inf_ratio;
    }

    public Country(T name) {
        this.name = name;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getInfected() {
        return infected;
    }

    public void setInfected(int infected) {
        this.infected = infected;
    }

    public int getRecovered() {
        return recovered;
    }

    public void setRecovered(int recovered) {
        this.recovered = recovered;
    }

    public long getPopulation() {
        return population;
    }

    public void setPopulation(long population) {
        this.population = population;
        this.pop_inf_ratio = (double) infected / population;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public T getName() {
        return name;
    }

    public void setName(T name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Country{" +
                "name='" + name.toString() + '\'' +
                ", deaths=" + deaths +
                ", infected=" + infected +
                ", recovered=" + recovered +
                ", population=" + population +
                ", pop_inf_ratio=" + pop_inf_ratio +
                '}';
    }
}
