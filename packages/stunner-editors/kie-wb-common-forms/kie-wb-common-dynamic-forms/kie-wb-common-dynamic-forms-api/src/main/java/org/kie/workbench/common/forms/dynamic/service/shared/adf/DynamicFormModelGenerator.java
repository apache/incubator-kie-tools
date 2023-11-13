/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.dynamic.service.shared.adf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.adf.engine.shared.FormBuildingService;
import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.util.PropertyValueExtractor;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.StaticModelFormRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.EntityRelationField;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.HasNestedForm;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.IsCRUDDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta;
import org.kie.workbench.common.forms.model.FormDefinition;

@Dependent
public class DynamicFormModelGenerator {

    private FormBuildingService formBuildingService;

    private PropertyValueExtractor propertyValueExtractor;

    @Inject
    public DynamicFormModelGenerator(FormBuildingService formBuildingService,
                                     PropertyValueExtractor propertyValueExtractor) {
        this.formBuildingService = formBuildingService;
        this.propertyValueExtractor = propertyValueExtractor;
    }

    public StaticModelFormRenderingContext getContextForModel(Object model, FormElementFilter... filters) {
        Objects.requireNonNull(model, "Parameter named 'model' should be not null!");

        Optional<FormElementFilter[]> optional = Optional.ofNullable(filters);
        Stream<FormElementFilter> streamFilter = optional.map(value -> Stream.of(value)).orElseGet(Stream::empty);

        List<FormElementFilter> rootFormElemenFilters = new ArrayList<>();
        List<FormElementFilter> nestedFormFilters = new ArrayList<>();

        streamFilter.forEach(filter -> {
            if(!filter.getElementName().contains(".")) {
                rootFormElemenFilters.add(filter);
            } else {
                nestedFormFilters.add(filter);
            }
        });

        FormDefinition formDefinition = formBuildingService.generateFormForModel(model, rootFormElemenFilters.stream().toArray(FormElementFilter[]::new));

        if (formDefinition == null) {
            return null;
        }

        StaticModelFormRenderingContext context = new StaticModelFormRenderingContext(String.valueOf(System.currentTimeMillis()));

        context.setModel(model);
        context.setRootForm(formDefinition);

        if (context.getModel() != null) {
            initNestedFormSettings(formDefinition, context.getModel(), context, nestedFormFilters);
        } else {
            initNestedFormSettings(formDefinition,null, context, nestedFormFilters);
        }

        return context;
    }

    private void initNestedFormSettings(final FormDefinition form,
                                        final Object model,
                                        final StaticModelFormRenderingContext context,
                                        final Collection<FormElementFilter> nestedFormFilters) {

        form.getFields().forEach(field -> {
            if (field instanceof HasNestedForm) {
                HasNestedForm hasNestedForm = (HasNestedForm) field;

                if (!context.getAvailableForms().containsKey(((HasNestedForm) field).getNestedForm())) {
                    addNestedForm(model,
                                  hasNestedForm.getNestedForm(),
                                  field.getName(),
                                  context,
                                  getNestedFiltersStream(field.getName(), nestedFormFilters));
                }
            } else if (field instanceof IsCRUDDefinition) {
                IsCRUDDefinition isCRUDDefinitionField = (IsCRUDDefinition) field;

                if (!context.getAvailableForms().containsKey(isCRUDDefinitionField.getCreationForm())) {
                    addNestedForm(field.getStandaloneClassName(), context, getNestedFiltersStream(field.getName(), nestedFormFilters));
                }

                if (isCRUDDefinitionField.getColumnMetas() == null || isCRUDDefinitionField.getColumnMetas().isEmpty()) {
                    if (isCRUDDefinitionField.getColumnMetas() == null) {
                        isCRUDDefinitionField.setColumnMetas(new ArrayList<>());
                    }

                    FormDefinition nestedForm = context.getAvailableForms().get(isCRUDDefinitionField.getCreationForm());

                    nestedForm.getFields().forEach(nestedField -> {
                        if (nestedField instanceof EntityRelationField) {
                            return;
                        }
                        isCRUDDefinitionField.getColumnMetas().add(new TableColumnMeta(nestedField.getLabel(),
                                                                                       nestedField.getBinding()));
                    });
                }
            }
        });
    }

    private Collection<FormElementFilter> getNestedFiltersStream(String name, Collection<FormElementFilter> filterStream) {
        return filterStream.stream().filter(filter -> filter.getElementName().startsWith(name + ".")).map(filter -> {
            String elementName = filter.getElementName().substring(name.length() + 1);
            return new FormElementFilter(elementName, filter.getPredicate());
        }).collect(Collectors.toList());
    }

    private void addNestedForm(final Object parentModel,
                               final String className,
                               final String fieldName,
                               final StaticModelFormRenderingContext context,
                               final Collection<FormElementFilter> nestedElementFilters) {
        if (!context.getAvailableForms().containsKey(className)) {
            if (parentModel != null) {

                Object value = propertyValueExtractor.readPropertyValue(parentModel,
                                                                        fieldName);

                if (value != null) {
                    addNestedForm(value, context, nestedElementFilters);
                } else {
                    addNestedForm(className, context, nestedElementFilters);
                }
            } else {
                addNestedForm(className, context, nestedElementFilters);
            }
        }
    }

    private void addNestedForm(String className,
                               StaticModelFormRenderingContext context,
                               Collection<FormElementFilter> nestedFormFilters) {
        if (!context.getAvailableForms().containsKey(className)) {
            FormDefinition nested = formBuildingService.generateFormForClassName(className, nestedFormFilters.stream().toArray(FormElementFilter[]::new));
            context.getAvailableForms().put(className,
                                            nested);
            initNestedFormSettings(nested,
                                   null,
                                   context,
                                   nestedFormFilters);
        }
    }

    private void addNestedForm(Object model,
                               StaticModelFormRenderingContext context,
                               Collection<FormElementFilter> nestedFormFilters) {
        if (!context.getAvailableForms().containsKey(model.getClass().getName())) {
            FormDefinition nested = formBuildingService.generateFormForModel(model, nestedFormFilters.stream().toArray(FormElementFilter[]::new));
            context.getAvailableForms().put(model.getClass().getName(),
                                            nested);
            initNestedFormSettings(nested,
                                   model,
                                   context, nestedFormFilters);
        }
    }
}
