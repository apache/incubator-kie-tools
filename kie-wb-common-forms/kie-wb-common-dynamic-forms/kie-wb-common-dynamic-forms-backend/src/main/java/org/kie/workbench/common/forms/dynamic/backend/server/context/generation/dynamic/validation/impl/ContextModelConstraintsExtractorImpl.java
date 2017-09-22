/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.validation.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.validation.Validation;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import org.jboss.errai.config.rebind.EnvUtil;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.validation.ContextModelConstraintsExtractor;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.validation.DynamicModelConstraints;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.validation.FieldConstraint;
import org.kie.workbench.common.forms.model.JavaFormModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
@Default
public class ContextModelConstraintsExtractorImpl implements ContextModelConstraintsExtractor,
                                                             Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ContextModelConstraintsExtractorImpl.class);

    @Override
    public void readModelConstraints(MapModelRenderingContext clientRenderingContext,
                                     ClassLoader classLoader) {
        if (clientRenderingContext == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        if (classLoader == null) {
            throw new IllegalArgumentException("ClassLoader cannot be null");
        }

        clientRenderingContext.getAvailableForms().values().forEach(formDefinition -> {
            if (formDefinition.getModel() instanceof JavaFormModel) {
                JavaFormModel javaModel = (JavaFormModel) formDefinition.getModel();

                if (clientRenderingContext.getModelConstraints().containsKey(javaModel)) {
                    return;
                }

                Class clazz = null;
                try {
                    clazz = classLoader.loadClass(javaModel.getType());
                    if (clazz == null) {
                        clazz = getClass().forName(javaModel.getType());
                    }
                } catch (ClassNotFoundException e) {
                    // maybe Class is not on the project ClassLoader, let's try on the main ClassLoader
                    try {
                        clazz = getClass().forName(javaModel.getType());
                    } catch (ClassNotFoundException e1) {
                        // ops! class not available on the main classLoader
                    }
                }

                if (clazz == null) {
                    logger.warn("Unable to find class for type {} on any classLoader. Skipping annotation processing",
                                javaModel.getType());
                } else {

                    BeanDescriptor descriptor = Validation.buildDefaultValidatorFactory().getValidator().getConstraintsForClass(clazz);

                    Set<PropertyDescriptor> properties = descriptor.getConstrainedProperties();

                    DynamicModelConstraints constraints = new DynamicModelConstraints(javaModel.getType());

                    clientRenderingContext.getModelConstraints().put(javaModel.getType(),
                                                                     constraints);

                    properties.forEach(property -> {

                        property.getConstraintDescriptors().forEach(constraintDescriptor -> {

                            Map<String, Object> attributes = new HashMap<>();

                            constraintDescriptor.getAttributes().forEach((key, value) -> {

                                if (key.equals("payload") || key.equals("groups")) {
                                    return;
                                }

                                Object portableValue;

                                if (EnvUtil.isPortableType(value.getClass())) {
                                    portableValue = value;
                                } else {
                                    portableValue = value.toString();
                                }
                                attributes.put(key,
                                               portableValue);
                            });

                            constraints.addConstraintForField(property.getPropertyName(),
                                                              new FieldConstraint(constraintDescriptor.getAnnotation().annotationType().getName(),
                                                                                  attributes));
                        });
                    });
                }
            }
        });
    }
}
