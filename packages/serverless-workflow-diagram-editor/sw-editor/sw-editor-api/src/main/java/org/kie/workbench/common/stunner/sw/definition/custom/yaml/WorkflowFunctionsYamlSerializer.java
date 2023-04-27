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
import org.kie.workbench.common.stunner.sw.definition.Function;
import org.kie.workbench.common.stunner.sw.definition.Function_YamlDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.Function_YamlSerializerImpl;

import static com.amihaiemil.eoyaml.Node.SCALAR;
import static com.amihaiemil.eoyaml.Node.SEQUENCE;

public class WorkflowFunctionsYamlSerializer implements YAMLDeserializer, YAMLSerializer {

    private static final Function_YamlSerializerImpl serializer =
            new Function_YamlSerializerImpl();
    private static final Function_YamlDeserializerImpl deserializer =
            new Function_YamlDeserializerImpl();

    private static final StringYAMLDeserializer stringJsonDeserializer = new StringYAMLDeserializer();
    private static final StringYAMLSerializer stringJsonSerializer = new StringYAMLSerializer();


    @Override
    public Object deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) throws YAMLDeserializationException {
        YamlNode value = yaml.value(key);
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
        if(node.type() == SCALAR) {
            return stringJsonDeserializer.deserialize(node, ctx);
        } else if (node.type() == SEQUENCE) {
            return ArrayYAMLDeserializer.newInstance(deserializer, Function[]::new).deserialize(node, ctx);
        }
        return null;
    }

    @Override
    public void serialize(YAMLWriter writer, String propertyName, Object obj, YAMLSerializationContext ctx) {
        if (obj instanceof String) {
            stringJsonSerializer.serialize(writer, propertyName, (String) obj, ctx);
        } else if (obj instanceof Function[]) {
            new ArrayYAMLSerializer<>(serializer)
                    .serialize(writer, propertyName, (Function[]) obj,
                            ctx);
        }
    }

    @Override
    public void serialize(YAMLSequenceWriter writer, Object value, YAMLSerializationContext ctx) {
        throw new RuntimeException("Not implemented");
    }
}

