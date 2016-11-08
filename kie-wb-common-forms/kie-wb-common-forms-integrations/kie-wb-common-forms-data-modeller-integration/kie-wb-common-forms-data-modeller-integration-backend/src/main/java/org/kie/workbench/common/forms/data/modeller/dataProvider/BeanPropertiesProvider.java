/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.data.modeller.dataProvider;

import java.util.HashMap;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.data.modeller.service.DataObjectFinderService;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SystemSelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.model.impl.relations.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.model.impl.relations.TableColumnMeta;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.forms.editor.service.FormEditorRenderingContext;
import org.uberfire.backend.vfs.Path;

@Dependent
public class BeanPropertiesProvider implements SystemSelectorDataProvider {

    @Inject
    private DataObjectFinderService dataObjectFinderService;

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    public SelectorData getSelectorData( FormRenderingContext context ) {

        HashMap<String, String> values = new HashMap<>();

        if ( context instanceof FormEditorRenderingContext && context.getParentContext() != null ) {
            if ( context.getParentContext().getModel() instanceof MultipleSubFormFieldDefinition ) {

                FormEditorRenderingContext editorContext = (FormEditorRenderingContext) context;

                MultipleSubFormFieldDefinition subForm = (MultipleSubFormFieldDefinition) context.getParentContext().getModel();

                Path path = editorContext.getFormPath();
                String typeName = subForm.getStandaloneClassName();

                TableColumnMeta model = (TableColumnMeta) context.getModel();

                for ( ObjectProperty property : dataObjectFinderService.getDataObjectProperties( typeName, path ) ) {
                    boolean add = true;

                    for ( int i = 0; i < subForm.getColumnMetas().size() && add == true; i++ ) {
                        TableColumnMeta meta = subForm.getColumnMetas().get( i );
                        if ( model != null && property.getName().equals( model.getProperty() ) ) {
                            break;
                        }
                        if ( meta.getProperty().equals( property.getName() ) ) {
                            add = false;
                        }
                    }

                    if ( add ) {
                        values.put( property.getName(), property.getName() );
                    }
                }}
        }
        return new SelectorData( values, null );
    }
}
