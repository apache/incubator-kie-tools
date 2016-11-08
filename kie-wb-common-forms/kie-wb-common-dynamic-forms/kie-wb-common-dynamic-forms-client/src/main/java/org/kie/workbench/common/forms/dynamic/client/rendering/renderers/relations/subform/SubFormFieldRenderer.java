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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.Legend;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform.widget.SubFormWidget;
import org.kie.workbench.common.forms.dynamic.client.resources.i18n.FormRenderingConstants;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.model.impl.relations.SubFormFieldDefinition;

@Dependent
public class SubFormFieldRenderer extends FieldRenderer<SubFormFieldDefinition> {

    private FieldSet container = new FieldSet();

    @Inject
    private SubFormWidget subFormWidget;

    @Override
    public void initInputWidget() {
        container.clear();
        container.add( new Legend( field.getLabel() ) );
        container.add( subFormWidget );
        if ( renderingContext != null && field.getNestedForm() != null ) {
            FormRenderingContext nestedContext = renderingContext.getCopyFor( field.getNestedForm(), null );
            subFormWidget.render( nestedContext );
        }
    }

    @Override
    protected List<String> getConfigErrors() {
        List<String> configErrors = new ArrayList<>();

        if ( field.getNestedForm() == null || field.getNestedForm().isEmpty() ) {
            configErrors.add( FormRenderingConstants.SubFormNoForm );
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
    public String getName() {
        return "SubForm";
    }

    @Override
    public String getSupportedCode() {
        return SubFormFieldDefinition.CODE;
    }

    @Override
    protected void setReadOnly( boolean readOnly ) {
        subFormWidget.setReadOnly( readOnly );
    }
}
