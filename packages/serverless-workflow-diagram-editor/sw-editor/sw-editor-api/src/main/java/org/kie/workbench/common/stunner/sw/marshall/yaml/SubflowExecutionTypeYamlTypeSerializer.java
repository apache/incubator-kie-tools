package org.kie.workbench.common.stunner.sw.marshall.yaml;

import org.kie.workbench.common.stunner.sw.definition.SubflowExecutionType;
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

public class SubflowExecutionTypeYamlTypeSerializer implements YAMLDeserializer<SubflowExecutionType>, YAMLSerializer<SubflowExecutionType> {

    private static final StringYAMLSerializer stringYAMLSerializer = new StringYAMLSerializer();
    private static final StringYAMLDeserializer stringYAMLDeserializer = new StringYAMLDeserializer();


    @Override
    public SubflowExecutionType deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) throws YAMLDeserializationException {
        YamlNode value = yaml.getNode(key);
        if (value == null) {
            return null;
        }
        return deserialize(value, ctx);
    }

    @Override
    public SubflowExecutionType deserialize(YamlNode node, YAMLDeserializationContext ctx) {
        if (node == null) {
            return null;
        }
        if(node.type() == NodeType.SCALAR) {
            String v = stringYAMLDeserializer.deserialize(node, ctx);
            return v.equals("terminate") ? SubflowExecutionType.TERMINATE : SubflowExecutionType.CONTINUE;
        }
        return null;
    }

    @Override
    public void serialize(YamlMapping writer, String propertyName, SubflowExecutionType obj, YAMLSerializationContext ctx) {
        stringYAMLSerializer.serialize(writer, propertyName, (obj == SubflowExecutionType.TERMINATE ? "terminate" : "continue"), ctx);

    }

    @Override
    public void serialize(YamlSequence writer, SubflowExecutionType obj, YAMLSerializationContext ctx) {
        stringYAMLSerializer.serialize(writer, (obj == SubflowExecutionType.TERMINATE ? "terminate" : "continue"), ctx);
    }
}
