package org.kie.workbench.common.stunner.sw.marshall.yaml;

import org.kie.workbench.common.stunner.sw.definition.WorkflowExecTimeout;
import org.kie.workbench.common.stunner.sw.definition.WorkflowExecTimeout_YamlMapperImpl;
import org.treblereel.gwt.yaml.api.YAMLDeserializer;
import org.treblereel.gwt.yaml.api.YAMLSerializer;
import org.treblereel.gwt.yaml.api.exception.YAMLDeserializationException;
import org.treblereel.gwt.yaml.api.internal.deser.StringYAMLDeserializer;
import org.treblereel.gwt.yaml.api.internal.deser.YAMLDeserializationContext;
import org.treblereel.gwt.yaml.api.internal.ser.StringYAMLSerializer;
import org.treblereel.gwt.yaml.api.internal.ser.YAMLSerializationContext;
import org.treblereel.gwt.yaml.api.node.NodeType;
import org.treblereel.gwt.yaml.api.node.YamlMapping;
import org.treblereel.gwt.yaml.api.node.YamlNode;
import org.treblereel.gwt.yaml.api.node.YamlSequence;

public class WorkflowExecTimeoutYamlSerializer implements YAMLDeserializer, YAMLSerializer {

    private static final WorkflowExecTimeout_YamlMapperImpl mapper =
            WorkflowExecTimeout_YamlMapperImpl.INSTANCE;

    private static final StringYAMLSerializer stringYAMLSerializer = new StringYAMLSerializer();
    private static final StringYAMLDeserializer stringYAMLDeserializer = new StringYAMLDeserializer();


    @Override
    public Object deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) throws YAMLDeserializationException {
        YamlNode value = yaml.getNode(key);
        if (value == null) {
            return null;
        }
        return deserialize(value, ctx);
    }

    @Override
    public Object deserialize(YamlNode node, YAMLDeserializationContext ctx) {
        if (node == null) {
            return null;
        }
        if(node.type() == NodeType.SCALAR) {
            return stringYAMLDeserializer.deserialize(node, ctx);
        } else {
            return mapper.getDeserializer().deserialize(node, ctx);
        }
    }

    @Override
    public void serialize(YamlMapping writer, String propertyName, Object value, YAMLSerializationContext ctx) {
        if (value instanceof String) {
            stringYAMLSerializer.serialize(writer, propertyName, (String) value, ctx);
        } else if (value instanceof WorkflowExecTimeout) {
            mapper.getSerializer().serialize(writer, propertyName, (WorkflowExecTimeout) value, ctx);
        }
    }

    @Override
    public void serialize(YamlSequence writer, Object value, YAMLSerializationContext ctx) {
        throw new UnsupportedOperationException("Unsupported serialization of " + value.getClass());
    }
}
