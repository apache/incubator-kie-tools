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
package org.kie.workbench.common.forms.editor.client.type;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.editor.client.resources.FormModelerEditorResources;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.kie.workbench.common.forms.editor.type.FormResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class FormDefinitionResourceType extends FormResourceTypeDefinition implements ClientResourceType {

    private TranslationService translationService;

    @Inject
    public FormDefinitionResourceType( TranslationService translationService ) {
        this.translationService = translationService;
    }

    @Override
    public IsWidget getIcon() {
        return new Image( FormModelerEditorResources.INSTANCE.images().typeForm() );
    }

    @Override
    public String getShortName() {
        String desc = translationService.getTranslation( FormEditorConstants.FormDefinitionResourceTypeFormTypeShortName );
        if ( desc == null || desc.isEmpty() ) return super.getShortName();
        return desc;
    }

    @Override
    public String getDescription() {
        String desc = translationService.getTranslation( FormEditorConstants.FormDefinitionResourceTypeFormTypeDescription );
        if ( desc == null || desc.isEmpty() ) return super.getDescription();
        return desc;
    }
}
