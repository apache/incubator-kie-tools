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
package org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.backend.definition.adapter.AbstractReflectDefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.annotation.DefinitionSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Handle i18n bundle.

@Dependent
public class BackendDefinitionSetAdapter<T> extends AbstractReflectDefinitionSetAdapter<T> implements DefinitionSetAdapter<T> {

    private static final Logger LOG = LoggerFactory.getLogger(BackendDefinitionSetAdapter.class);

    private final BackendDefinitionAdapter annotatedDefinitionAdapter;

    @Inject
    public BackendDefinitionSetAdapter(final BackendDefinitionAdapter annotatedDefinitionAdapter) {
        this.annotatedDefinitionAdapter = annotatedDefinitionAdapter;
    }

    @Override
    public boolean accepts(final Class<?> pojo) {
        return pojo.getAnnotation(DefinitionSet.class) != null;
    }

    @Override
    public Class<? extends ElementFactory> getGraphFactoryType(final T definitionSet) {
        Class<? extends ElementFactory> result = null;
        if (null != definitionSet) {
            DefinitionSet annotation = definitionSet.getClass().getAnnotation(DefinitionSet.class);
            if (null != annotation) {
                result = annotation.graphFactory();
            }
        }
        return result;
    }

    @Override
    public Annotation getQualifier(final T definitionSet) {
        if (null != definitionSet) {
            DefinitionSet annotation = definitionSet.getClass().getAnnotation(DefinitionSet.class);
            if (null != annotation) {
                return annotation::qualifier;
            }
        }
        return null;
    }

    @Override
    public String getDescription(final T definitionSet) {
        try {
            return getAnnotatedFieldValue(definitionSet,
                                          Description.class);
        } catch (Exception e) {
            LOG.error("Error obtaining annotated category for DefinitionSet with id " + getId(definitionSet));
        }
        return BindableAdapterUtils.toSimpleName(definitionSet);
    }

    @Override
    public Set<String> getDefinitions(final T definitionSet) {
        return getAnnotatedDefinitions(definitionSet);
    }
}
