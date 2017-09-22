/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.data.modeller.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.service.DataObjectFinderService;
import org.kie.workbench.common.forms.editor.backend.service.impl.AbstractFormModelHandler;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandler;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasPlaceHolder;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.screens.datamodeller.model.maindomain.MainDomainAnnotations;
import org.kie.workbench.common.services.backend.project.ProjectClassLoaderHelper;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;

@Dependent
public class DataObjectFormModelHandler extends AbstractFormModelHandler<DataObjectFormModel> {

    public static final String SERIAL_VERSION_UID = "serialVersionUID";
    public static final String[] RESTRICTED_PROPERTY_NAMES = new String[]{SERIAL_VERSION_UID};

    public static final String PERSISTENCE_ANNOTATION = "javax.persistence.Id";
    public static final String[] RESTRICTED_ANNOTATIONS = new String[]{PERSISTENCE_ANNOTATION};

    private static final Logger logger = LoggerFactory.getLogger(DataObjectFormModelHandler.class);

    protected DataObjectFinderService finderService;

    protected DataObject dataObject;

    protected FieldManager fieldManager;

    @Inject
    public DataObjectFormModelHandler(KieProjectService projectService,
                                      ProjectClassLoaderHelper classLoaderHelper,
                                      DataObjectFinderService finderService,
                                      FieldManager fieldManager) {
        super(projectService,
              classLoaderHelper);

        this.finderService = finderService;
        this.fieldManager = fieldManager;
    }

    @Override
    public Class<DataObjectFormModel> getModelType() {
        return DataObjectFormModel.class;
    }

    @Override
    protected void initialize() {
        super.checkInitialized();

        dataObject = finderService.getDataObject(formModel.getClassName(),
                                                 path);
    }

    @Override
    protected void log(String message,
                       Exception e) {
        logger.warn(message,
                    e);
    }

    @Override
    protected List<ModelProperty> getCurrentModelProperties() {
        return getDataObjectProperties(dataObject);
    }

    public DataObjectFormModel createFormModel(DataObject dataObject,
                                               Path path) {
        this.path = path;

        initClassLoader();

        DataObjectFormModel formModel = new DataObjectFormModel(dataObject.getName(),
                                                                dataObject.getClassName());

        formModel.getProperties().clear();

        formModel.getProperties().addAll(getDataObjectProperties(dataObject));

        return formModel;
    }

    protected List<ModelProperty> getDataObjectProperties(DataObject dataObject) {
        List<ModelProperty> properties = new ArrayList<>();
        dataObject.getProperties().forEach(property -> {
            if (isValidDataObjectProperty(property)) {
                Optional<ModelProperty> optional = createModelProperty(property.getName(),
                                                                       property.getClassName(),
                                                                       property.isMultiple());

                if (optional.isPresent()) {
                    properties.add(optional.get());
                }
            }
        });
        return properties;
    }

    @Override
    public FormModelHandler<DataObjectFormModel> newInstance() {
        return new DataObjectFormModelHandler(projectService,
                                              classLoaderHelper,
                                              finderService,
                                              fieldManager);
    }

    @Override
    protected List<FieldDefinition> doGenerateModelFields() {
        return formModel.getProperties().stream().map(this::doCreateFieldDefinition).collect(Collectors.toList());
    }

    @Override
    protected FieldDefinition doCreateFieldDefinition(ModelProperty property) {

        Optional<ObjectProperty> objectPropertyOptional = Optional.ofNullable(dataObject.getProperty(property.getName()));

        if (!objectPropertyOptional.isPresent()) {
            return null;
        }

        ObjectProperty objectProperty = objectPropertyOptional.get();

        if (!isValidDataObjectProperty(objectProperty)) {
            return null;
        }

        Optional<FieldDefinition> fieldOptional = Optional.ofNullable(fieldManager.getDefinitionByDataType(property.getTypeInfo()));

        if (!fieldOptional.isPresent()) {
            return null;
        }

        FieldDefinition field = fieldOptional.get();
        field.setName(property.getName());
        String label = getPropertyLabel(objectProperty);
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
