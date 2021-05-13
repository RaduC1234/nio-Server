package team.JavaTeens.Account;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import team.JavaTeens.Utils.CalendarEventDeserializer;
import team.JavaTeens.Utils.CalendarEventSerializer;

import java.time.LocalDate;

@JsonSerialize(using = CalendarEventSerializer.class)
@JsonDeserialize(using = CalendarEventDeserializer.class)
public class CalendarEvent {

    private LocalDate date;
    private String description;

    public CalendarEvent(LocalDate date, String description) {
        this.date = date;
        this.description = description;
    }
    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}