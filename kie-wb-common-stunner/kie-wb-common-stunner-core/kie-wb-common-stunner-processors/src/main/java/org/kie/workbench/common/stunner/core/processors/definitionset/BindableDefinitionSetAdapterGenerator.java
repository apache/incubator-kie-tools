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

package org.kie.workbench.common.stunner.core.processors.definitionset;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Messager;

import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterFactory;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionSetAdapterProxy;
import org.kie.workbench.common.stunner.core.processors.AbstractBindableAdapterGenerator;
import org.kie.workbench.common.stunner.core.processors.ProcessingDefinitionSetAnnotations;
import org.uberfire.annotations.processors.exceptions.GenerationException;

public class BindableDefinitionSetAdapterGenerator extends AbstractBindableAdapterGenerator {

    @Override
    protected String getTemplatePath() {
        return "BindableDefinitionSetAdapter.ftl";
    }

    public StringBuffer generate(final String packageName,
                                 final String className,
                                 final ProcessingDefinitionSetAnnotations processingDefinitionSetAnnotations,
                                 final Messager messager) throws GenerationException {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("packageName",
                 packageName);
        root.put("className",
                 className);
        root.put("parentAdapterClassName",
                 BindableDefinitionSetAdapterProxy.class.getName());
        root.put("adapterFactoryClassName",
                 BindableAdapterFactory.class.getName());
        root.put("generatedByClassName",
                 BindableDefinitionSetAdapterGenerator.class.getName());
        addFields("graphFactoryTypes",
                  root,
                  processingDefinitionSetAnnotations.getGraphFactoryTypes());
        addFields("qualifiers",
                  root,
                  processingDefinitionSetAnnotations.getQualifiers());
        addFields("valuePropNames",
                  root,
                  processingDefinitionSetAnnotations.getDescriptionFieldNames());
        root.put("definitionIds",
                 processingDefinitionSetAnnotations.getDefinitionIds());
        root.put("definitionIdsSize",
                 processingDefinitionSetAnnotations.getDefinitionIds().size());
        // Generate code from the template.
        return writeTemplate(packageName,
                             className,
                             root,
                             messager);
    }
}
