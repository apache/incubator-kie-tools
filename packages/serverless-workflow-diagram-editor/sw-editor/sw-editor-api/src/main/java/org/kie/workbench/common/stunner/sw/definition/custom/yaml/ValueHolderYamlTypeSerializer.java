package org.kie.workbench.common.stunner.sw.definition.custom.yaml;

import java.util.List;

import elemental2.core.JsArray;
import elemental2.core.JsObject;
import elemental2.core.Reflect;
import jakarta.json.JsonObject;
import jsinterop.base.Js;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLDeserializationException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.BaseNumberYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.BooleanYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.StringYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.YAMLSerializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.NodeType;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlNode;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlSequence;
import org.kie.workbench.common.stunner.sw.definition.ValueHolder;


public class ValueHolderYamlTypeSerializer implements YAMLDeserializer<ValueHolder>, YAMLSerializer<ValueHolder> {

    private static final StringYAMLSerializer stringYAMLSerializer = new StringYAMLSerializer();
    private static final BooleanYAMLSerializer booleanYAMLSerializer = new BooleanYAMLSerializer();
    private static final BaseNumberYAMLSerializer.ByteYAMLSerializer byteYAMLSerializer = new BaseNumberYAMLSerializer.ByteYAMLSerializer();
    private static final BaseNumberYAMLSerializer.ShortYAMLSerializer shortYAMLSerializer = new BaseNumberYAMLSerializer.ShortYAMLSerializer();
    private static final BaseNumberYAMLSerializer.IntegerYAMLSerializer integerYAMLSerializer = new BaseNumberYAMLSerializer.IntegerYAMLSerializer();
    private static final BaseNumberYAMLSerializer.LongYAMLSerializer longYAMLSerializer = new BaseNumberYAMLSerializer.LongYAMLSerializer();
    private static final BaseNumberYAMLSerializer.FloatYAMLSerializer floatYAMLSerializer = new BaseNumberYAMLSerializer.FloatYAMLSerializer();

