package org.drools.guvnor.server.gson;


import com.google.gson.*;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A default type adapter for a {@link java.util.Date} object.<br>
 * Create a GSON instance that can serialize/deserialize "java.util.Date" objects:
 * <pre>
 * Gson gson = new GsonBuilder()
 * .registerTypeAdapter(new DateTypeAdapter())
 * .create();
 * </pre>
 */
public class SQLDateTypeAdapter implements JsonSerializer<Timestamp>, JsonDeserializer<Date> {
    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public JsonElement serialize(java.sql.Timestamp src, Type typeOfSrc, JsonSerializationContext context) {
        String dateFormatAsString = format.format(src);
        return new JsonPrimitive(dateFormatAsString);
    }

    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("The date should be a string value");
        }

        try {
            return format.parse(json.getAsString());
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }

    }
}
