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

package org.uberfire.annotations.processors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Utilities for code generation
 */
public class GeneratorUtils {

    public static AnnotationMirror getAnnotation(Elements elementUtils,
                                                 Element annotationTarget,
                                                 String annotationName) {
        for (AnnotationMirror annotation : elementUtils.getAllAnnotationMirrors(annotationTarget)) {
            if (annotationName.contentEquals(getQualifiedName(annotation))) {
                return annotation;
            }
        }
        return null;
    }

    public static Name getQualifiedName(AnnotationMirror annotation) {
        return ((TypeElement) annotation.getAnnotationType().asElement()).getQualifiedName();
    }

    /**
     * Provides a uniform way of working with single- and multi-valued AnnotationValue objects.
     * @return the annotation values as strings. For multi-valued annotation params, the collection's iteration order matches the
     * order the values appeared in the source code. Single-valued params are wrapped in a single-element collection.
     * In either case, don't attempt to modify the returned collection.
     */
    public static Collection<String> extractValue(final AnnotationValue value) {
        if (value.getValue() instanceof Collection) {
            final Collection<?> varray = (List<?>) value.getValue();
            final ArrayList<String> result = new ArrayList<String>(varray.size());
            for (final Object active : varray) {
                result.addAll(extractValue((AnnotationValue) active));
            }
            return result;
        }
        return Collections.singleton(value.getValue().toString());
    }

    public static boolean debugLoggingEnabled() {
        return Boolean.parseBoolean(System.getProperty("org.uberfire.processors.debug",
                                                       "false"));
    }

    public static AnnotationValue extractAnnotationPropertyValue(Elements elementUtils,
                                                                 AnnotationMirror annotation,
                                                                 CharSequence annotationProperty) {

        Map<? extends ExecutableElement, ? extends AnnotationValue> annotationParams =
                elementUtils.getElementValuesWithDefaults(annotation);

        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> param : annotationParams.entrySet()) {
            if (param.getKey().getSimpleName().contentEquals(annotationProperty)) {
                return param.getValue();
            }
        }
        return null;
    }
}
