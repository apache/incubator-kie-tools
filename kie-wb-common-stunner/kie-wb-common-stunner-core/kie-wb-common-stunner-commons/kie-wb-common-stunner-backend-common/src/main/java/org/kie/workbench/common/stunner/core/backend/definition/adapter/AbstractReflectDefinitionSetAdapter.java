/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.core.backend.definition.adapter;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.annotation.DefinitionSet;
import org.kie.workbench.common.stunner.core.definition.annotation.SvgNodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractReflectDefinitionSetAdapter<T> extends AbstractReflectAdapter<T>
        implements DefinitionSetAdapter<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractReflectDefinitionSetAdapter.class);

    protected Set<String> getAnnotatedDefinitions(final T definitionSet) {
        Set<String> result = null;
        if (null != definitionSet) {
            DefinitionSet annotation = definitionSet.getClass().getAnnotation(DefinitionSet.class);
            if (null != annotation) {
                Class<?>[] definitions = annotation.definitions();
                if (definitions.length > 0) {
                    result = new HashSet<String>(definitions.length);
                    for (Class<?> defClass : definitions) {
                        result.add(BindableAdapterUtils.getDefinitionSetId(defClass));
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String getId(final T definitionSet) {
        String defSetId = BindableAdapterUtils.getDefinitionSetId(definitionSet.getClass());
        // Avoid weld proxy class names issues.
        if (defSetId.contains("$")) {
            defSetId = defSetId.substring(0,
                                          defSetId.indexOf("$"));
        }
        return defSetId;
    }

    @Override
    public String getDomain(final T definitionSet) {
        return BindableAdapterUtils.getDefinitionSetDomain(definitionSet.getClass());
    }

    @Override
    public Optional<String> getSvgNodeId(T definitionSet) {
        try {
            return Optional.ofNullable(getAnnotatedFieldValue(definitionSet,
                                                              SvgNodeId.class));
        } catch (Exception e) {
            LOG.error("Error obtaining annotated SvgNodeId for DefinitionSet with id " + getId(definitionSet));
        }
        return Optional.empty();
    }
}
