package org.kie.workbench.common.stunner.sw.definition.custom.yaml;

import java.util.List;

import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import elemental2.core.JsArray;
import elemental2.core.JsObject;
import elemental2.core.Reflect;
import jakarta.json.JsonObject;
import jsinterop.base.Js;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLDeserializationException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.BaseNumberYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.BooleanYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.StringYAMLDeserializer;
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

    private static final StringYAMLDeserializer stringYAMLDeserializer = new StringYAMLDeserializer();
    private static final BooleanYAMLDeserializer booleanYAMLDeserializer = new BooleanYAMLDeserializer();

    private static final BaseNumberYAMLDeserializer.ByteYAMLDeserializer byteYAMLDeserializer = new BaseNumberYAMLDeserializer.ByteYAMLDeserializer();
    private static final BaseNumberYAMLDeserializer.ShortYAMLDeserializer shortYAMLDeserializer = new BaseNumberYAMLDeserializer.ShortYAMLDeserializer();
    private static final BaseNumberYAMLDeserializer.IntegerYAMLDeserializer integerYAMLDeserializer = new BaseNumberYAMLDeserializer.IntegerYAMLDeserializer();
    private static final BaseNumberYAMLDeserializer.LongYAMLDeserializer longYAMLDeserializer = new BaseNumberYAMLDeserializer.LongYAMLDeserializer();
    private static final BaseNumberYAMLDeserializer.FloatYAMLDeserializer floatYAMLDeserializer = new BaseNumberYAMLDeserializer.FloatYAMLDeserializer();
    private static final BaseNumberYAMLDeserializer.DoubleYAMLDeserializer doubleYAMLDeserializer = new BaseNumberYAMLDeserializer.DoubleYAMLDeserializer();


    @Override
    public ValueHolder deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) throws YAMLDeserializationException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ValueHolder deserialize(YamlNode node, YAMLDeserializationContext ctx) {
        throw new UnsupportedOperationException("Not implemented yet");
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
