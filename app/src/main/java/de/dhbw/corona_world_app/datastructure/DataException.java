package de.dhbw.corona_world_app.datastructure;

public class DataException extends Error{

    public DataException(){
        super("There seems to be a problem with your local data");
    }

    public DataException(String message){
        super(message);
    }
}
