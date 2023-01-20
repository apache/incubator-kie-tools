package org.kie.workbench.common.stunner.sw.definition.custom;

import java.lang.reflect.Type;

import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.StringJsonDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.StringJsonSerializer;
import org.kie.workbench.common.stunner.sw.definition.SubflowExecutionType;


public class SubflowExecutionTypeJsonbTypeSerializer implements JsonbDeserializer<SubflowExecutionType>, JsonbSerializer<SubflowExecutionType> {
    private static final StringJsonSerializer stringJsonSerializer = new StringJsonSerializer();

    private static final StringJsonDeserializer stringJsonDeserializer = new StringJsonDeserializer();

    @Override
    public void serialize(SubflowExecutionType obj, JsonGenerator generator, SerializationContext ctx) {
        stringJsonSerializer.serialize((obj == SubflowExecutionType.TERMINATE ? "terminate" : "continue"), generator, ctx);
    }

    @Override
    public SubflowExecutionType deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        JsonValue value = parser.getValue();
        if (value != null) {
            if (value.getValueType() != JsonValue.ValueType.NULL) {
                if (value.getValueType() == JsonValue.ValueType.STRING) {
                    String v = stringJsonDeserializer.deserialize(value, ctx);
                    return v.equals("terminate") ? SubflowExecutionType.TERMINATE : SubflowExecutionType.CONTINUE;
                }
            }
        }
        return null;
    }
}
