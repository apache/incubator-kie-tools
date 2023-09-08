package org.kie.workbench.common.stunner.sw.marshall.yaml;

import org.kie.workbench.common.stunner.sw.definition.WorkflowTimeouts;
import org.kie.workbench.common.stunner.sw.definition.WorkflowTimeouts_YamlDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.WorkflowTimeouts_YamlSerializerImpl;
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

public class WorkflowTimeoutsYamlSerializer implements YAMLDeserializer, YAMLSerializer {

    private static final WorkflowTimeouts_YamlSerializerImpl serializer =
            new WorkflowTimeouts_YamlSerializerImpl();
    private static final WorkflowTimeouts_YamlDeserializerImpl deserializer =
            new WorkflowTimeouts_YamlDeserializerImpl();

    private static final StringYAMLSerializer stringJsonSerializer = new StringYAMLSerializer();

    private static final StringYAMLDeserializer stringJsonDeserializer = new StringYAMLDeserializer();


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
            return stringJsonDeserializer.deserialize(node, ctx);
        } else {
            return deserializer.deserialize(node, ctx);
        }
    }

    @Override
    public void serialize(YamlMapping writer, String propertyName, Object value, YAMLSerializationContext ctx) {
        if (value instanceof String) {
            stringJsonSerializer.serialize(writer, propertyName, (String) value, ctx);
        } else if (value instanceof WorkflowTimeouts) {
            serializer.serialize(writer, propertyName, (WorkflowTimeouts) value, ctx);
        }
    }

    @Override
    public void serialize(YamlSequence writer, Object obj, YAMLSerializationContext ctx) {
        if (obj instanceof String) {
            stringJsonSerializer.serialize(writer, (String) obj, ctx);
        } else if (obj instanceof WorkflowTimeouts) {
            serializer.serialize(writer, (WorkflowTimeouts) obj, ctx);
        }
    }
}
