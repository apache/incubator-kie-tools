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
package org.kie.workbench.common.forms.migration.legacy.services;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.forms.migration.legacy.model.FieldType;
import org.kie.workbench.common.forms.migration.legacy.services.impl.FieldTypeBuilder;

public class FieldTypeManager {

    private static FieldTypeManager instance;

    private List<FieldType> fieldTypes;
    private List<FieldType> decoratorTypes;
    private List<FieldType> complexTypes;

    protected FieldType customType = new FieldType();

    private FieldTypeManager() {

        fieldTypes = FieldTypeBuilder.getSimpleFieldTypes();
        complexTypes = FieldTypeBuilder.getComplexTypesList();
        decoratorTypes = FieldTypeBuilder.getDecoratorTypesList();

        customType.setCode("CustomInput");
        customType.setFieldClass("*");
        customType.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.customInput.CustomInputFieldHandler");
    }

    public FieldType getTypeByCode(String typeCode, String fieldClass) {
        if (customType.getCode().equals(typeCode)) {
            FieldType ft = new FieldType(customType);
            ft.setFieldClass(fieldClass);
            return ft;
        }

        for (FieldType fieldType : fieldTypes) {
            if (fieldType.getCode().equals(typeCode)) {
                return fieldType;
            }
        }

        for (FieldType decorator : decoratorTypes) {
            if (decorator.getCode().equals(typeCode)) {
                return decorator;
            }
        }

        for (FieldType complexType : complexTypes) {
            if (complexType.getCode().equals(typeCode)) {
                return complexType;
            }
        }
        return null;
    }

    public FieldType getTypeByCode(String typeCode) {
        return getTypeByCode(typeCode, null);
    }

    public FieldType getTypeByClass(String className) {
        return getTypeByClass(className, null);
    }

    public FieldType getTypeByClass(String className, String bag) {
        if (StringUtils.isEmpty(className)) {
            return null;
        }

        if (!StringUtils.isEmpty(bag)) {
            if (getSimpleTypeByClass(bag) != null) {
                return getSimpleTypeByClass(className);
            } else {
                return getComplexTypeByClass(className);
            }
        }

        FieldType fieldType = getSimpleTypeByClass(className);

        if (fieldType != null) {
            return fieldType;
        }

        fieldType = getDecoratorTypeByClass(className);

        if (fieldType != null) {
            return fieldType;
        }

        fieldType = getComplexTypeByClass(className);

        if (fieldType != null) {
            return fieldType;
        }

        return getTypeByCode("Subform");
    }

    public FieldType getSimpleTypeByClass(String className) {
        return getFieldTypeByClass(fieldTypes, className);
    }

    public FieldType getComplexTypeByClass(String className) {
        return getFieldTypeByClass(complexTypes, className);
    }

    public FieldType getDecoratorTypeByClass(String className) {
        return getFieldTypeByClass(decoratorTypes, className);
    }

    protected FieldType getFieldTypeByClass(List<FieldType> fieldTypes, String className) {
        for (FieldType fieldType : fieldTypes) {
            if (fieldType.getFieldClass().equals(className)) {
                return fieldType;
            }
        }
        return null;
    }

    public static FieldTypeManager get() {
        if(instance == null) {
            instance = new FieldTypeManager();
        }
        return instance;
    }
}
