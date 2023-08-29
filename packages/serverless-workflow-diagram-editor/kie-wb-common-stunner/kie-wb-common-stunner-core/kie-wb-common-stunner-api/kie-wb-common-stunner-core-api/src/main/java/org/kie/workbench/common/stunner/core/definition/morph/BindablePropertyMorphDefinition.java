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


package org.kie.workbench.common.stunner.core.definition.morph;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class BindablePropertyMorphDefinition extends BindableMorphDefinition
        implements PropertyMorphDefinition {

    protected abstract Map<Class<?>, Collection<MorphProperty>> getBindableMorphProperties();

    @Override
    public Iterable<MorphProperty> getMorphProperties(String definitionId) {
        final Class<?> type = getSourceType(definitionId);
        return getMorphPropertiesForType(type);
    }

    public Iterable<MorphProperty> getMorphPropertiesForType(Class<?> type) {
        if (null != type) {
            return getBindableMorphProperties().get(type);
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<Class<?>, Collection<Class<?>>> getDomainMorphs() {
        if (null != getBindableMorphProperties() && !getBindableMorphProperties().isEmpty()) {
            final Map<Class<?>, Collection<Class<?>>> result = new LinkedHashMap<>();
            for (final Map.Entry<Class<?>, Collection<MorphProperty>> entry : getBindableMorphProperties().entrySet()) {
                final Class<?> sourceType = entry.getKey();
                final Collection<MorphProperty> pms = entry.getValue();
                for (final MorphProperty pm : pms) {
                    final BindableMorphProperty<?, ?> morphProperty = (BindableMorphProperty<?, ?>) pm;
                    Collection<Class<?>> targets = result.get(sourceType);
                    if (null == targets) {
                        targets = new LinkedList<>();
                        result.put(sourceType,
                                   targets);
                    }
                    targets.addAll(morphProperty.getMorphTargetClasses().values());
                }
            }
            return result;
        }
        return null;
    }
}
