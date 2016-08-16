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

package org.kie.workbench.common.forms.processing.engine.handling.imp;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.Node;
import org.kie.workbench.common.forms.processing.engine.handling.FieldStyleHandler;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;

@ApplicationScoped
public class FieldStyleHandlerImpl implements FieldStyleHandler {

    public static final String FORM_GROUP_SUFFIX = "_form_group";
    public static final String HELP_BLOCK_SUFFIX = "_help_block";

    public static final String VALIDATION_ERROR_CLASSNAME = "has-error";

    @Override
    public void displayFieldError( FormField field, String message ) {
        assert field != null;

        Element formGroup = findFormGroup( field );

        if ( formGroup != null ) {
            formGroup.addClassName( VALIDATION_ERROR_CLASSNAME );

            Element helpBlock = findHelpBlock( field.getFieldName() + HELP_BLOCK_SUFFIX, formGroup );

            if ( helpBlock != null ) {
                helpBlock.setInnerHTML( message );
            }
        }
    }

    @Override
    public void clearFieldError( FormField field ) {
        assert field != null;

        Element formGroup = findFormGroup( field );

        if ( formGroup != null ) {
            formGroup.removeClassName( VALIDATION_ERROR_CLASSNAME );

            Element helpBlock = findHelpBlock( field.getFieldName() + HELP_BLOCK_SUFFIX, formGroup );

            if ( helpBlock != null ) {
                helpBlock.setInnerHTML( "" );
            }
        }
    }

    private Element findHelpBlock( String helpBlockId, Element parent ) {
        if ( parent == null ) return null;
        for ( int i=0; i<parent.getChildCount(); i++) {
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

    private Element findFormGroup( FormField field ) {
        String groupId = field.getFieldName() + FORM_GROUP_SUFFIX;

        Element element = field.getWidget().asWidget().getElement();

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
