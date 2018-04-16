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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.service.DataObjectFinderService;
import org.kie.workbench.common.forms.editor.backend.service.impl.AbstractFormModelHandler;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandler;
import org.kie.workbench.common.forms.editor.service.backend.SourceFormModelNotFoundException;
import org.kie.workbench.common.forms.fields.shared.model.meta.entries.FieldPlaceHolderEntry;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.impl.meta.entries.FieldLabelEntry;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.screens.datamodeller.model.maindomain.MainDomainAnnotations;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;

@Dependent
public class DataObjectFormModelHandler extends AbstractFormModelHandler<DataObjectFormModel> {

    private static final String BUNDLE = "org.kie.workbench.common.forms.data.modeller.service.BackendConstants";
    private static final String SHORT_KEY = "DataObjectFormModelHandler.shortMessage";
    private static final String FULL_KEY = "DataObjectFormModelHandler.fullMessage";
    private static final String DATA_OBJECT_KEY = "DataObjectFormModelHandler.dataObject";

    public static final String SERIAL_VERSION_UID = "serialVersionUID";
    public static final String[] RESTRICTED_PROPERTY_NAMES = new String[]{SERIAL_VERSION_UID};

    public static final String PERSISTENCE_ANNOTATION = "javax.persistence.Id";
    public static final String[] RESTRICTED_ANNOTATIONS = new String[]{PERSISTENCE_ANNOTATION};

    private static final Logger logger = LoggerFactory.getLogger(DataObjectFormModelHandler.class);

    protected DataObjectFinderService finderService;

    protected DataObject dataObject;

    protected FieldManager fieldManager;

    @Inject
    public DataObjectFormModelHandler(KieModuleService moduleService,
                                      ModuleClassLoaderHelper classLoaderHelper,
                                      DataObjectFinderService finderService,
                                      FieldManager fieldManager) {
        super(moduleService,
              classLoaderHelper);

        this.finderService = finderService;
        this.fieldManager = fieldManager;
    }

    public static boolean isValidDataObjectProperty(final ObjectProperty property) {
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

    @Override
    public Class<DataObjectFormModel> getModelType() {
        return DataObjectFormModel.class;
    }

    @Override
    protected void initialize() {
        checkInitialized();

        dataObject = finderService.getDataObject(formModel.getClassName(), path);
    }

    @Override
    public void checkSourceModel() throws SourceFormModelNotFoundException {
        checkInitialized();

        if (dataObject == null) {
            String[] params = new String[]{formModel.getClassName()};

            throwException(BUNDLE, SHORT_KEY, params, FULL_KEY, params, DATA_OBJECT_KEY);
        }
    }

    @Override
    protected void log(String message,
                       Exception e) {
        logger.warn(message, e);
    }

    @Override
    protected List<ModelProperty> getCurrentModelProperties() {
        return getDataObjectProperties(dataObject);
    }

    public DataObjectFormModel createFormModel(DataObject dataObject, Path path) {
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
                    ModelProperty modelProperty = optional.get();

                    extractMetaData(property,
                                    modelProperty);

                    properties.add(modelProperty);
                }
            }
        });
        return properties;
    }

    @Override
    public FormModelHandler<DataObjectFormModel> newInstance() {
        return new DataObjectFormModelHandler(moduleService,
                                              classLoaderHelper,
                                              finderService,
                                              fieldManager);
    }

    private void extractMetaData(ObjectProperty property,
                                 ModelProperty modelProperty) {
        Annotation labelAnnotation = property.getAnnotation(MainDomainAnnotations.LABEL_ANNOTATION);
        if (labelAnnotation != null) {
            String label = labelAnnotation.getValue(MainDomainAnnotations.VALUE_PARAM).toString();
            modelProperty.getMetaData().addEntry(new FieldLabelEntry(label));
            modelProperty.getMetaData().addEntry(new FieldPlaceHolderEntry(label));
        }
    }
}
