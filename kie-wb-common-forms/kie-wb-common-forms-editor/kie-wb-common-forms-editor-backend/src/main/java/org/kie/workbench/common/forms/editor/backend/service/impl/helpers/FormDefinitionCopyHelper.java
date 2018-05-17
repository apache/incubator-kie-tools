/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.backend.service.impl.helpers;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormLayoutComponent;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.services.backend.util.UIDGenerator;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.helper.CopyHelper;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.io.IOService;

@ApplicationScoped
public class FormDefinitionCopyHelper extends AbstractFormDefinitionHelper implements CopyHelper {

    @Inject
    public FormDefinitionCopyHelper(@Named("ioStrategy") IOService ioService, FormDefinitionSerializer serializer, CommentedOptionFactory commentedOptionFactory) {
        super(serializer, ioService, commentedOptionFactory);
    }

    @Override
    protected void processFormDefinition(FormDefinition formDefinition, Path formPath) {
        super.processFormDefinition(formDefinition, formPath);

        String newFormId = UIDGenerator.generateUID();
        formDefinition.setId(newFormId);

        LayoutTemplate layout = formDefinition.getLayoutTemplate();

        if (layout != null) {
            layout.getRows().stream().forEach(row -> process(row, newFormId));
        }
    }

    private void process(LayoutRow row, String formId) {
        row.getLayoutColumns().stream().forEach(column -> process(column, formId));
    }

    private void process(LayoutColumn column, String formId) {
        column.getLayoutComponents().stream().forEach(component -> process(component, formId));
    }

    private void process(LayoutComponent component, String formId) {
        Map<String, String> properties = component.getProperties();
        if (properties.containsKey(FormLayoutComponent.FIELD_ID) && properties.containsKey(FormLayoutComponent.FORM_ID)) {
            properties.put(FormLayoutComponent.FORM_ID, formId);
        }
    }
}
