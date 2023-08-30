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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;

public abstract class BindableMorphProperty<P, V> implements MorphProperty<V> {

    public abstract Class<?> getPropertyClass();

    public abstract Map getMorphTargetClasses();

    public abstract V getValue(P property);

    @Override
    public String getProperty() {
        return getPropertyId(getPropertyClass());
    }

    @Override
    public String getMorphTarget(final V value) {
        return getDefinitionId((Class<?>) getMorphTargetClasses().get(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<String> getMorphTargets() {
        final Set<String> result = new LinkedHashSet<>();
        final Map morphTargets = getMorphTargetClasses();
        Set<Map.Entry> entries = morphTargets.entrySet();
        for (final Map.Entry entry : entries) {
            final String tId = getDefinitionId((Class<?>) entry.getValue());
            result.add(tId);
        }
        return result;
    }

    protected String getPropertyId(final Class<?> type) {
        return BindableAdapterUtils.getPropertyId(type);
    }

    protected String getDefinitionId(final Class<?> type) {
        return BindableAdapterUtils.getDefinitionId(type);
    }
}
