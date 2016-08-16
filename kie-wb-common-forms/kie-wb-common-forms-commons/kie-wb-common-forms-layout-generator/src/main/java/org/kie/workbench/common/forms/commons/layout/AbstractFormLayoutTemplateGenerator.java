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

import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormLayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

public abstract class AbstractFormLayoutTemplateGenerator implements FormLayoutTemplateGenerator {

    @Override
    public LayoutTemplate generateLayoutTemplate( FormDefinition formDefinition ) {
        LayoutTemplate template = new LayoutTemplate();

        for ( FieldDefinition field : formDefinition.getFields() ) {
            LayoutComponent layoutComponent = new LayoutComponent( getDraggableType() );
            layoutComponent.addProperty( FormLayoutComponent.FORM_ID, formDefinition.getId() );
            layoutComponent.addProperty( FormLayoutComponent.FIELD_ID, field.getId() );

            LayoutColumn column = new LayoutColumn("12");
            column.add( layoutComponent );

            LayoutRow row = new LayoutRow();
            row.add( column );

            template.addRow( row );
        }

        return template;
    }
}
