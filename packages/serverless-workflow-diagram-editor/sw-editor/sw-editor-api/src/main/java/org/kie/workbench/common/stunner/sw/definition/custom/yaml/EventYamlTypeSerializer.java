package org.kie.workbench.common.stunner.sw.definition.custom.yaml;

import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLDeserializationException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.StringYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.array.ArrayYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.StringYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.YAMLSerializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.array.ArrayYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.YAMLSequenceWriter;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.YAMLWriter;
import org.kie.workbench.common.stunner.sw.definition.Event;
import org.kie.workbench.common.stunner.sw.definition.Event_YamlMapperImpl;

import static com.amihaiemil.eoyaml.Node.SCALAR;
import static com.amihaiemil.eoyaml.Node.SEQUENCE;

public class EventYamlTypeSerializer implements YAMLDeserializer, YAMLSerializer {

    private static final Event_YamlMapperImpl mapper = Event_YamlMapperImpl.INSTANCE;
    private static final StringYAMLSerializer stringYAMLSerializer = new StringYAMLSerializer();
    private static final StringYAMLDeserializer stringYAMLDeserializer = new StringYAMLDeserializer();

    @Override
    public Object deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) throws YAMLDeserializationException {
        if(yaml == null || yaml.isEmpty() || yaml.value(key) == null) {
            return null;
        }
        return deserialize(yaml.value(key), ctx);
    }

    @Override
    public Object deserialize(YamlNode node, YAMLDeserializationContext ctx) {
        if(node.type() == SCALAR) {
            return stringYAMLDeserializer.deserialize(node, ctx);
        } else if (node.type() == SEQUENCE) {
            return ArrayYAMLDeserializer.newInstance(mapper.getDeserializer(), Event[]::new).deserialize(node, ctx);
        }
        return null;
    }

    @Override
    public void serialize(YAMLWriter writer, String propertyName, Object obj, YAMLSerializationContext ctx) {
        if (obj instanceof String) {
            stringYAMLSerializer.serialize(writer, propertyName, (String) obj, ctx);
        } else if (obj instanceof Event[]) {
            new ArrayYAMLSerializer<>(mapper.getSerializer())
                    .serialize(writer, propertyName, (Event[]) obj,
                            ctx);
        }
    }

    @Override
    public void serialize(YAMLSequenceWriter writer, Object value, YAMLSerializationContext ctx) {
        throw new RuntimeException("Not implemented");
    }
}
