package team.JavaTeens.Serializer_Deserializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import team.JavaTeens.Account.EventDay;

import java.io.IOException;

public class EventDaySerializer extends StdSerializer<EventDay>{

    public EventDaySerializer(){
        this(null);
    }

    public EventDaySerializer(Class<EventDay> t) {
        super(t);
    }

    @Override
    public void serialize(EventDay eventDay, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumber(eventDay.getDate().getYear());
        jsonGenerator.writeNumber(eventDay.getDate().getMonthValue());
        jsonGenerator.writeNumber(eventDay.getDate().getDayOfMonth());
        jsonGenerator.writeEndObject();
    }
}
