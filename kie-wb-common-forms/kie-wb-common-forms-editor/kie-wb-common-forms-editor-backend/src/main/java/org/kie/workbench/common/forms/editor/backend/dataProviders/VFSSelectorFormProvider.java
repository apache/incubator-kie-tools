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

package org.kie.workbench.common.forms.editor.backend.dataProviders;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.inject.Inject;

import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SystemSelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.editor.service.FormEditorRenderingContext;
import org.kie.workbench.common.forms.editor.service.VFSFormFinderService;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.impl.relations.EmbeddedFormField;

public class VFSSelectorFormProvider implements SystemSelectorDataProvider {

    @Inject
    private VFSFormFinderService vfsFormFinderService;

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    public SelectorData getSelectorData( FormRenderingContext context ) {
        Map<String, String> values = new TreeMap<>();

        if ( context.getModel() instanceof EmbeddedFormField ) {
            FormEditorRenderingContext editorContext = (FormEditorRenderingContext) context;

            FieldDefinition field = (FieldDefinition) context.getModel();

            List<FormDefinition> forms;
            if ( field != null ) {
                forms = vfsFormFinderService.findFormsForType( field.getStandaloneClassName(), editorContext.getFormPath());
            } else {
                forms = vfsFormFinderService.findAllForms( editorContext.getFormPath() );
            }

            for ( FormDefinition form : forms ) {
                values.put( form.getId(), form.getName() );
            }
        }
        return  new SelectorData( values, null );
    }
}
