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
package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.Legend;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.resources.i18n.FormRenderingConstants;
import org.kie.workbench.common.forms.model.impl.relations.MultipleSubFormFieldDefinition;

@Dependent
public class MultipleSubFormFieldRenderer extends FieldRenderer<MultipleSubFormFieldDefinition> {

    private FieldSet container = new FieldSet();

    @Inject
    private MultipleSubFormWidget subFormWidget;

    @Override
    public String getName() {
        return "Multiple SubForm";
    }

    @Override
    public void initInputWidget() {
        container.clear();
        container.add( new Legend( field.getLabel() ) );
        subFormWidget.config( field, renderingContext );
        container.add( subFormWidget );
    }

    @Override
    protected List<String> getConfigErrors() {
        List<String> configErrors = new ArrayList<>();

        if ( field.getColumnMetas() == null || field.getColumnMetas().size() == 0 ) {
            configErrors.add( FormRenderingConstants.MultipleSubformNoColumns );
        }
        if ( field.getCreationForm() == null || field.getCreationForm().isEmpty() ) {
            configErrors.add( FormRenderingConstants.MultipleSubformNoCreationForm );
        }
        if ( field.getEditionForm() == null || field.getEditionForm().isEmpty() ) {
            configErrors.add( FormRenderingConstants.MultipleSubformNoEditionForm );
        }
        return configErrors;
    }

    @Override
    public IsWidget getInputWidget() {
        return subFormWidget;
    }

    @Override
    public IsWidget getPrettyViewWidget() {
        initInputWidget();
        return getInputWidget();
    }

    @Override
    public String getSupportedCode() {
        return MultipleSubFormFieldDefinition.CODE;
    }

    @Override
    protected void setReadOnly( boolean readOnly ) {
        subFormWidget.setReadOnly( readOnly );
    }
}
