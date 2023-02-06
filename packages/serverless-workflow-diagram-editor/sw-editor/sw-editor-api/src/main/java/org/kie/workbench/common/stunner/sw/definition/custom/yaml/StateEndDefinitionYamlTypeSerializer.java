package org.kie.workbench.common.stunner.sw.definition.custom.yaml;

import com.amihaiemil.eoyaml.Node;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLDeserializationException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.BooleanYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.BooleanYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.YAMLSerializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.YAMLSequenceWriter;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.YAMLWriter;
import org.kie.workbench.common.stunner.sw.definition.StateEnd;
import org.kie.workbench.common.stunner.sw.definition.StateEnd_YamlMapperImpl;

public class StateEndDefinitionYamlTypeSerializer implements YAMLDeserializer, YAMLSerializer {

    private static final StateEnd_YamlMapperImpl mapper =
            StateEnd_YamlMapperImpl.INSTANCE;

    private static final BooleanYAMLSerializer booleanYAMLSerializer = new BooleanYAMLSerializer();
    private static final BooleanYAMLDeserializer booleanYAMLDeserializer = new BooleanYAMLDeserializer();

    @Override
    public Object deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) throws YAMLDeserializationException {
        YamlNode node = yaml.value(key);
        if (node != null) {
            if(node.type() == Node.MAPPING) {
                return mapper.getDeserializer().deserialize(yaml, key, ctx);
            } else if(node.type() == Node.SCALAR) {
                return booleanYAMLDeserializer.deserialize(yaml, key, ctx);
            }
        }
        return null;
    }

    @Override
    public Object deserialize(YamlNode node, YAMLDeserializationContext ctx) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void serialize(YAMLWriter writer, String propertyName, Object value, YAMLSerializationContext ctx) {
        if (value instanceof Boolean) {
            booleanYAMLSerializer.serialize(writer, propertyName, (Boolean) value, ctx);
        } else if (value instanceof StateEnd) {
            mapper.getSerializer().serialize(writer, propertyName, (StateEnd) value, ctx);
        }
    }

    @Override
    public void serialize(YAMLSequenceWriter writer, Object value, YAMLSerializationContext ctx) {
        throw new UnsupportedOperationException("Unsupported serialization of " + value.getClass());
    }
}
