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
import org.kie.workbench.common.widgets.client.resources.WizardResources;
import org.drools.workbench.screens.guided.dtable.client.widget.Validator;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.RequiresValidator;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;

/**
 * A cell to display Actions
 */
public class ActionInsertFactFieldCell extends AbstractCell<ActionInsertFactCol52> implements RequiresValidator {

    private Validator validator;

    interface ActionSetFieldCellTemplate
            extends
            SafeHtmlTemplates {

        @Template("<div class=\"{0}\" >{1}</div>")
        SafeHtml text( String cssStyleName,
                       String message );
    }

    private static final ActionSetFieldCellTemplate TEMPLATE = GWT.create( ActionSetFieldCellTemplate.class );

    @Override
    public void setValidator( final Validator validator ) {
        this.validator = validator;
    }

    @Override
    public void render( final Context context,
                        final ActionInsertFactCol52 value,
                        final SafeHtmlBuilder sb ) {
        final StringBuilder b = new StringBuilder();
        appendHeader( b,
                      value );
        b.append( value.getFactField() );
        sb.append( TEMPLATE.text( getCssStyleName( value ),
                                  b.toString() ) );
    }

    private void appendHeader( final StringBuilder sb,
                               final ActionInsertFactCol52 a ) {
        if ( validator.isActionHeaderValid( a ) ) {
            sb.append( "[" );
            sb.append( a.getHeader() );
            sb.append( "] " );
        }
    }

    private String getCssStyleName( final ActionInsertFactCol52 a ) {
        if ( !validator.isActionValid( a ) ) {
            return WizardResources.INSTANCE.css().wizardDTableValidationError();
        }
        return "";
    }

}
