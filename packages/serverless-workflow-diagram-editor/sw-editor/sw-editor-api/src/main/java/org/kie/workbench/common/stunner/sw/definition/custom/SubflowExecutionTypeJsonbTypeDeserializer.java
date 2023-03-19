package org.kie.workbench.common.stunner.sw.definition.custom;

import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.DeserializationContext;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.JsonbDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.StringJsonDeserializer;
import org.kie.workbench.common.stunner.sw.definition.SubflowExecutionType;

public class SubflowExecutionTypeJsonbTypeDeserializer extends JsonbDeserializer<SubflowExecutionType> {

    private static final StringJsonDeserializer stringJsonDeserializer = new StringJsonDeserializer();

    @Override
    public SubflowExecutionType deserialize(JsonValue value, DeserializationContext ctx) {
        if (value.getValueType() != JsonValue.ValueType.NULL) {
            if (value.getValueType() == JsonValue.ValueType.STRING) {
                String v = stringJsonDeserializer.deserialize(value, ctx);
                return v.equals("terminate") ? SubflowExecutionType.TERMINATE : SubflowExecutionType.CONTINUE;
            }
        }
        return null;
    }
}
