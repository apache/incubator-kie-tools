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

package org.kie.workbench.common.stunner.core.processors.definition;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.processing.Messager;

import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterFactory;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionAdapterProxy;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.processors.AbstractBindableAdapterGenerator;
import org.kie.workbench.common.stunner.core.processors.ProcessingContext;
import org.kie.workbench.common.stunner.core.processors.ProcessingDefinitionAnnotations;
import org.uberfire.annotations.processors.exceptions.GenerationException;

public class BindableDefinitionAdapterGenerator extends AbstractBindableAdapterGenerator {

    @Override
    protected String getTemplatePath() {
        return "BindableDefinitionAdapter.ftl";
    }

    public StringBuffer generate(final String packageName,
                                 final String className,
                                 final ProcessingContext processingContext,
                                 final Messager messager) throws GenerationException {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("packageName",
                 packageName);
        root.put("className",
                 className);
        root.put("parentAdapterClassName",
                 BindableDefinitionAdapterProxy.class.getName());
        root.put("generatedByClassName",
                 BindableDefinitionAdapterGenerator.class.getName());
        root.put("adapterFactoryClassName",
                 BindableAdapterFactory.class.getName());
        ProcessingDefinitionAnnotations processingDefinitionAnnotations = processingContext.getDefinitionAnnotations();
        addFields("baseTypes",
                  root,
                  processingDefinitionAnnotations.getBaseTypes());
        addFields("idFieldNames",
                  root,
                  processingDefinitionAnnotations.getIdFieldNames());
        addFields("categoryFieldNames",
                  root,
                  processingDefinitionAnnotations.getCategoryFieldNames());
        addFields("titleFieldNames",
                  root,
                  processingDefinitionAnnotations.getTitleFieldNames());
        addFields("descriptionFieldNames",
                  root,
                  processingDefinitionAnnotations.getDescriptionFieldNames());
        addFields("labelsFieldNames",
                  root,
                  processingDefinitionAnnotations.getLabelsFieldNames());
        addFields("graphFactoryFieldNames",
                  root,
                  processingDefinitionAnnotations.getGraphFactoryFieldNames());
        addSetFields("propertySetsFieldNames",
                     root,
                     processingDefinitionAnnotations.getPropertySetFieldNames());
        addSetFields("propertiesFieldNames",
                     root,
                     processingDefinitionAnnotations.getPropertyFieldNames());

        // Meta-properties.
        final Map<String, String> metaMap = new LinkedHashMap<>();
        processingContext.getMetaPropertyTypes().entrySet().stream().forEach(entry -> {
            metaMap.put(toStringMetaType(entry.getKey()),
                        entry.getValue());
        });
        root.put("metaTypeClass",
                 PropertyMetaTypes.class.getName());
        addFields("metaTypes",
                  root,
                  metaMap);

        //Generate code
        return writeTemplate(packageName,
                             className,
                             root,
                             messager);
    }

    private String toStringMetaType(final PropertyMetaTypes type) {
        return PropertyMetaTypes.class.getName() + "." + type.name();
    }
}