    @Override
    public ValueHolder deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) throws YAMLDeserializationException {
        YamlNode value = yaml.getNode(key);
        if (value == null) {
            return null;
        }
        return deserialize(value, ctx);
    }

    @Override
    public ValueHolder deserialize(YamlNode node, YAMLDeserializationContext ctx) {
        if (node != null && node.type() == NodeType.MAPPING && !node.asMapping().isEmpty()) {
            ValueHolder valueHolder = new ValueHolder();
            YamlMapping yamlMapping = node.asMapping();
            writeObject(valueHolder, yamlMapping);
            return valueHolder;
        }
        return null;
    }

    private void writeObject(Object obj, YamlMapping yamlMapping) {
        yamlMapping.keys().forEach(key -> {
            YamlNode yamlNode = yamlMapping.getNode(key);
            if (yamlNode.type() == NodeType.SCALAR) {
                Object value = yamlNode.asScalar().value();
                if (value != null) {
                    Reflect.set(obj, key, value);
                }
            } else if (yamlNode.type() == NodeType.MAPPING) {
                ValueHolder inner = new ValueHolder();
                Reflect.set(obj, key, inner);
                writeObject(inner, yamlNode.asMapping());
            } else if (yamlNode.type() == NodeType.SEQUENCE) {
                YamlSequence sequence = yamlNode.asSequence();
                JsArray<Object> array = new JsArray<>();
                sequence.values().forEach(yamlNode1 -> {
                    if (yamlNode1.type() == NodeType.SCALAR) {
                        array.push(yamlNode1.asScalar().value());
                    } else if (yamlNode1.type() == NodeType.MAPPING) {
                        ValueHolder inner = new ValueHolder();
                        array.push(inner);
                        writeObject(inner, yamlNode1.asMapping());
                    }
                });
                Reflect.set(obj, key, array);
            } else {
                throw new UnsupportedOperationException("Unsupported key type " + yamlNode.type());
            }
        });
    }

    @Override
    public void serialize(YamlMapping writer, String propertyName, ValueHolder value, YAMLSerializationContext ctx) {
        if (value == null) {
            return;
        }
        YamlMapping innerWrite = writer.addMappingNode(propertyName);
        writeObjectProps(innerWrite, value, ctx);
    }

    private void writeObjectProps(YamlMapping writer, Object obj, YAMLSerializationContext ctx) {
        if (obj == null) {
            return;
        }
        if (obj instanceof JsonObject) {
            obj = ((JsonObject) obj).asJsonObject();
        }
        List<Reflect.OwnKeysArrayUnionType> keys = Reflect.ownKeys(obj).asList();
        if (!keys.isEmpty()) {
            for (Reflect.OwnKeysArrayUnionType k : keys) {
                String key = k.asString();
                if (key.startsWith("$")) { // skip internal properties
                    continue;
                }

                Object jsonValue = Js.asPropertyMap(obj).get(key);
                if (jsonValue instanceof JsonObject) {
                    jsonValue = ((JsonObject) jsonValue).asJsonObject();
                }
                if (jsonValue instanceof String) {
                    stringYAMLSerializer.serialize(writer, key, (String) jsonValue, ctx);
                } else if (jsonValue instanceof Byte) {
                    byteYAMLSerializer.serialize(writer, key, (Byte) jsonValue, ctx);
                } else if (jsonValue instanceof Short) {
                    shortYAMLSerializer.serialize(writer, key, (Short) jsonValue, ctx);
                } else if (jsonValue instanceof Integer) {
                    integerYAMLSerializer.serialize(writer, key, (Integer) jsonValue, ctx);
                } else if (jsonValue instanceof Double) {
                    writer.addScalarNode(key, (Double) jsonValue);
                } else if (jsonValue instanceof Float) {
                    floatYAMLSerializer.serialize(writer, key, (Float) jsonValue, ctx);
                } else if (jsonValue instanceof Long) {
                    longYAMLSerializer.serialize(writer, key, (Long) jsonValue, ctx);
                } else if (jsonValue instanceof Boolean) {
                    booleanYAMLSerializer.serialize(writer, key, (Boolean) jsonValue, ctx);
                } else if (JsArray.isArray(jsonValue)) {
                    JsArray<?> array = (JsArray<?>) jsonValue;
                    YamlSequence yamlSequenceWriter = writer.addSequenceNode(key);
                    for (Object value : array.asList()) {
                        if (value instanceof String) {
                            stringYAMLSerializer.serialize(yamlSequenceWriter, (String) value, ctx);
                        } else if (value instanceof Integer) {
                            integerYAMLSerializer.serialize(yamlSequenceWriter, (Integer) value, ctx);
                        } else if (value instanceof Double) {
                            yamlSequenceWriter.addScalarNode((Double) value);
                        } else if (value instanceof Float) {
                            floatYAMLSerializer.serialize(yamlSequenceWriter, (Float) value, ctx);
                        } else if (value instanceof Long) {
                            longYAMLSerializer.serialize(yamlSequenceWriter, (Long) value, ctx);
                        } else if (value instanceof Boolean) {
                            booleanYAMLSerializer.serialize(yamlSequenceWriter, (Boolean) value, ctx);
                        } else if (value instanceof Object) {
                            YamlMapping innerWrite = yamlSequenceWriter.addMappingNode();
                            writeObjectProps(innerWrite, value, ctx);
                        }
                    }
                } else if (jsonValue instanceof JsObject) {
                    YamlMapping innerWrite = writer.addMappingNode(key);
                    writeObjectProps(innerWrite, jsonValue, ctx);
                } else {
                    throw new UnsupportedOperationException("unknown type " + jsonValue.getClass().getCanonicalName());
                }
            }
        }
    }

    @Override
    public void serialize(YamlSequence writer, ValueHolder value, YAMLSerializationContext ctx) {
        YamlMapping innerWrite = writer.addMappingNode();
        writeObjectProps(innerWrite, value, ctx);
    }
}
