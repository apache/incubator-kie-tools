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

import org.treblereel.gwt.yaml.api.YAMLDeserializer;
import org.treblereel.gwt.yaml.api.YAMLSerializer;
import org.treblereel.gwt.yaml.api.exception.YAMLDeserializationException;
import org.treblereel.gwt.yaml.api.internal.deser.StringYAMLDeserializer;
import org.treblereel.gwt.yaml.api.internal.deser.YAMLDeserializationContext;
import org.treblereel.gwt.yaml.api.internal.ser.BaseNumberYAMLSerializer;
import org.treblereel.gwt.yaml.api.internal.ser.StringYAMLSerializer;
import org.treblereel.gwt.yaml.api.internal.ser.YAMLSerializationContext;
import org.treblereel.gwt.yaml.api.node.NodeType;
import org.treblereel.gwt.yaml.api.node.YamlMapping;
import org.treblereel.gwt.yaml.api.node.YamlNode;
import org.treblereel.gwt.yaml.api.node.YamlSequence;

public class StringNumberYamlTypeSerializer implements YAMLDeserializer, YAMLSerializer {

    private static final StringYAMLSerializer stringJsonSerializer = new StringYAMLSerializer();

    private static final StringYAMLDeserializer stringJsonDeserializer = new StringYAMLDeserializer();

    private static final BaseNumberYAMLSerializer.IntegerYAMLSerializer integerJsonSerializer = new BaseNumberYAMLSerializer.IntegerYAMLSerializer();

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
            String value =  stringJsonDeserializer.deserialize(node, ctx);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return value;
            }
        }
        return null;
    }

    @Override
    public void serialize(YamlMapping writer, String propertyName, Object value, YAMLSerializationContext ctx) {
        if (value instanceof String) {
            stringJsonSerializer.serialize(writer, propertyName, (String) value, ctx);
        } else if (value instanceof Integer) {
            integerJsonSerializer.serialize(writer, propertyName, ((Integer) value), ctx);
        }
    }

    @Override
    public void serialize(YamlSequence writer, Object value, YAMLSerializationContext ctx) {
        if (value instanceof String) {
            stringJsonSerializer.serialize(writer, (String) value, ctx);
        } else if (value instanceof Integer) {
            integerJsonSerializer.serialize(writer, ((Integer) value), ctx);
        }
    }
}
