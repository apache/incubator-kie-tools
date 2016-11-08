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

package org.kie.workbench.common.forms.editor.backend.service.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContextGeneratorService;
import org.kie.workbench.common.forms.editor.service.FieldPropertiesService;
import org.kie.workbench.common.forms.editor.service.FormEditorRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class FieldPropertiesServiceImpl implements FieldPropertiesService {

    protected FormRenderingContextGeneratorService formRenderingContextGeneratorService;

    @Inject
    public FieldPropertiesServiceImpl( FormRenderingContextGeneratorService formRenderingContextGeneratorService ) {
        this.formRenderingContextGeneratorService = formRenderingContextGeneratorService;
    }

    @Override
    public FormEditorRenderingContext getFieldPropertiesRenderingContext( FieldDefinition fieldDefinition,
                                                                          Path formPath ) {
        FormRenderingContext context = formRenderingContextGeneratorService.createContext( fieldDefinition );

        FormEditorRenderingContext editorContext = new FormEditorRenderingContext( formPath );
        editorContext.setRootForm( context.getRootForm() );
        editorContext.setModel( fieldDefinition );

        editorContext.getAvailableForms().putAll( context.getAvailableForms() );

        return editorContext;
    }
}
