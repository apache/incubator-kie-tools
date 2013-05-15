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
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactFieldsPattern;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.Validator;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.RequiresValidator;
import org.kie.workbench.common.widgets.client.resources.WizardResources;

/**
 * A cell to display a Fact Pattern on the Action Insert Fact Field page.
 * Additional validation is performed on the Pattern's fields to determine
 * whether the cell should be rendered as valid or invalid
 */
public class ActionInsertFactFieldPatternCell extends AbstractCell<ActionInsertFactFieldsPattern> implements RequiresValidator {

    protected Validator validator;

    interface ActionInsertFactFieldPatternCellTemplate
            extends
            SafeHtmlTemplates {

        @Template("<div class=\"{0}\" >{1}</div>")
        SafeHtml text( String cssStyleName,
                       String message );
    }

    private static final ActionInsertFactFieldPatternCellTemplate TEMPLATE = GWT.create( ActionInsertFactFieldPatternCellTemplate.class );

    @Override
    public void setValidator( final Validator validator ) {
        this.validator = validator;
    }

    @Override
    public void render( final Context context,
                        final ActionInsertFactFieldsPattern value,
                        final SafeHtmlBuilder sb ) {
        final String binding = value.getBoundName();
        final StringBuilder b = new StringBuilder();
        if ( binding == null || "".equals( binding ) ) {
            b.append( value.getFactType() );
        } else {
            b.append( value.getBoundName() );
            b.append( " : " );
            b.append( value.getFactType() );
        }
        sb.append( TEMPLATE.text( getCssStyleName( value ),
                                  b.toString() ) );
    }

    protected String getCssStyleName( final Pattern52 p ) {
        if ( !( validator.isPatternBindingUnique( p ) ) ) {
            return WizardResources.INSTANCE.css().wizardDTableValidationError();
        }
        if ( !validator.arePatternActionInsertFactFieldsValid( p ) ) {
            return WizardResources.INSTANCE.css().wizardDTableValidationError();
        }
        return "";
    }

}
