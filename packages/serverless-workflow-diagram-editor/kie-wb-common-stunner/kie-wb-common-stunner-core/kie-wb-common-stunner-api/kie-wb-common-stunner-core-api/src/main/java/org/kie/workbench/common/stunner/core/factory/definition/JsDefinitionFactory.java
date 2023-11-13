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


package org.kie.workbench.common.stunner.core.factory.definition;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JsDefinitionFactory implements DefinitionFactory<Object> {

    private final Map<String, Supplier<?>> definitions = new HashMap<>();


    @Override
    public boolean accepts(String identifier) {
        return true;
    }

    private Object createInstanceForType(String typeName) {
        if(definitions.containsKey(typeName)) {
            return definitions.get(typeName).get();
        }
        throw new Error("No definition found for type " + typeName);
    }

    public <T> void register(Class<T> typeName, Supplier<T> constructor) {
        definitions.put(typeName.getCanonicalName(), constructor);
    }

    @Override
    public Object build(String identifier) {
        return createInstanceForType(identifier);
    }
}
