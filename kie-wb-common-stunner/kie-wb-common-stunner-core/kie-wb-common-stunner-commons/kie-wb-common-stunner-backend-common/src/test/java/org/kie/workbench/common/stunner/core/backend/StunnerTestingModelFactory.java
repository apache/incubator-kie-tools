/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.backend;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.function.Supplier;

import org.kie.workbench.common.stunner.core.backend.util.BackendBindableDefinitionUtils;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.definition.builder.VoidBuilder;
import org.kie.workbench.common.stunner.core.factory.definition.AbstractTypeDefinitionFactory;

/**
 * Model factory for annotated modelsfor using  on test scope.
 */
public class StunnerTestingModelFactory extends AbstractTypeDefinitionFactory<Object> {

    private final Object definitionSet;

    public StunnerTestingModelFactory(Object definitionSet) {
        this.definitionSet = definitionSet;
    }

    private static Set<Class<?>> getDefinitions(Object defSet) {
        return BackendBindableDefinitionUtils.getDefinitions(defSet);
    }

    @Override
    public Set<Class<?>> getAcceptedClasses() {
        return getDefinitions(this.definitionSet);
    }

    @Override
    public Object build(Class<?> clazz) {
        return newDefinitionBuilder(clazz).get();
    }

    private Supplier<?> newDefinitionBuilder(Class<?> definitionClass) {
        Class<? extends Builder<?>> builderClass = getDefinitionBuilderClass(definitionClass);
        if (null != builderClass) {
            // if there is a builder, then get the constructor for the builder
            Builder<?> builder = getEmptyConstructor(builderClass).get();
            // return the build method as a supplier
            return builder::build;
        } else {
            return getEmptyConstructor(definitionClass);
        }
    }

    private Class<? extends Builder<?>> getDefinitionBuilderClass(Class<?> definitionClass) {
        Definition annotation = definitionClass.getAnnotation(Definition.class);
        if (null != annotation && annotation.builder() != VoidBuilder.class) {
            return annotation.builder();
        }
        return null;
    }

    /**
     * wrap empty constructor into a Supplier, turning checked exceptions into runtime exceptions
     */
    private <T> Supplier<T> getEmptyConstructor(Class<T> definitionClass) {
        return () -> {
            try {
                return definitionClass.getConstructor().newInstance();
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException("No constructor for type " + definitionClass, e);
            }
        };
    }
}
