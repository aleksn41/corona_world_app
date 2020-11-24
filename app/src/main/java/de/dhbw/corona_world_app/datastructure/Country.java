package de.dhbw.corona_world_app.datastructure;

public class Country {

    private String name;

    private int deaths;

    private int infected;

    private int recovered;

    private long population;

    public Country(){

    }

    public Country(String name) {
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
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Country [name=" + name + ", deaths=" + deaths + ", infected=" + infected + ", recovered=" + recovered
                + ", population=" + population + "]";
    }
}
