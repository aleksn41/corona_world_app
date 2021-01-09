package de.dhbw.corona_world_app.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PostmanConverter implements APIDateTimeConverter {

    @Override
    public String convert(LocalDateTime from, LocalDateTime to) {
        return "?from="+from.format(DateTimeFormatter.ISO_INSTANT) +"?to="+ to.format(DateTimeFormatter.ISO_INSTANT);
    }

    @Override
    public String getFormat() {
        return "YYYY-MM-ddTHH:mm:ssZ";
    }

    @Override
    public API getAPI() {
        return API.POSTMANAPI;
    }
}
