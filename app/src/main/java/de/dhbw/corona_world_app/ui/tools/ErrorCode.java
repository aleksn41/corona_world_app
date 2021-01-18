package de.dhbw.corona_world_app.ui.tools;

import org.jetbrains.annotations.NotNull;
//TODO if i where a user i would not know what to do with these error messages
public enum ErrorCode {
    UNEXPECTED_ERROR(0,"An Unexpected Error occurred"),
    DATA_CORRUPT(1,"Your Data is Corrupt, all corrupt Files will be deleted"),
    CANNOT_READ_FILE(2,"Cannot access File"),
    CANNOT_SAVE_FILE(3,"Cannot write into File"),
    CANNOT_DELETE_FILE(4,"Cannot access File"),
    CANNOT_RESTORE_FILE(5,"No Backup has been found, all corrupt Files will be deleted");

    int code;
    String message;
    ErrorCode(int code, @NotNull String message) {
        this.code=code;
        this.message=message;
    }
}
