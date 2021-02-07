package de.dhbw.corona_world_app.datastructure;

/**
 * @author Aleksandr Stankoski
 */
public class DataException extends Error{

    public DataException(){
        super("There seems to be a problem with your local data");
    }

    /**
     * Indicates that there is something wrong with the Data
     * @param message the message that should be displayed
     */
    public DataException(String message){
        super(message);
    }
}
