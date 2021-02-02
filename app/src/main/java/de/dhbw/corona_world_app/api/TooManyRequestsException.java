package de.dhbw.corona_world_app.api;

public class TooManyRequestsException extends Exception {

    public TooManyRequestsException(String s) {
        super(s);
    }
}
