/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.sw.definition.custom.yaml;

import elemental2.core.JsArray;
import elemental2.core.Reflect;
import jsinterop.base.Js;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLDeserializationException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.StringYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.YAMLSerializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.NodeType;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlNode;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlSequence;
import org.kie.workbench.common.stunner.sw.definition.Metadata;
import org.kie.workbench.common.stunner.sw.definition.ValueHolder;

public class MetadataYamlSerializer implements YAMLSerializer<Metadata>, YAMLDeserializer<Metadata> {

    private final static ValueHolderYamlTypeSerializer valueHolderYamlTypeSerializer = new ValueHolderYamlTypeSerializer();

    private static final StringYAMLDeserializer stringYAMLDeserializer = new StringYAMLDeserializer();


    @Override
    public Metadata deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) throws YAMLDeserializationException {
        YamlNode value = yaml.getNode(key);
        if (value == null) {
            return null;
        }
        return deserialize(value, ctx);
    }

    @Override
    public Metadata deserialize(YamlNode node, YAMLDeserializationContext ctx) {
        if (node != null && node.type() == NodeType.MAPPING && !node.asMapping().isEmpty()) {
            YamlMapping yaml = node.asMapping();
            Metadata metadata = new Metadata();
            yaml.keys().forEach(key -> {
                readObject(yaml.getNode(key), key, metadata);
            });

            return metadata;

        }
        return null;
    }

    private void readObject(YamlNode node, String prop, Object obj) {
        if(node == null) {
            return;
        }
        if(node.type() == NodeType.SCALAR) {
            Reflect.set(obj, prop, stringYAMLDeserializer.deserialize(node, null));
        } else if(node.type() == NodeType.MAPPING) {
            YamlMapping yaml = node.asMapping();
            ValueHolder valueHolder = new ValueHolder();
            yaml.keys().forEach(key -> {
                readObject(yaml.getNode(key), key, valueHolder);
                Reflect.set(obj, prop, valueHolder);
            });
        } else if(node.type() == NodeType.SEQUENCE) {
            JsArray array = new JsArray();

            node.asSequence().values().forEach(value -> {
                if(value.type() == NodeType.SCALAR) {
                    array.push(stringYAMLDeserializer.deserialize(value, null));
                } else if(value.type() == NodeType.MAPPING) {
                    YamlMapping yaml = value.asMapping();
                    yaml.keys().forEach(key -> {
                        ValueHolder valueHolder = new ValueHolder();
                        readObject(yaml.getNode(key), key, valueHolder);
                        array.push(valueHolder);
                    });
                }
            });
            if (array.length > 0) {
                Reflect.set(obj, prop, array);
            }
        }
    }

    @Override
    public void serialize(YamlMapping writer, String propertyName, Metadata metadata, YAMLSerializationContext ctx) {
        writeObjectProps(writer, metadata, ctx);
    }

    @Override
    public void serialize(YamlSequence writer, Metadata value, YAMLSerializationContext ctx) {
        YamlMapping innerWrite = writer.addMappingNode();
        writeObjectProps(innerWrite, value, ctx);
    }

    private void writeObjectProps(YamlMapping writer, Metadata metadata, YAMLSerializationContext ctx) {
        if(metadata != null) {
            valueHolderYamlTypeSerializer.serialize(writer, "metadata", Js.uncheckedCast(metadata), ctx);
        }
    }
}
