package org.kie.workbench.common.stunner.sw.definition.custom.yaml;

import java.util.List;

import com.amihaiemil.eoyaml.Node;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlSequence;
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
import org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.YAMLSequenceWriter;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.YAMLWriter;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.impl.DefaultYAMLSequenceWriter;
import org.kie.workbench.common.stunner.sw.definition.ValueHolder;


public class ValueHolderYamlTypeSerializer implements YAMLDeserializer<ValueHolder>, YAMLSerializer<ValueHolder> {

    private static final StringYAMLSerializer stringYAMLSerializer = new StringYAMLSerializer();
    private static final BooleanYAMLSerializer booleanYAMLSerializer = new BooleanYAMLSerializer();
    private static final BaseNumberYAMLSerializer.ByteYAMLSerializer byteYAMLSerializer = new BaseNumberYAMLSerializer.ByteYAMLSerializer();
    private static final BaseNumberYAMLSerializer.ShortYAMLSerializer shortYAMLSerializer = new BaseNumberYAMLSerializer.ShortYAMLSerializer();
    private static final BaseNumberYAMLSerializer.IntegerYAMLSerializer integerYAMLSerializer = new BaseNumberYAMLSerializer.IntegerYAMLSerializer();
    private static final BaseNumberYAMLSerializer.LongYAMLSerializer longYAMLSerializer = new BaseNumberYAMLSerializer.LongYAMLSerializer();
    private static final BaseNumberYAMLSerializer.FloatYAMLSerializer floatYAMLSerializer = new BaseNumberYAMLSerializer.FloatYAMLSerializer();
    private static final BaseNumberYAMLSerializer.DoubleYAMLSerializer doubleYAMLSerializer = new BaseNumberYAMLSerializer.DoubleYAMLSerializer();

    @Override
    public ValueHolder deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) throws YAMLDeserializationException {
        if(yaml != null || yaml.value(key) != null) {
            return deserialize(yaml.value(key), ctx);
        }
        return null;
    }

    @Override
    public ValueHolder deserialize(YamlNode node, YAMLDeserializationContext ctx) {
        if(node != null && node.type() == Node.MAPPING && !node.asMapping().isEmpty()) {
            ValueHolder valueHolder = new ValueHolder();
            YamlMapping yamlMapping = node.asMapping();
            writeObject(valueHolder, yamlMapping);
            return valueHolder;
        }
        return null;
    }

    private void writeObject(Object obj, YamlMapping yamlMapping) {
        yamlMapping.keys().forEach(key -> {
            if(key.type() == Node.SCALAR) {
                String keyName = key.asScalar().value();
                YamlNode yamlNode = yamlMapping.value(key);
                if (yamlNode.type() == Node.SCALAR) {
                    String value = yamlNode.asScalar().value();
                    if (value != null) {
                        Reflect.set(obj, keyName, value);
                    }
                } else if (yamlNode.type() == Node.MAPPING) {
                    ValueHolder inner = new ValueHolder();
                    Reflect.set(obj, keyName, inner);
                    writeObject(inner, yamlNode.asMapping());
                }else if (yamlNode.type() == Node.SEQUENCE) {
                    YamlSequence sequence = yamlNode.asSequence();
                    JsArray<Object> array = new JsArray<>();
                    sequence.values().forEach(yamlNode1 -> {
                        if (yamlNode1.type() == Node.SCALAR) {
                            array.push(yamlNode1.asScalar().value());
                        } else if (yamlNode1.type() == Node.MAPPING) {
                            ValueHolder inner = new ValueHolder();
                            array.push(inner);
                            writeObject(inner, yamlNode1.asMapping());
                        }
                    });
                    Reflect.set(obj, keyName, array);
                }
            } else {
                throw new UnsupportedOperationException("Unsupported key type " + key.type());
            }
        });
    }

    @Override
    public void serialize(YAMLWriter writer, String propertyName, ValueHolder value, YAMLSerializationContext ctx) {
        if (value == null) {
            return;
        }
        YAMLWriter innerWrite = ctx.newYAMLWriter();
        writeObjectProps(innerWrite, value, ctx);
        writer.value(propertyName, innerWrite.getWriter().build());
    }

    private void writeObjectProps(YAMLWriter writer, Object obj, YAMLSerializationContext ctx) {
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
                if(key.startsWith("$")) { // skip internal properties
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
                    doubleYAMLSerializer.serialize(writer, key, (Double) jsonValue, ctx);
                } else if (jsonValue instanceof Float) {
                    floatYAMLSerializer.serialize(writer, key, (Float) jsonValue, ctx);
                } else if (jsonValue instanceof Long) {
                    longYAMLSerializer.serialize(writer, key, (Long) jsonValue, ctx);
                } else if (jsonValue instanceof Boolean) {
                    booleanYAMLSerializer.serialize(writer, key, (Boolean) jsonValue, ctx);
                } else if (JsArray.isArray(jsonValue)) {
                    JsArray array = (JsArray) jsonValue;
                    YAMLSequenceWriter yamlSequenceWriter = new DefaultYAMLSequenceWriter();
                    for(Object value: array.asList()) {
                        if (value instanceof String) {
                            stringYAMLSerializer.serialize(yamlSequenceWriter, (String) value, ctx);
                        } else if (value instanceof Integer) {
                            integerYAMLSerializer.serialize(yamlSequenceWriter, (Integer) value, ctx);
                        } else if (value instanceof Double) {
                            doubleYAMLSerializer.serialize(yamlSequenceWriter, (Double) value, ctx);
                        } else if (value instanceof Float) {
                            floatYAMLSerializer.serialize(yamlSequenceWriter, (Float) value, ctx);
                        } else if (value instanceof Long) {
                            longYAMLSerializer.serialize(yamlSequenceWriter, (Long) value, ctx);
                        } else if (value instanceof Boolean) {
                            booleanYAMLSerializer.serialize(yamlSequenceWriter, (Boolean) value, ctx);
                        } else if (value instanceof Object) {
                            YAMLWriter innerWrite = ctx.newYAMLWriter();
                            writeObjectProps(innerWrite, value, ctx);
                            yamlSequenceWriter.value(innerWrite.getWriter().build());
                        }
                    }
                    writer.value(key, yamlSequenceWriter.getWriter());
                } else if (jsonValue instanceof JsObject) {
                    YAMLWriter innerWrite = ctx.newYAMLWriter();
                    writeObjectProps(innerWrite, jsonValue, ctx);
                    writer.value(key, innerWrite.getWriter().build());
                } else {
                    throw new UnsupportedOperationException("unknown type " + jsonValue.getClass().getCanonicalName());
                }
            }
        }
    }

    @Override
    public void serialize(YAMLSequenceWriter writer, ValueHolder value, YAMLSerializationContext ctx) {
        YAMLWriter innerWrite = ctx.newYAMLWriter();
        writeObjectProps(innerWrite, value, ctx);
        writer.value(innerWrite.getWriter().build());
    }
}
