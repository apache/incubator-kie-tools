package org.kie.workbench.common.stunner.sw.definition.custom;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.StringJsonSerializer;
import org.kie.workbench.common.stunner.sw.definition.SubflowExecutionType;

public class SubflowExecutionTypeJsonbTypeSerializer implements JsonbSerializer<SubflowExecutionType> {
    private static final StringJsonSerializer stringJsonSerializer = new StringJsonSerializer();

    @Override
    public void serialize(SubflowExecutionType obj, JsonGenerator generator, SerializationContext ctx) {
        stringJsonSerializer.serialize((obj == SubflowExecutionType.TERMINATE ? "terminate" : "continue"), "onParentComplete", generator, ctx);
    }
}
