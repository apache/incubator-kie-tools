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

package org.kie.workbench.common.forms.commons.layout;

import java.util.List;

import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormLayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

public abstract class AbstractFormLayoutTemplateGenerator implements FormLayoutTemplateGenerator {

    @Override
    public void generateLayoutTemplate( FormDefinition formDefinition ) {

        formDefinition.setLayoutTemplate( new LayoutTemplate() );

        addFieldsToTemplate( formDefinition.getLayoutTemplate(), formDefinition.getFields(), formDefinition.getId() );
    }

    @Override
    public void updateLayoutTemplate( FormDefinition form, List<FieldDefinition> newFields ) {

        newFields.forEach( newField -> {
            if ( form.getFieldById( newField.getId() ) == null ) {
                form.getFields().add( newField );
            }
        } );

        if ( form.getLayoutTemplate() == null || form.getLayoutTemplate().isEmpty() ) {
            generateLayoutTemplate( form );
            return;
        }

        addFieldsToTemplate( form.getLayoutTemplate(), newFields, form.getId() );
    }

    protected void addFieldsToTemplate( LayoutTemplate template, List<FieldDefinition> fields, String formId ) {
        fields.forEach( field -> {
            LayoutComponent layoutComponent = new LayoutComponent( getDraggableType() );
            layoutComponent.addProperty( FormLayoutComponent.FORM_ID, formId );
            layoutComponent.addProperty( FormLayoutComponent.FIELD_ID, field.getId() );

            LayoutColumn column = new LayoutColumn( "12" );
            column.add( layoutComponent );

            LayoutRow row = new LayoutRow();
            row.add( column );

            template.addRow( row );
        } );
    }
}
