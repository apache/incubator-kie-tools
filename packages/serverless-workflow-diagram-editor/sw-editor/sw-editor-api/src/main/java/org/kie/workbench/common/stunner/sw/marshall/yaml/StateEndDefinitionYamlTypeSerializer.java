package org.kie.workbench.common.stunner.sw.marshall.yaml;

import org.kie.workbench.common.stunner.sw.definition.StateEnd;
import org.kie.workbench.common.stunner.sw.definition.StateEnd_YamlMapperImpl;
import org.treblereel.gwt.yaml.api.YAMLDeserializer;
import org.treblereel.gwt.yaml.api.YAMLSerializer;
import org.treblereel.gwt.yaml.api.exception.YAMLDeserializationException;
import org.treblereel.gwt.yaml.api.internal.deser.BooleanYAMLDeserializer;
import org.treblereel.gwt.yaml.api.internal.deser.YAMLDeserializationContext;
import org.treblereel.gwt.yaml.api.internal.ser.BooleanYAMLSerializer;
import org.treblereel.gwt.yaml.api.internal.ser.YAMLSerializationContext;
import org.treblereel.gwt.yaml.api.node.NodeType;
import org.treblereel.gwt.yaml.api.node.YamlMapping;
import org.treblereel.gwt.yaml.api.node.YamlNode;
import org.treblereel.gwt.yaml.api.node.YamlSequence;

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
