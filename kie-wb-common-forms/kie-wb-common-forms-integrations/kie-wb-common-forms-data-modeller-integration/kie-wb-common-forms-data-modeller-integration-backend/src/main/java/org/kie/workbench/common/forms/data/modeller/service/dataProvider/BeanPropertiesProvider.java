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

package org.kie.workbench.common.forms.data.modeller.service.dataProvider;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.forms.data.modeller.service.shared.ModelFinderService;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SystemSelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.uberfire.backend.vfs.Path;

@Dependent
public class BeanPropertiesProvider implements SystemSelectorDataProvider {

    private ModelFinderService modelFinderService;

    @Inject
    public BeanPropertiesProvider(ModelFinderService modelFinderService) {
        this.modelFinderService = modelFinderService;
    }

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    public SelectorData getSelectorData(FormRenderingContext context) {

        HashMap<String, String> values = new HashMap<>();

        if (context instanceof FormEditorRenderingContext && context.getParentContext() != null) {
            if (context.getParentContext().getModel() instanceof MultipleSubFormFieldDefinition) {

                FormEditorRenderingContext editorContext = (FormEditorRenderingContext) context;

                if (context.getParentContext() != null) {
                    MultipleSubFormFieldDefinition subForm = (MultipleSubFormFieldDefinition) context.getParentContext().getModel();

                    Path path = editorContext.getFormPath();
                    String typeName = subForm.getStandaloneClassName();

                    final TableColumnMeta currentMeta = (TableColumnMeta) context.getModel();

                    Set<String> unavailableProperties = subForm.getColumnMetas().stream().map(TableColumnMeta::getProperty).collect(Collectors.toSet());

                    if (currentMeta != null && !StringUtils.isEmpty(currentMeta.getProperty())) {
                        unavailableProperties.remove(currentMeta.getProperty());
                    }

                    modelFinderService.getModel(typeName, path).getProperties().stream()
                            .filter(property -> !unavailableProperties.contains(property.getName()))
                            .forEachOrdered(property -> values.put(property.getName(), property.getName()));
                }
            }
        }
        return new SelectorData(values,null);
    }
}
