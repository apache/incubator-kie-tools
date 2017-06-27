/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.preferences.processors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;

import org.uberfire.annotations.processors.GeneratorUtils;
import org.uberfire.preferences.shared.PropertyFormType;
import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;

/**
 * Class used to store preferences' properties information during template processing.
 */
public class PropertyData {

    private String fieldName;

    private String capitalizedFieldName;

    private String typeFullName;

    private boolean shared;

    private boolean subPreference;

    private PropertyFormType formType;

    private boolean privateAccess;

    private TypeKind typeKind;

    private List<String> validators;

    public PropertyData(final Element element,
                        final Property propertyAnnotation,
                        final AnnotationMirror propertyAnnotationMirror,
                        final Elements elementUtils) {
        fieldName = element.getSimpleName().toString();

        typeFullName = element.asType().toString();

        final char elementNameFirstLetter = fieldName.charAt(0);
        final char elementNameCapitalizedFirstLetter = Character.toUpperCase(elementNameFirstLetter);
        final String nameWithoutFirstLetter = fieldName.substring(1);
        capitalizedFieldName = elementNameCapitalizedFirstLetter + nameWithoutFirstLetter;

        shared = propertyAnnotation.shared();

        final TypeElement typeElement = elementUtils.getTypeElement(element.asType().toString());
        subPreference = typeElement != null && typeElement.getAnnotation(WorkbenchPreference.class) != null;

        formType = propertyAnnotation.formType();

        privateAccess = element.getModifiers().contains(Modifier.PRIVATE);

        typeKind = element.asType().getKind();

        setupValidators(propertyAnnotationMirror,
                        elementUtils);
    }

    void setupValidators(AnnotationMirror propertyAnnotationMirror,
                         Elements elementUtils) {
        final AnnotationValue validatorsAnnotationValue = GeneratorUtils.extractAnnotationPropertyValue(elementUtils,
                                                                                                        propertyAnnotationMirror,
                                                                                                        "validators");
        List<?> validators = (List<?>) GeneratorUtils.extractValue(validatorsAnnotationValue);
        this.validators = new ArrayList<>();
        if (validators != null) {
            this.validators.addAll((validators.stream()
                    .map(v -> v.toString())
                    .collect(Collectors.toList())));
        }
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getCapitalizedFieldName() {
        return capitalizedFieldName;
    }

    public String getTypeFullName() {
        return typeFullName;
    }

    public boolean isShared() {
        return shared;
    }

    public boolean isSubPreference() {
        return subPreference;
    }

    public PropertyFormType getFormType() {
        return formType;
    }

    public boolean isPrivateAccess() {
        return privateAccess;
    }

    public boolean isPrimitive() {
        return typeKind.isPrimitive();
    }

    public List<String> getValidators() {
        return validators;
    }

    public boolean hasValidators() {
        return validators != null && !validators.isEmpty();
    }

    public String getHashCodeFormula() {
        if (isPrimitive()) {
            switch (typeKind) {
                case BOOLEAN:
                    return "java.lang.Boolean.hashCode( " + getFieldAccessorCommand() + " )";
                case BYTE:
                    return "java.lang.Byte.hashCode( " + getFieldAccessorCommand() + " )";
                case SHORT:
                    return "java.lang.Short.hashCode( " + getFieldAccessorCommand() + " )";
                case INT:
                    return "java.lang.Integer.hashCode( " + getFieldAccessorCommand() + " )";
                case LONG:
                    return "java.lang.Long.hashCode( " + getFieldAccessorCommand() + " )";
                case CHAR:
                    return "java.lang.Character.hashCode( " + getFieldAccessorCommand() + " )";
                case FLOAT:
                    return "java.lang.Float.hashCode( " + getFieldAccessorCommand() + " )";
                case DOUBLE:
                    return "java.lang.Double.hashCode( " + getFieldAccessorCommand() + " )";
            }
        }

        return "( " + getFieldAccessorCommand() + " != null ? " + getFieldAccessorCommand() + ".hashCode() : 0 )";
    }

    public String getFieldAccessorCommand() {
        if (!isPrivateAccess()) {
            return fieldName;
        } else if (isPrimitive() && TypeKind.BOOLEAN.equals(typeKind)) {
            return "is" + capitalizedFieldName + "()";
        } else {
            return "get" + capitalizedFieldName + "()";
        }
    }
}