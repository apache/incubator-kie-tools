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

package org.kie.guvnor.guided.rule.client.widget;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.models.commons.shared.rule.FromEntryPointFactPattern;
import org.kie.guvnor.guided.rule.client.editor.RuleModeller;
import org.kie.guvnor.commons.ui.client.resources.HumanReadable;
import org.kie.guvnor.guided.rule.client.resources.i18n.Constants;
import org.uberfire.client.common.ClickableLabel;
import org.uberfire.client.common.DirtyableFlexTable;

public class FromEntryPointFactPatternWidget extends FromCompositeFactPatternWidget {

    private TextBox txtEntryPoint;

    public FromEntryPointFactPatternWidget( RuleModeller modeller,
                                            EventBus eventBus,
                                            FromEntryPointFactPattern pattern ) {
        super( modeller,
               eventBus,
               pattern );
    }

    public FromEntryPointFactPatternWidget( RuleModeller modeller,
                                            EventBus eventBus,
                                            FromEntryPointFactPattern pattern,
                                            Boolean readOnly ) {
        super( modeller,
               eventBus,
               pattern,
               readOnly );
    }

    @Override
    protected Widget getCompositeLabel() {

        ClickHandler click = new ClickHandler() {

            public void onClick( ClickEvent event ) {
                Widget w = (Widget) event.getSource();
                showFactTypeSelector( w );

            }
        };
        String lbl = "<div class='form-field'>" + HumanReadable.getCEDisplayName( "from entry-point" ) + "</div>";

        DirtyableFlexTable panel = new DirtyableFlexTable();

        int r = 0;

        if ( pattern.getFactPattern() == null ) {
            panel.setWidget( r,
                             0,
                             new ClickableLabel( "<br> <font color='red'>" + Constants.INSTANCE.clickToAddPatterns() + "</font>",
                                                 click,
                                                 !this.readOnly ) );
            r++;
        }

        panel.setWidget( r,
                         0,
                         new HTML( lbl ) );

        this.txtEntryPoint = new TextBox();
        this.txtEntryPoint.setText( getFromEntryPointPattern().getEntryPointName() );
        this.txtEntryPoint.addChangeHandler( new ChangeHandler() {

            public void onChange( ChangeEvent event ) {
                getFromEntryPointPattern().setEntryPointName( txtEntryPoint.getText() );
                setModified( true );
            }

        } );
        panel.setWidget( r,
                         1,
                         this.txtEntryPoint );

        return panel;
    }

    private FromEntryPointFactPattern getFromEntryPointPattern() {
        return (FromEntryPointFactPattern) this.pattern;
    }

}
