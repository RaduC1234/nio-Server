package team.JavaTeens.Server;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import team.JavaTeens.Serializer_Deserializer.EventDayDeserializer;
import team.JavaTeens.Serializer_Deserializer.EventDaySerializer;

import java.time.LocalDate;

@JsonSerialize(using = EventDaySerializer.class)
@JsonDeserialize(using = EventDayDeserializer.class)
public class EventDay {

    private LocalDate date;
    private String reason;

    public EventDay(LocalDate date, String reason) {
        this.date = date;
        this.reason = reason;
    }
    public LocalDate getDate() {
        return date;
    }

    public String getReason() {
        return reason;
    }
}