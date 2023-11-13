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


package org.kie.workbench.common.forms.adf.processors.util;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import org.kie.workbench.common.forms.adf.processors.FormDefinitionFieldData;

public class FormGenerationUtils {

    public static String fixClassName(String className) {
        return className.replaceAll("\\.", "_");
    }

    public static Collection<FieldInfo> extractFieldInfos(TypeElement typeElement, Predicate<VariableElement> predicate) {
        Map<String, FieldInfo> allFields = new HashMap<>();

        typeElement.getEnclosedElements().forEach(element -> {
            if (element.getKind().equals(ElementKind.FIELD)) {
                VariableElement fieldElement = (VariableElement) element;

                if (predicate != null && !predicate.test(fieldElement)) {
                    return;
                }

                FieldInfo fieldInfo = getInfoFromMap(fieldElement.getSimpleName().toString(), allFields);

                fieldInfo.setFieldElement(fieldElement);
            } else if (element.getKind().equals(ElementKind.METHOD)) {
                ExecutableElement method = (ExecutableElement) element;

                String methodName = method.getSimpleName().toString();

                if (isGetter(method)) {
                    String fieldName = extractFieldName(methodName, 3);
                    FieldInfo info = getInfoFromMap(fieldName,
                                                    allFields);
                    info.setGetter(methodName);
                } else if (isBooleanGetter(method)) {
                    String fieldName = extractFieldName(methodName, 2);
                    FieldInfo info = getInfoFromMap(fieldName, allFields);
                    info.setGetter(methodName);
                } else if (isSetter(method)) {
                    String fieldName = extractFieldName(methodName, 3);
                    FieldInfo info = getInfoFromMap(fieldName, allFields);
                    info.setSetter(methodName);
                }
            }
        });

        return allFields.values().stream().filter(fieldInfo -> fieldInfo.getFieldElement() != null).collect(Collectors.toCollection(() -> new ArrayList<>()));
    }

    private static FieldInfo getInfoFromMap(String fieldName,
                                            Map<String, FieldInfo> map) {
        FieldInfo info = map.get(fieldName);

        if (info == null) {
            info = new FieldInfo();
            map.put(fieldName,
                    info);
        }

        return info;
    }

    private static String extractFieldName(String methodName,
                                    int index) {
        if (methodName.length() <= index) {
            throw new IllegalArgumentException("MethodName ( '" + methodName + "' ) size < " + index);
        }
        return Introspector.decapitalize(methodName.substring(index));
    }

    private static boolean isGetter(final ExecutableElement method) {
        String name = method.getSimpleName().toString();
        if (method.getReturnType().getKind().equals(TypeKind.VOID)) {
            return false;
        }

        int parameterCount = method.getParameters().size();
        if (parameterCount != 0) {
            return false;
        }
        return (name.length() > 3 && name.startsWith("get"));
    }

    private static boolean isBooleanGetter(final ExecutableElement method) {
        String name = method.getSimpleName().toString();

        if (!method.getReturnType().getKind().equals(TypeKind.BOOLEAN)) {
            return false;
        }

        int parameterCount = method.getParameters().size();
        if (parameterCount != 0) {
            return false;
        }
        return (name.length() > 2 && name.startsWith("is"));
    }

    private static boolean isSetter(final ExecutableElement method) {
        String name = method.getSimpleName().toString();
        int parameterCount = method.getParameters().size();
        if (parameterCount != 1) {
            return false;
        }
        return (name.length() > 3 && name.startsWith("set"));
    }

    public static void sort(String firstName, List<FormDefinitionFieldData> elements) {

        if (elements == null || elements.isEmpty()) {
            return;
        }

        List<FormDefinitionFieldData> backup = new ArrayList<>(elements);

        elements.clear();

        FormDefinitionFieldData firstElement = backup.stream()
                .filter(formElement -> formElement.getName().equals(firstName))
                .findFirst()
                .orElse(backup.get(0));

        backup.remove(firstElement);

        elements.add(firstElement);

        buildChain(firstElement, backup, elements);

        if (!backup.isEmpty()) {
            for (Iterator<FormDefinitionFieldData> it = backup.iterator(); it.hasNext(); ) {
                FormDefinitionFieldData formElement = it.next();
                it.remove();
                if (!elements.contains(formElement)) {
                    elements.add(formElement);
                    buildChain(formElement, new ArrayList<>(backup), elements);
                }
            }
        }
    }

    protected static void buildChain(FormDefinitionFieldData previous, List<FormDefinitionFieldData> elements, List<FormDefinitionFieldData> originalList) {
        if (elements.isEmpty() || previous == null) {
            return;
        }

        List<FormDefinitionFieldData> aux = elements.stream().filter(formElement -> previous.getName().equals(formElement.getAfterElement())).collect(Collectors.toList());

        elements.removeAll(aux);

        for (FormDefinitionFieldData element : aux) {
            originalList.add(originalList.indexOf(previous) + 1, element);
            buildChain(element, elements, originalList);
        }
    }
}
