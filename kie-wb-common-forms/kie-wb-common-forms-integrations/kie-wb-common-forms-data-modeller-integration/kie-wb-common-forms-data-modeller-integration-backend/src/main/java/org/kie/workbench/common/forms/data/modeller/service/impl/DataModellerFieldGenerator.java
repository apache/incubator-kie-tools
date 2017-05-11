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

package org.kie.workbench.common.forms.data.modeller.service.impl;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasPlaceHolder;
import org.kie.workbench.common.forms.model.FieldDataType;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.service.FieldManager;
import org.kie.workbench.common.screens.datamodeller.model.maindomain.MainDomainAnnotations;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;

@Dependent
public class DataModellerFieldGenerator {

    public static final String SERIAL_VERSION_UID = "serialVersionUID";
    public static final String[] RESTRICTED_PROPERTY_NAMES = new String[]{SERIAL_VERSION_UID};

    public static final String PERSISTENCE_ANNOTATION = "javax.persistence.Id";
    public static final String[] RESTRICTED_ANNOTATIONS = new String[]{PERSISTENCE_ANNOTATION};

    private FieldManager fieldManager;

    @Inject
    public DataModellerFieldGenerator(FieldManager fieldManager) {
        this.fieldManager = fieldManager;
    }

    public List<FieldDefinition> getFieldsFromDataObject(String holderName,
                                                         DataObject dataObject) {
        List<FieldDefinition> result = new ArrayList<>();
        if (dataObject != null) {
            for (ObjectProperty property : dataObject.getProperties()) {
                if (!isValidDataObjectProperty(property)) {
                    continue;
                }

                FieldDefinition field = createFieldDefinition(holderName,
                                                              property);
                result.add(field);
            }
        }
        return result;
    }

    public FieldDefinition createFieldDefinition(String holderName,
                                                 ObjectProperty property) {
        String propertyName = holderName + "_" + property.getName();

        FieldDefinition field = fieldManager.getDefinitionByDataType(new FieldDataType(property.getClassName(),
                                                                                       property.isMultiple(),
                                                                                       false));

        if (field == null) {
            return null;
        }

        field.setName(propertyName);
        String label = getPropertyLabel(property);
        field.setLabel(label);
        field.setBinding(property.getName());

        if (field instanceof HasPlaceHolder) {
            ((HasPlaceHolder) field).setPlaceHolder(label);
        }
        return field;
    }

    private String getPropertyLabel(ObjectProperty property) {
        Annotation labelAnnotation = property.getAnnotation(MainDomainAnnotations.LABEL_ANNOTATION);
        if (labelAnnotation != null) {
            return labelAnnotation.getValue(MainDomainAnnotations.VALUE_PARAM).toString();
        }

        return property.getName();
    }

    public static boolean isValidDataObjectProperty(ObjectProperty property) {
        if (ArrayUtils.contains(RESTRICTED_PROPERTY_NAMES,
                                property.getName())) {
            return false;
        }

        for (String annotation : RESTRICTED_ANNOTATIONS) {
            if (property.getAnnotation(annotation) != null) {
                return false;
            }
        }
        return true;
    }
}
