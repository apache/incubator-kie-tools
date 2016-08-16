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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.kie.workbench.common.forms.dynamic.client.resources.i18n.FormRenderingConstants;
import org.kie.workbench.common.forms.dynamic.service.FormRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.processing.engine.handling.imp.FieldStyleHandlerImpl;

public abstract class FieldRenderer<F extends FieldDefinition> {

    protected FormRenderingContext renderingContext;
    protected F field;

    public void init( FormRenderingContext renderingContext, F field ) {
        this.renderingContext = renderingContext;
        this.field = field;
    }

    public IsWidget renderWidget() {
        FormGroup group = new FormGroup();
        group.getElement().setId( getFormGroupId( field ) );

        if ( isFieldWellConfigured() ) {
            initInputWidget();
            addFormGroupContents( group );
        } else {
            group.setValidationState( ValidationState.ERROR );
            HelpBlock helpBlock = new HelpBlock();
            helpBlock.setIconType( IconType.WARNING );
            helpBlock.setHTML( FormRenderingConstants.INSTANCE.unableToDisplayField() );
            group.add( helpBlock );
        }
        return group;
    }

    protected void addFormGroupContents( FormGroup group ) {
        FormLabel label = new FormLabel();
        label.setText( field.getLabel() );

        Widget input = getInputWidget().asWidget();

        label.setFor( input.getElement().getId() );
        group.add( label );
        group.add( input );

        HelpBlock helpBlock = new HelpBlock();
        helpBlock.setId( getHelpBlokId( field ) );

        group.add( helpBlock );
    }

    public F getField() {
        return field;
    }

    public boolean isFieldWellConfigured() {
        return true;
    }

    public abstract String getName();

    public abstract void initInputWidget();

    public abstract IsWidget getInputWidget();

    public abstract String getSupportedCode();

    protected String getFormGroupId( F field ) {
        return generateRelatedId( field, FieldStyleHandlerImpl.FORM_GROUP_SUFFIX );
    }

    protected String getHelpBlokId( F field ) {
        return generateRelatedId( field, FieldStyleHandlerImpl.HELP_BLOCK_SUFFIX );
    }

    private String generateRelatedId( F field, String suffix ) {
        if ( field == null ) {
            return "";
        }
        return field.getName() + suffix;
    }
}
