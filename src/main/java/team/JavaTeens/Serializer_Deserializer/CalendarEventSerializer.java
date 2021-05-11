package team.JavaTeens.Serializer_Deserializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import team.JavaTeens.Account.CalendarEvent;

import java.io.IOException;

public class CalendarEventSerializer extends StdSerializer<CalendarEvent>{

    public CalendarEventSerializer(){
        this(null);
    }

    public CalendarEventSerializer(Class<CalendarEvent> t) {
        super(t);
    }

    @Override
    public void serialize(CalendarEvent calendarEvent, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("year",calendarEvent.getDate().getYear());
        jsonGenerator.writeNumberField("mouth",calendarEvent.getDate().getMonthValue());
        jsonGenerator.writeNumberField("day",calendarEvent.getDate().getDayOfMonth());
        jsonGenerator.writeStringField("description",calendarEvent.getDescription());
        jsonGenerator.writeEndObject();
    }
}
