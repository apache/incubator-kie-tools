/*
 * Copyright 2011 JBoss Inc
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
package org.drools.workbench.screens.guided.dtable.client.wizard.pages.cells;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.AvailableField;

/**
 * A cell to display a available Fields
 */
public class AvailableFieldCell extends AbstractCell<AvailableField> {

    interface AvailableFieldCellTemplate
            extends
            SafeHtmlTemplates {

        @Template("<div>{0}</div>")
        SafeHtml text( String message );
    }

    private static final AvailableFieldCellTemplate TEMPLATE = GWT.create( AvailableFieldCellTemplate.class );

    @Override
    public void render( final Context context,
                        final AvailableField value,
                        final SafeHtmlBuilder sb ) {
        final StringBuilder b = new StringBuilder();
        b.append( value.getName() );
        if ( value.getCalculationType() == BaseSingleFieldConstraint.TYPE_LITERAL || value.getCalculationType() == BaseSingleFieldConstraint.TYPE_RET_VALUE ) {
            appendType( b,
                        value );
        }
        sb.append( TEMPLATE.text( b.toString() ) );
    }

    private void appendType( final StringBuilder b,
                             final AvailableField af ) {
        if ( af.getType() != null && !af.getType().equals( "" ) ) {
            b.append( " : " );
            b.append( af.getDisplayType() );
        }
    }
}
