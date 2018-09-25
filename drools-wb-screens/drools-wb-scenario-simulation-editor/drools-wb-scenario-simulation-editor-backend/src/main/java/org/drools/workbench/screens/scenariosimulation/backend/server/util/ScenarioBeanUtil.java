/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.backend.server.util;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioException;

public class ScenarioBeanUtil {

    private ScenarioBeanUtil() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T fillBean(String className, Map<List<String>, Object> params, ClassLoader classLoader) {
        Class<T> clazz = loadClass(className, classLoader);

        // if a direct mapping exists (no steps to reach the field) the value itself is the object
        Optional<Object> noStepMapping = params.entrySet().stream()
                .filter(e -> e.getKey().isEmpty()).map(Map.Entry::getValue).findFirst();
        if (noStepMapping.isPresent()) {
            return (T) noStepMapping.get();
        }

        T beanToFill = newInstance(clazz);

        for (Map.Entry<List<String>, Object> param : params.entrySet()) {
            try {
                fillProperty(beanToFill, param.getKey(), param.getValue());
            } catch (ReflectiveOperationException e) {
                throw new ScenarioException(new StringBuilder().append("Impossible to fill ").append(className)
                                                    .append(" with the provided properties").toString(), e);
            }
        }

        return beanToFill;
    }

    private static <T> void fillProperty(T beanToFill, List<String> steps, Object propertyValue) throws ReflectiveOperationException {
        List<String> pathToProperty = steps.subList(0, steps.size() - 1);
        String lastStep = steps.get(steps.size() - 1);

        Object currentObject = beanToFill;
        if (pathToProperty.size() > 0) {
            currentObject = navigateToObject(beanToFill, pathToProperty);
        }

        Field last = currentObject.getClass().getDeclaredField(lastStep);
        last.setAccessible(true);
        last.set(currentObject, propertyValue);
    }

    public static Object navigateToObject(Object rootObject, List<String> steps) {
        if (steps.size() < 1) {
            throw new ScenarioException(new StringBuilder().append("Invalid path to a property: path='")
                                                .append(String.join(".", steps)).append("'").toString());
        }

        Class<?> currentClass = rootObject.getClass();
        Object currentObject = rootObject;

        for (String step : steps) {
            Field declaredField = null;
            try {
                declaredField = currentClass.getDeclaredField(step);
            } catch (NoSuchFieldException e) {
                throw new ScenarioException(new StringBuilder().append("Impossible to find field with name '")
                                                    .append(step).append("' in class ")
                                                    .append(currentClass.getCanonicalName()).toString(), e);
            }
            declaredField.setAccessible(true);
            currentClass = declaredField.getType();
            try {
                currentObject = getOrCreate(declaredField, currentObject);
            } catch (ReflectiveOperationException e) {
                throw new ScenarioException(new StringBuilder().append("Impossible to get or create class ")
                                                    .append(currentClass.getCanonicalName()).toString());
            }
        }

        return currentObject;
    }

    private static Object getOrCreate(Field declaredField, Object currentObject) throws ReflectiveOperationException {
        Object value = declaredField.get(currentObject);
        if (value == null) {
            value = newInstance(declaredField.getType());
            declaredField.set(currentObject, value);
        }
        return value;
    }

    private static <T> Class<T> loadClass(String className, ClassLoader classLoader) {
        try {
            return (Class<T>) classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new ScenarioException(new StringBuilder().append("Impossible to load class ").append(className).toString(), e);
        }
    }

    private static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new ScenarioException(new StringBuilder().append("Class ").append(clazz.getCanonicalName())
                                                .append(" has no empty constructor").toString(), e);
        }
    }
}
