/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.data.modeller.service.impl;

import org.kie.workbench.common.screens.datamodeller.model.maindomain.MainDomainAnnotations;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationDefinitionImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;

public abstract class AbstractDataObjectTest {

    public static final String LABEL_SUFFIX = "Label_";

    protected ObjectProperty addProperty(DataObject dataObject,
                                         String propertyName,
                                         String className,
                                         boolean multiple,
                                         boolean withLabels) {

        ObjectProperty property = dataObject.addProperty(propertyName,
                                                         className,
                                                         multiple);

        if (withLabels) {
            Annotation labelAnnotation = new AnnotationImpl(new AnnotationDefinitionImpl(MainDomainAnnotations.LABEL_ANNOTATION));
            labelAnnotation.setValue(MainDomainAnnotations.VALUE_PARAM,
                                     LABEL_SUFFIX + propertyName);
            property.addAnnotation(labelAnnotation);
        }

        return property;
    }
}
