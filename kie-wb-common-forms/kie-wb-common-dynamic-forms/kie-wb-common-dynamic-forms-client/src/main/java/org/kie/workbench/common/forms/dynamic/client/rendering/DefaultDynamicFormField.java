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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.common.client.api.Assert;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.uberfire.ext.widgets.common.client.common.StyleHelper;

public abstract class DefaultDynamicFormField<F extends FieldDefinition, W extends Widget> implements FormField {

    protected F field;

    protected W widget;

    protected Element formGroup;

    protected Element helpBlock;

    public DefaultDynamicFormField( F field,
                                    W widget ) {
        Assert.notNull( "Field cannot be null", field );
        Assert.notNull( "Widget cannot be null", widget );
        this.field = field;
        this.widget = widget;
        this.formGroup = findFormGroup();
        this.helpBlock = findHelpBlock();
    }

    @Override
    public String getFieldName() {
        return field.getName();
    }

    @Override
    public String getFieldBinding() {
        return field.getBinding();
    }

    @Override
    public boolean isValidateOnChange() {
        return field.getValidateOnChange();
    }


    @Override
    public boolean isBindable() {
        return field.getBinding() != null && !field.getBinding().isEmpty();
    }

    @Override
    public void setVisible( boolean visible ) {
        formGroup.getStyle().setVisibility( visible ? Style.Visibility.VISIBLE : Style.Visibility.HIDDEN );
    }

    @Override
    public void setReadOnly( boolean readOnly ) {
        if ( !field.getReadonly() ) {
            doSetReadOnly( readOnly );
        }
    }

    protected abstract void doSetReadOnly( boolean readOnly );

    @Override
    public void clearError() {
        if ( formGroup != null ) {
            StyleHelper.addEnumStyleName( formGroup, ValidationState.NONE );
            StyleHelper.removeEnumStyleName( formGroup, ValidationState.ERROR );
        }
        if ( helpBlock != null ) {
            helpBlock.setInnerHTML( "" );
        }
    }

    @Override
    public void setError( String error ) {
        if ( error == null || error.isEmpty() ) {
            clearError();
        } else {
            if ( formGroup != null ) {
                StyleHelper.addEnumStyleName( formGroup, ValidationState.ERROR );
                StyleHelper.removeEnumStyleName( formGroup, ValidationState.NONE );
            }
            if ( helpBlock != null ) {
                helpBlock.setInnerHTML( error );
            }
        }
    }

    @Override
    public IsWidget getWidget() {
        return widget;
    }

    private Element findHelpBlock() {
        if ( formGroup != null ) {
            String helpBlockId = field.getName() + HELP_BLOCK_SUFFIX;
            return findHelpBlock( helpBlockId, formGroup );
        }
        return null;
    }

    private Element findHelpBlock( String helpBlockId, Element parent ) {
        if ( parent == null ) return null;
        for ( int i = 0; i < parent.getChildCount(); i++ ) {
            Node child = parent.getChild( i );
            if ( child.getNodeType() == Node.ELEMENT_NODE ) {
                Element childE = (Element) child;
                if ( childE.getId().equals( helpBlockId ) ) {
                    return childE;
                }
                childE = findHelpBlock( helpBlockId, childE );
                if ( childE != null ) return childE;
            }
        }
        return null;
    }

    private Element findFormGroup() {
        String groupId = field.getName() + FORM_GROUP_SUFFIX;

        Element element = widget.asWidget().getElement();

        return findFormGroup( groupId, element );
    }

    private Element findFormGroup( String groupId, Element element ) {
        if ( element.getTagName().equals( FormElement.TAG ) ) {
            return null;
        }
        if ( element.getId().equals( groupId ) ) {
            return element;
        }
        return findFormGroup( groupId, element.getParentElement() );
    }
}
