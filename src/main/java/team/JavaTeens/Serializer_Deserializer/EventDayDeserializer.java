package team.JavaTeens.Serializer_Deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import team.JavaTeens.Server.EventDay;

import java.io.IOException;
import java.time.LocalDate;

public class EventDayDeserializer extends StdDeserializer<EventDay> {

    public EventDayDeserializer(){
        this(null);
    }
    public EventDayDeserializer(Class<EventDay> t){
        super(t);
    }

    @Override
    public EventDay deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        int year = (Integer) node.get("year").numberValue();
        int mouth = (Integer) node.get("mouth").numberValue();
        int day = (Integer) node.get("day").numberValue();

        String reason = node.get("reason").asText();

        return new EventDay(LocalDate.of(year,mouth,day), reason);
    }
}
