package de.dhbw.corona_world_app.datastructure;

/**
 * @author Aleksandr Stankoski
 */
public class DataException extends Error{

    /**
     * Indicates that there is something wrong with the Data
     * @param message the message that should be displayed
     */
    public DataException(String message){
        super(message);
    }
}
