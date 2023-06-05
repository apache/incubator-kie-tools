package org.kie.workbench.common.stunner.sw.definition.custom.yaml;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLDeserializationException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.BooleanYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.BooleanYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.YAMLSerializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.NodeType;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlNode;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlSequence;
import org.kie.workbench.common.stunner.sw.definition.StateEnd;
import org.kie.workbench.common.stunner.sw.definition.StateEnd_YamlMapperImpl;

public class StateEndDefinitionYamlTypeSerializer implements YAMLDeserializer, YAMLSerializer {

    private static final StateEnd_YamlMapperImpl mapper =
            StateEnd_YamlMapperImpl.INSTANCE;

    private static final BooleanYAMLSerializer booleanYAMLSerializer = new BooleanYAMLSerializer();
    private static final BooleanYAMLDeserializer booleanYAMLDeserializer = new BooleanYAMLDeserializer();

    @Override
    public Object deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) throws YAMLDeserializationException {
        YamlNode node = yaml.getNode(key);
        return deserialize(node, ctx);
    }

    @Override
    public Object deserialize(YamlNode node, YAMLDeserializationContext ctx) {
        if (node != null) {
            if (node.type() == NodeType.MAPPING) {
                return mapper.getDeserializer().deserialize(node, ctx);
            } else if (node.type() == NodeType.SCALAR) {
                return booleanYAMLDeserializer.deserialize(node, ctx);
            }
        }
        return null;
    }

    @Override
    public void serialize(YamlMapping writer, String propertyName, Object value, YAMLSerializationContext ctx) {
        if (value instanceof Boolean) {
            booleanYAMLSerializer.serialize(writer, propertyName, (Boolean) value, ctx);
        } else if (value instanceof StateEnd) {
            mapper.getSerializer().serialize(writer, propertyName, (StateEnd) value, ctx);
        }
    }

    @Override
    public void serialize(YamlSequence writer, Object value, YAMLSerializationContext ctx) {
        throw new UnsupportedOperationException("Unsupported serialization of " + value.getClass());
    }
}
