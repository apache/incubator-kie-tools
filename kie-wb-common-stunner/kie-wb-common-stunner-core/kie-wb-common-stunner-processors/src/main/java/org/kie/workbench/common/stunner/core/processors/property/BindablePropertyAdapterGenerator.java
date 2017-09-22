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

package org.kie.workbench.common.stunner.core.processors.property;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Messager;

import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterFactory;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindablePropertyAdapterProxy;
import org.kie.workbench.common.stunner.core.processors.AbstractBindableAdapterGenerator;
import org.kie.workbench.common.stunner.core.processors.ProcessingPropertyAnnotations;
import org.uberfire.annotations.processors.exceptions.GenerationException;

public class BindablePropertyAdapterGenerator extends AbstractBindableAdapterGenerator {

    @Override
    protected String getTemplatePath() {
        return "BindablePropertyAdapter.ftl";
    }

    public StringBuffer generate(final String packageName,
                                 final String className,
                                 final ProcessingPropertyAnnotations processingPropertyAnnotations,
                                 final Messager messager) throws GenerationException {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("packageName",
                 packageName);
        root.put("className",
                 className);
        root.put("parentAdapterClassName",
                 BindablePropertyAdapterProxy.class.getName());
        root.put("adapterFactoryClassName",
                 BindableAdapterFactory.class.getName());
        root.put("generatedByClassName",
                 BindablePropertyAdapterGenerator.class.getName());
        addFields("valuePropNames",
                  root,
                  processingPropertyAnnotations.getValueFieldNames());
        addFields("defaultValuePropNames",
                  root,
                  processingPropertyAnnotations.getDefaultValueFieldNames());
        addFields("captionPropNames",
                  root,
                  processingPropertyAnnotations.getCaptionFieldNames());
        addFields("descriptionPropNames",
                  root,
                  processingPropertyAnnotations.getDescriptionFieldNames());
        addFields("propTypePropNames",
                  root,
                  processingPropertyAnnotations.getTypeFieldNames());
        addFields("readOnlyPropNames",
                  root,
                  processingPropertyAnnotations.getReadOnlyFieldNames());
        addFields("optionalPropNames",
                  root,
                  processingPropertyAnnotations.getOptionalFieldNames());
        addFields("allowedValuesPropNames",
                  root,
                  processingPropertyAnnotations.getAllowedValuesFieldNames());

        //Generate code
        return writeTemplate(packageName,
                             className,
                             root,
                             messager);
    }
}
