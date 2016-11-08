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

package org.kie.workbench.common.forms.dynamic.client.rendering;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroupDisplayers.FormGroupDisplayer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroupDisplayers.FormGroupDisplayerFactory;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroupDisplayers.FormGroupDisplayerWidgetAware;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroupDisplayers.impl.configError.ConfigErrorFormGroupDisplayer;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.model.FieldDefinition;

public abstract class FieldRenderer<F extends FieldDefinition> {

    protected FormRenderingContext renderingContext;
    protected F field;
    protected DefaultDynamicFormField formField = null;
    protected FormGroupDisplayer group;

    public void init( FormRenderingContext renderingContext, F field ) {
        this.renderingContext = renderingContext;
        this.field = field;
    }

    public IsWidget renderWidget() {
        FieldConfigStatus configStatus = checkFieldConfig();

        if ( !configStatus.isWellConfigured() ) {
            ConfigErrorFormGroupDisplayer errorGroup = FormGroupDisplayerFactory.getErrorGroup();

            errorGroup.render( configStatus.getConfigErrors() );

            group = errorGroup;
        } else {
            IsWidget widget = null;

            if ( renderingContext.getRenderMode().equals( RenderMode.PRETTY_MODE ) ) {
                widget = getPrettyViewWidget();
            } else {
                initInputWidget();

                widget = getInputWidget();
            }

            FormGroupDisplayerWidgetAware formGroup = FormGroupDisplayerFactory.getGeneratorForRenderer(
                    renderingContext,
                    this );

            formGroup.render( widget.asWidget(), field );

            group = formGroup;

            formField = new DefaultDynamicFormField( field, widget.asWidget() ) {
                @Override
                protected void doSetReadOnly( boolean readOnly ) {
                    if ( renderingContext.getRenderMode().equals( RenderMode.PRETTY_MODE ) ) {
                        return;
                    }
                    FieldRenderer.this.setReadOnly( readOnly );
                }
            };

            formField.setReadOnly( renderingContext.getRenderMode().equals( RenderMode.READ_ONLY_MODE ) );
        }
        return group;
    }


    public DefaultDynamicFormField getFormField() {
        return formField;
    }

    public F getField() {
        return field;
    }

    protected FieldConfigStatus checkFieldConfig() {
        return new FieldConfigStatus( getConfigErrors() );
    }

    protected List<String> getConfigErrors() {
        return null;
    }

    public abstract String getName();

    public abstract void initInputWidget();

    public abstract IsWidget getInputWidget();

    public abstract IsWidget getPrettyViewWidget();

    public abstract String getSupportedCode();

    protected abstract void setReadOnly( boolean readOnly );

    protected class FieldConfigStatus {

        protected List<String> configErrors;

        public FieldConfigStatus( List<String> configErrors ) {
            this.configErrors = configErrors;
        }

        public List<String> getConfigErrors() {
            return configErrors;
        }

        public boolean isWellConfigured() {
            return configErrors == null || configErrors.isEmpty();
        }
    }
}
