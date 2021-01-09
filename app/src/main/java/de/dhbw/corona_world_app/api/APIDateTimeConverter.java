package de.dhbw.corona_world_app.api;

import java.time.LocalDateTime;

public interface APIDateTimeConverter {

    public String convert(LocalDateTime from, LocalDateTime to);

    public String getFormat();

    public API getAPI();
}
