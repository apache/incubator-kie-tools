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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.clone.ClonePolicy;

public abstract class BindableMorphDefinition implements MorphDefinition {

    protected abstract Class<?> getDefaultType();

    protected abstract Map<Class<?>, Collection<Class<?>>> getDomainMorphs();

    @Override
    public boolean accepts(final String definitionId) {
        Set<Class<?>> s = getDomainMorphs().keySet();
        for (Class<?> c : s) {
            if (getDefinitionId(c).equals(definitionId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getBase() {
        final Class<?> baseType = getDomainMorphs().keySet().iterator().next();
        return getDefinitionId(baseType);
    }

    @Override
    public ClonePolicy getPolicy() {
        return ClonePolicy.ALL;
    }

    public boolean canMorphType(final Class<?> type) {
        return getDomainMorphs().keySet().contains(type);
    }

    @Override
    public String getDefault() {
        return getDefinitionId(getDefaultType());
    }

    @Override
    public Iterable<String> getTargets(final String sourceId) {
        final Class<?> sourceType = getSourceType(sourceId);
        if (null != sourceType) {
            return getTargetsForType(sourceType);
        }
        return null;
    }

    protected Class<?> getSourceType(final String definitionId) {
        Set<Class<?>> s = getDomainMorphs().keySet();
        for (Class<?> c : s) {
            if (getDefinitionId(c).equals(definitionId)) {
                return c;
            }
        }
        return null;
    }

    protected Collection<String> getTargetsForType(final Class<?> sourceType) {
        final Collection<Class<?>> targetClasses = getDomainMorphs().get(sourceType);
        if (null != targetClasses && !targetClasses.isEmpty()) {
            final List<String> result = new LinkedList<>();
            for (final Class<?> targetClass : targetClasses) {
                final String id = getDefinitionId(targetClass);
                result.add(id);
            }
            return result;
        }
        return null;
    }

    protected Class<?> getTargetClass(final Class<?> sourceType,
                                      final String target) {
        final Collection<Class<?>> targetClasses = getDomainMorphs().get(sourceType);
        if (null != targetClasses && !targetClasses.isEmpty()) {
            for (final Class<?> targetClass : targetClasses) {
                final String id = getDefinitionId(targetClass);
                if (id.equals(target)) {
                    return targetClass;
                }
            }
        }
        return null;
    }

    protected String getDefinitionId(final Class<?> definitionClass) {
        return BindableAdapterUtils.getDefinitionId(definitionClass);
    }
}
