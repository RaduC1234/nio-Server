package team.JavaTeens.Utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import team.JavaTeens.Account.CalendarEvent;

import java.io.IOException;
import java.time.LocalDate;

public class CalendarEventDeserializer extends StdDeserializer<CalendarEvent> {

    public CalendarEventDeserializer(){
        this(null);
    }
    public CalendarEventDeserializer(Class<CalendarEvent> t){
        super(t);
    }

    @Override
    public CalendarEvent deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        int year = (Integer) node.get("year").numberValue();
        int mouth = (Integer) node.get("mouth").numberValue();
        int day = (Integer) node.get("day").numberValue();

        String description = node.get("description").asText();

        return new CalendarEvent(LocalDate.of(year,mouth,day), description);
    }
}
