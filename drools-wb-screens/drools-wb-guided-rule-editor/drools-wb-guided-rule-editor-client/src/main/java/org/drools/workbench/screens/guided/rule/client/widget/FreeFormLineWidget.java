/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.screens.guided.rule.client.widget;

import com.github.gwtbootstrap.client.ui.TextArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.guided.rule.client.resources.i18n.Constants;

/**
 * Free form DRL line widget
 */
public class FreeFormLineWidget extends RuleModellerWidget {

    private FreeFormLine action;
    private FlexTable layout = new FlexTable();
    private TextArea textArea = new TextArea();
    private boolean readOnly;

    private Constants constants = GWT.create( Constants.class );

    public FreeFormLineWidget( RuleModeller mod,
                               EventBus eventBus,
                               FreeFormLine p ) {
        this(mod,
                eventBus,
                p,
                null);
    }

    /**
     * Creates a new FactPatternWidget
     * @param mod
     * @param p
     * @param readOnly if the widget should be in RO mode. If this parameter is null,
     * the readOnly attribute is calculated.
     */
    public FreeFormLineWidget( RuleModeller mod,
                               EventBus eventBus,
                               FreeFormLine p,
                               Boolean readOnly ) {
        super(mod,
                eventBus);
        this.action = p;

        if ( readOnly == null ) {
            this.readOnly = false;
        } else {
            this.readOnly = readOnly;
        }

        layout.setWidth("100%");
        textArea.setWidth( "100%" );

        layout.setWidget( 0,
                          0,
                          createTextBox() );
        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        formatter.setAlignment( 0,
                                0,
                                HasHorizontalAlignment.ALIGN_LEFT,
                                HasVerticalAlignment.ALIGN_BOTTOM );

        formatter.setAlignment( 0,
                                1,
                                HasHorizontalAlignment.ALIGN_LEFT,
                                HasVerticalAlignment.ALIGN_TOP );

        if ( this.readOnly ) {
            this.layout.addStyleName( "editor-disabled-widget" );
        }

        initWidget( layout );
    }

    private Widget createTextBox() {
        textArea.setTitle( GuidedRuleEditorResources.CONSTANTS.ThisIsADrlExpressionFreeForm() );
        textArea.setText( this.action.getText() );
        textArea.setEnabled( !this.readOnly );
        textArea.setPlaceholder( constants.AddFreeFormDrlDotDotDot() );
        if ( !this.readOnly ) {
            textArea.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange( ValueChangeEvent<String> event ) {
                    action.setText( textArea.getText() );
                    setModified( true );
                }

            } );
        }
        return textArea;
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public boolean isFactTypeKnown() {
        return true;
    }

}
