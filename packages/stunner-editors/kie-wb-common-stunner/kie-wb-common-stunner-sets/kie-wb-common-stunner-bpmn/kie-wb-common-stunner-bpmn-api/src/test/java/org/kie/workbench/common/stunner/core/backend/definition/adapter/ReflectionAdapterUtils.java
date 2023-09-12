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


package org.kie.workbench.common.stunner.core.backend.definition.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;

public class ReflectionAdapterUtils {

    @SuppressWarnings("unchecked")
    public static <T, A extends Annotation, V> V getAnnotatedFieldValue(final T object,
                                                                        final Class<A> annotationType) throws IllegalAccessException {
        Class<?> c = object.getClass();
        while (!(c.isAssignableFrom(Object.class))) {
            V result = getAnnotatedFieldValue(object,
                                              c,
                                              annotationType);
            if (null != result) {
                return result;
            }
            c = c.getSuperclass();
        }
        return null;
    }

    public static <T, V> Set<V> getFieldValues(final T object,
                                               final Collection<String> fieldNames) throws IllegalAccessException {
        Set<V> result = new LinkedHashSet<V>();
        if (null != fieldNames) {
            for (String fieldName : fieldNames) {
                Class<?> c = object.getClass();
                while (!(c.isAssignableFrom(Object.class))) {
                    V result1 = getFieldValue(object,
                                              c,
                                              fieldName);
                    if (null != result1) {
                        result.add(result1);
                    }
                    c = c.getSuperclass();
                }
            }
        }
        return result;
    }

    @SuppressWarnings("all")
    public static <T, V> V getValue(final T object,
                                    final String fieldName) throws IllegalAccessException {
        String[] fields = fieldName.contains(".") ? fieldName.split("\\.") : new String[]{fieldName};
        Object value = object;
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];
            value = getFieldValue(value, field);
        }
        return (V) value;
    }

    public static <T, V> V getFieldValue(final T object,
                                         final String fieldName) throws IllegalAccessException {
        Class<?> c = object.getClass();
        while (!(c.isAssignableFrom(Object.class))) {
            V result = getFieldValue(object,
                                     c,
                                     fieldName);
            if (null != result) {
                return result;
            }
            c = c.getSuperclass();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T, A extends Annotation, V> V getAnnotatedFieldValue(final T object,
                                                                        final Class<?> sourceType,
                                                                        final Class<A> annotationType) throws IllegalAccessException {
        V result = null;
        Field[] fields = sourceType.getDeclaredFields();
        for (Field field : fields) {
            A annotation = field.getAnnotation(annotationType);
            if (null != annotation) {
                field.setAccessible(true);
                result = (V) field.get(object);
                break;
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T, V> V getFieldValue(final T object,
                                         final Class<?> sourceType,
                                         final String fieldName) throws IllegalAccessException {
        V result = null;
        Field[] fields = sourceType.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                field.setAccessible(true);
                result = (V) field.get(object);
                break;
            }
        }
        return result;
    }

    public static <T> Field getField(final T object,
                                     final String fieldName) throws SecurityException {
        Class<?> c = object.getClass();
        while (!(c.isAssignableFrom(Object.class))) {
            Field result = getField(c,
                                    fieldName);
            if (null != result) {
                return result;
            }
            c = c.getSuperclass();
        }
        return null;
    }

    public static Field getField(final Class<?> sourceType,
                                 final String fieldName) throws SecurityException {
        Field[] fields = sourceType.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    public static List<Field> getFields(final Class<?> sourceType) throws SecurityException {
        return Stream.of(sourceType.getDeclaredFields()).collect(Collectors.toList());
    }

    public static <T extends Annotation> T getClassAnnotation(final Class<?> type,
                                                              final Class<T> annotationType) {
        Class<?> c = type;
        while (!(c.isAssignableFrom(Object.class))) {
            T result = c.getAnnotation(annotationType);
            if (null != result) {
                return result;
            }
            c = c.getSuperclass();
        }
        return null;
    }

    public static <T extends Annotation> Collection<Field> getFieldAnnotations(final Class<?> type,
                                                                               final Class<T> annotationType) {
        if (null != type && null != annotationType) {
            Collection<Field> result = new LinkedList<>();
            Class<?> c = type;
            while (!(c.isAssignableFrom(Object.class))) {
                Collection<Field> fields = _getFieldAnnotations(c,
                                                                annotationType);
                if (null != fields && !fields.isEmpty()) {
                    result.addAll(fields);
                }
                c = c.getSuperclass();
            }
            return result;
        }
        return null;
    }

    private static <T extends Annotation> Collection<Field> _getFieldAnnotations(final Class<?> type,
                                                                                 final Class<T> annotationType) {
        Field[] fields = type.getDeclaredFields();
        if (null != fields) {
            Collection<Field> result = new LinkedList<>();
            for (Field field : fields) {
                T annotation = field.getAnnotation(annotationType);
                if (null != annotation) {
                    result.add(field);
                }
            }
            return result;
        }
        return null;
    }

    public static String getDefinitionId(final Class<?> type) {
        return BindableAdapterUtils.getDefinitionId(type);
    }

    public static String getPropertyId(final Object pojo) {
        return BindableAdapterUtils.getPropertyId(pojo.getClass());
    }
}