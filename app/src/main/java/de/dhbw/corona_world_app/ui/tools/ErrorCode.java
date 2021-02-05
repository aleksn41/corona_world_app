package de.dhbw.corona_world_app.ui.tools;

import org.jetbrains.annotations.NotNull;

//TODO if i where a user i would not know what to do with these error messages
public enum ErrorCode {
    UNEXPECTED_ERROR(0, "An unexpected error occurred."),
    DATA_CORRUPT(1, "Your data is corrupt, all corrupt files will be deleted."),
    CANNOT_READ_FILE(2, "Cannot access file."),
    CANNOT_SAVE_FILE(3, "Cannot write into file."),
    CANNOT_DELETE_FILE(4, "Could not delete file."),
    CANNOT_RESTORE_FILE(5, "No backup has been found, all corrupt files will be deleted."),
    CREATE_STATISTIC_FAILED(6, "An unexpected error occurred while creating the statistic."),
    NO_CONNECTION(7, "The Internet could not be accessed."),
    CONNECTION_TIMEOUT(8, "A timeout occurred while querying data. Please try again later."),
    UNEXPECTED_ANSWER(9,"The requested data has an unexpected format."),
    TOO_MANY_REQUESTS(10, "Too many requests were made, please retry querying in a few seconds."),
    NO_DATA_FOUND(11, "There was no data found. Please try reloading the map."),
    ;

    int code;
    String message;

    ErrorCode(int code, @NotNull String message) {
        this.code = code;
        this.message = message;
    }
}
