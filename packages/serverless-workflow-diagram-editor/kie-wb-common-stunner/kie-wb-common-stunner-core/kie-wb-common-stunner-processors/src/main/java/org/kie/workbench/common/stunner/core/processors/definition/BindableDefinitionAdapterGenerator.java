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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.processing.Messager;

import org.kie.workbench.common.stunner.core.definition.adapter.binding.DefinitionAdapterBindings;
import org.kie.workbench.common.stunner.core.processors.AbstractBindableAdapterGenerator;
import org.kie.workbench.common.stunner.core.processors.ProcessingContext;
import org.kie.workbench.common.stunner.core.processors.ProcessingDefinitionAnnotations;
import org.uberfire.annotations.processors.GenerationException;

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
        root.put("generatedByClassName",
                 BindableDefinitionAdapterGenerator.class.getName());
        ProcessingDefinitionAnnotations processingDefinitionAnnotations = processingContext.getDefinitionAnnotations();
        // Bindings.
        Map<String, List<String>> fieldNames = processingDefinitionAnnotations.getPropertyFieldNames();
        Map<String, String> defAdapterBindings = new HashMap<>();
        fieldNames.forEach((type, fields) -> {
            String baseType = processingDefinitionAnnotations.getBaseTypes().get(type);
            baseType = null != baseType && baseType.trim().length() > 0 ? baseType : "java.lang.Object";
            String graphFactory = processingDefinitionAnnotations.getGraphFactory().get(type);
            String idField = processingDefinitionAnnotations.getIdFieldNames().get(type);
            idField = null != idField && idField.trim().length() > 0 ? "\"" + idField + "\"" : null;
            String titleField = processingDefinitionAnnotations.getTitleFieldNames().get(type);
            titleField = null != titleField && titleField.trim().length() > 0 ? "\"" + titleField + "\"" : null;
            String labelsField = "\"" + processingDefinitionAnnotations.getLabelsFieldNames().get(type) + "\"";
            String categoryField = "\"" + processingDefinitionAnnotations.getCategoryFieldNames().get(type) + "\"";
            String descriptionField = processingDefinitionAnnotations.getDescriptionFieldNames().get(type);
            descriptionField = null != descriptionField && descriptionField.trim().length() > 0 ? "\"" + descriptionField + "\"" : null;
            List<String> propertyFields = processingDefinitionAnnotations.getPropertyFieldNames().get(type);
            String propertyFieldsArray = propertyFields.stream().map(f -> "\"" + f + "\"").collect(Collectors.joining(","));
            List<Boolean> typedPropertyFields = processingDefinitionAnnotations.getTypedPropertyFields().get(type);
            String typedPropertyFieldsArray = typedPropertyFields.stream().map(Object::toString).collect(Collectors.joining(","));
            DefinitionAdapterBindings.PropertyMetaTypes metaTypes = processingContext.getMetaPropertyTypesFields().get(type);
            defAdapterBindings.put(type,
                                   "new DefinitionAdapterBindings()" +
                                           ".setBaseType(" + baseType + ".class)" +
                                           ".setGraphFactory(" + graphFactory + ".class)" +
                                           ".setIdField(" + idField + ")" +
                                           ".setTitleField(" + titleField + ")" +
                                           ".setLabelsField(" + labelsField + ")" +
                                           ".setCategoryField(" + categoryField + ")" +
                                           ".setDescriptionField(" + descriptionField + ")" +
                                           ".setPropertiesFieldNames(Arrays.asList(" + propertyFieldsArray + "))" +
                                           ".setTypedPropertyFields(Arrays.asList(" + typedPropertyFieldsArray + "))" +
                                           ".setMetaTypes(DefinitionAdapterBindings.PropertyMetaTypes.parse(\"" + metaTypes.format() + "\"))"
            );
        });
        addFields("bindings",
                  root,
                  defAdapterBindings);

        //Generate code
        return writeTemplate(packageName,
                             className,
                             root,
                             messager);
    }
}
