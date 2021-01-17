package de.dhbw.corona_world_app.ui.tools;

import org.jetbrains.annotations.NotNull;

public enum ErrorCodes{
    DATA_CORRUPT(0,"Your Data is Corrupt, all corrupt Files will be deleted"),
    CANNOT_READ_FILE(1,"Cannot access File"),
    CANNOT_SAVE_FILE(2,"Cannot write into File"),
    CANNOT_RESTORE_FILE(3,"No Backup has been found");

    int code;
    String message;
    ErrorCodes(int code, @NotNull String message) {
        this.code=code;
        this.message=message;
    }
}
