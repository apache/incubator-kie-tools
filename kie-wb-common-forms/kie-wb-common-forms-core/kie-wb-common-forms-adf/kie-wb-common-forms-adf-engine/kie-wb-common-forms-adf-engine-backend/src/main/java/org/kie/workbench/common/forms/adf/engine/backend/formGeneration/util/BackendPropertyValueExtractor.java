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

package org.kie.workbench.common.forms.adf.engine.backend.formGeneration.util;

import javax.enterprise.context.Dependent;

import org.apache.commons.beanutils.PropertyUtils;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.util.impl.AbstractPropertyValueExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
public class BackendPropertyValueExtractor extends AbstractPropertyValueExtractor {

    private static final Logger logger = LoggerFactory.getLogger(BackendPropertyValueExtractor.class);

    @Override
    protected Object readValue(Object model,
                               String propertyName) {
        try {
            if (PropertyUtils.getPropertyDescriptor(model,
                                                    propertyName) != null) {
                return PropertyUtils.getProperty(model,
                                                 propertyName);
            }
        } catch (Exception e) {
            logger.warn("Error getting property '{}' from object '{}'",
                        propertyName,
                        model);
            logger.warn("Caused by:",
                        e);
        }
        return null;
    }
}
