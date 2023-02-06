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

import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLDeserializationException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.StringYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.StringYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.YAMLSerializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.array.ArrayYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.YAMLSequenceWriter;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.YAMLWriter;
import org.kie.workbench.common.stunner.sw.definition.Error_YamlDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.Error_YamlSerializerImpl;

import static com.amihaiemil.eoyaml.Node.SCALAR;

public class ErrorYamlSerializer implements YAMLDeserializer, YAMLSerializer {

    private static final StringYAMLSerializer stringYAMLSerializer = new StringYAMLSerializer();
    private static final StringYAMLDeserializer stringYAMLDeserializer = new StringYAMLDeserializer();

    private static final Error_YamlDeserializerImpl deserializer =
            new Error_YamlDeserializerImpl();

    private static final Error_YamlSerializerImpl serializer =
            new Error_YamlSerializerImpl();


    @Override
    public Object deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) throws YAMLDeserializationException {
        if(yaml == null || yaml.isEmpty() || yaml.value(key) == null) {
            return null;
        }

        return deserialize(yaml.value(key), ctx);    }

    @Override
    public Object deserialize(YamlNode node, YAMLDeserializationContext ctx) {
        if (node == null) {
            return null;
        }
        if(node.type() == SCALAR) {
            return stringYAMLDeserializer.deserialize(node, ctx);
        } else {
            return deserializer.deserialize(node, ctx);
        }

    }

    @Override
    public void serialize(YAMLWriter writer, String propertyName, Object obj, YAMLSerializationContext ctx) {
        if (obj instanceof String) {
            stringYAMLSerializer.serialize(writer, propertyName, (String) obj, ctx);
        } else if (obj instanceof org.kie.workbench.common.stunner.sw.definition.Error[]) {
            new ArrayYAMLSerializer<org.kie.workbench.common.stunner.sw.definition.Error>(serializer)
                    .serialize(writer, (org.kie.workbench.common.stunner.sw.definition.Error[]) obj,
                            ctx);

        }
    }

    @Override
    public void serialize(YAMLSequenceWriter writer, Object value, YAMLSerializationContext ctx) {
        throw new RuntimeException("Not implemented");
    }
}
