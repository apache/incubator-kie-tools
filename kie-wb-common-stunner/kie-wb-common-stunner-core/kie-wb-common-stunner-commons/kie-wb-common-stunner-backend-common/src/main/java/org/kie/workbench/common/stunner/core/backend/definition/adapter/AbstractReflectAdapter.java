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

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.backend.definition.adapter.bind.BackendBindableAdapterFunctions;
import org.kie.workbench.common.stunner.core.definition.adapter.PriorityAdapter;

public abstract class AbstractReflectAdapter<T> implements PriorityAdapter {

    @SuppressWarnings("unchecked")
    protected <A extends Annotation, V> V getAnnotatedFieldValue(final T object,
                                                                 final Class<A> annotationType) throws IllegalAccessException {
        return ReflectionAdapterUtils.getAnnotatedFieldValue(object,
                                                             annotationType);
    }

    @SuppressWarnings("unchecked")
    protected <A extends Annotation, V> V getAnnotatedFieldValue(final T object,
                                                                 final Class<?> sourceType,
                                                                 final Class<A> annotationType) throws IllegalAccessException {
        return ReflectionAdapterUtils.getAnnotatedFieldValue(object,
                                                             sourceType,
                                                             annotationType);
    }

    protected static <T extends Annotation> T getClassAnnotation(final Class<?> type,
                                                                 final Class<T> annotationType) {
        return ReflectionAdapterUtils.getClassAnnotation(type,
                                                         annotationType);
    }

    protected static String getDefinitionId(final Class<?> type) {
        return ReflectionAdapterUtils.getDefinitionId(type);
    }

    @Override
    public int getPriority() {
        return 100;
    }

    public Optional<?> getProperty(T pojo, String field) {
        Object value = BackendBindableAdapterFunctions.getFieldValue(pojo, field);
        return Optional.ofNullable(value);
    }
}
