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
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.Validator;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.RequiresValidator;
import org.kie.workbench.common.widgets.client.resources.WizardResources;

/**
 * A cell to display Conditions
 */
public class ConditionCell extends AbstractCell<ConditionCol52> implements RequiresValidator {

    private Validator validator;

    interface ConditionCellTemplate
            extends
            SafeHtmlTemplates {

        @Template("<div class=\"{0}\" >{1}</div>")
        SafeHtml text( String cssStyleName,
                       String message );
    }

    private static final ConditionCellTemplate TEMPLATE = GWT.create( ConditionCellTemplate.class );

    @Override
    public void setValidator( final Validator validator ) {
        this.validator = validator;
    }

    @Override
    public void render( final Context context,
                        final ConditionCol52 value,
                        final SafeHtmlBuilder sb ) {
        final StringBuilder b = new StringBuilder();

        switch ( value.getConstraintValueType() ) {
            case BaseSingleFieldConstraint.TYPE_LITERAL:
                makeLiteral( b,
                             value );
                break;
            case BaseSingleFieldConstraint.TYPE_RET_VALUE:
                makeFormula( b,
                             value );
                break;
            case BaseSingleFieldConstraint.TYPE_PREDICATE:
                makePredicate( b,
                               value );
        }
        sb.append( TEMPLATE.text( getCssStyleName( value ),
                                  b.toString() ) );
    }

    private void makeLiteral( final StringBuilder sb,
                              final ConditionCol52 condition ) {
        appendHeader( sb,
                      condition );
        sb.append( condition.getFactField() );
    }

    private void makeFormula( final StringBuilder sb,
                              final ConditionCol52 condition ) {
        appendHeader( sb,
                      condition );
        sb.append( condition.getFactField() );
    }

    private void makePredicate( final StringBuilder sb,
                                final ConditionCol52 condition ) {
        appendHeader( sb,
                      condition );
        sb.append( condition.getFactField() );
    }

    private void appendHeader( final StringBuilder sb,
                               final ConditionCol52 condition ) {
        if ( validator.isConditionHeaderValid( condition ) ) {
            sb.append( "[" );
            sb.append( condition.getHeader() );
            sb.append( "] " );
        }
    }

    private String getCssStyleName( final ConditionCol52 c ) {
        if ( !validator.isConditionValid( c ) ) {
            return WizardResources.INSTANCE.css().wizardDTableValidationError();
        }
        return "";
    }

}
