package org.kie.workbench.common.stunner.sw.definition.custom.yaml;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLDeserializationException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.StringYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.StringYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.YAMLSerializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.NodeType;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlNode;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlSequence;
import org.kie.workbench.common.stunner.sw.definition.SubflowExecutionType;

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
