/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.sw.marshall.yaml;

import org.kie.workbench.common.stunner.sw.definition.Event;
import org.kie.workbench.common.stunner.sw.definition.Event_YamlMapperImpl;
import org.treblereel.gwt.yaml.api.YAMLDeserializer;
import org.treblereel.gwt.yaml.api.YAMLSerializer;
import org.treblereel.gwt.yaml.api.exception.YAMLDeserializationException;
import org.treblereel.gwt.yaml.api.internal.deser.StringYAMLDeserializer;
import org.treblereel.gwt.yaml.api.internal.deser.YAMLDeserializationContext;
import org.treblereel.gwt.yaml.api.internal.deser.array.ArrayYAMLDeserializer;
import org.treblereel.gwt.yaml.api.internal.ser.StringYAMLSerializer;
import org.treblereel.gwt.yaml.api.internal.ser.YAMLSerializationContext;
import org.treblereel.gwt.yaml.api.internal.ser.array.ArrayYAMLSerializer;
import org.treblereel.gwt.yaml.api.node.NodeType;
import org.treblereel.gwt.yaml.api.node.YamlMapping;
import org.treblereel.gwt.yaml.api.node.YamlNode;
import org.treblereel.gwt.yaml.api.node.YamlSequence;

public class EventYamlTypeSerializer implements YAMLDeserializer, YAMLSerializer {

    private static final Event_YamlMapperImpl mapper = Event_YamlMapperImpl.INSTANCE;
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
        if(node.type() == NodeType.SCALAR) {
            return stringYAMLDeserializer.deserialize(node, ctx);
        } else if (node.type() == NodeType.SEQUENCE) {
            return ArrayYAMLDeserializer.newInstance(mapper.getDeserializer(), Event[]::new).deserialize(node, ctx);
        }
        return null;
    }

    @Override
    public void serialize(YamlMapping writer, String propertyName, Object obj, YAMLSerializationContext ctx) {
        if (obj instanceof String) {
            stringYAMLSerializer.serialize(writer, propertyName, (String) obj, ctx);
        } else if (obj instanceof Event[]) {
            new ArrayYAMLSerializer<>(mapper.getSerializer())
                    .serialize(writer, propertyName, (Event[]) obj,
                            ctx);
        }
    }

    @Override
    public void serialize(YamlSequence writer, Object value, YAMLSerializationContext ctx) {
        throw new RuntimeException("Not implemented");
    }
}
