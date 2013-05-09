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

package org.drools.workbench.screens.guided.rule.client.editor.factPattern;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.models.commons.shared.rule.BaseSingleFieldConstraint;
import org.drools.guvnor.models.commons.shared.rule.ConnectiveConstraint;
import org.drools.guvnor.models.commons.shared.rule.FactPattern;
import org.drools.guvnor.models.commons.shared.rule.SingleFieldConstraint;
import org.drools.workbench.screens.guided.rule.client.editor.CEPOperatorsDropdown;
import org.drools.workbench.screens.guided.rule.client.editor.ConstraintValueEditor;
import org.drools.workbench.screens.guided.rule.client.editor.OperatorSelection;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.drools.workbench.screens.guided.rule.client.resources.i18n.Constants;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.kie.guvnor.commons.ui.client.resources.HumanReadable;
import org.kie.guvnor.datamodel.oracle.PackageDataModelOracle;
import org.uberfire.client.common.SmallLabel;

public class Connectives {

    private final RuleModeller modeller;
    private final EventBus eventBus;
    private final FactPattern pattern;
    private final Boolean isReadOnly;

    public Connectives( RuleModeller modeller,
                        EventBus eventBus,
                        FactPattern pattern,
                        Boolean isReadOnly ) {
        this.pattern = pattern;
        this.modeller = modeller;
        this.eventBus = eventBus;
        this.isReadOnly = isReadOnly;
    }

    /**
     * Returns the pattern.
     */
    public FactPattern getPattern() {
        return pattern;
    }

    /**
     * Returns the completions.
     */
    public PackageDataModelOracle getCompletions() {
        return this.modeller.getSuggestionCompletions();
    }

    public Widget connectives( SingleFieldConstraint c,
                               String factClass ) {
        HorizontalPanel hp = new HorizontalPanel();
        if ( c.getConnectives() != null && c.getConnectives().length > 0 ) {
            hp.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
            hp.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
            for ( int i = 0; i < c.getConnectives().length; i++ ) {

                ConnectiveConstraint con = c.getConnectives()[ i ];

                hp.add( connectiveOperatorDropDown( con ) );
                hp.add( connectiveValueEditor( con ) );

                if ( !isReadOnly ) {
                    Image clear = GuidedRuleEditorImages508.INSTANCE.DeleteItemSmall();
                    clear.setAltText( Constants.INSTANCE.RemoveThisRestriction() );
                    clear.setTitle( Constants.INSTANCE.RemoveThisRestriction() );
                    clear.addClickHandler( createClickHandlerForClearImageButton( c,
                                                                                  i ) );
                    hp.add( clear );
                }

            }
        }
        return hp;

    }

    private Widget connectiveValueEditor( final BaseSingleFieldConstraint con ) {

        return new ConstraintValueEditor( con,
                                          pattern.getConstraintList(),
                                          this.modeller,
                                          this.eventBus,
                                          isReadOnly );
    }

    private Widget connectiveOperatorDropDown( final ConnectiveConstraint cc ) {

        if ( !isReadOnly ) {

            String factType = cc.getFactType();
            String fieldName = cc.getFieldName();

            String[] operators = this.getCompletions().getConnectiveOperatorCompletions( factType,
                                                                                         fieldName );
            CEPOperatorsDropdown w = new CEPOperatorsDropdown( operators,
                                                               cc );

            w.addValueChangeHandler( new ValueChangeHandler<OperatorSelection>() {

                public void onValueChange( ValueChangeEvent<OperatorSelection> event ) {
                    OperatorSelection selection = event.getValue();
                    String selected = selection.getValue();
                    cc.setOperator( selected );
                }
            } );

            return w;

        } else {
            SmallLabel sl = new SmallLabel( "<b>" + ( cc.getOperator() == null ? Constants.INSTANCE.pleaseChoose() : HumanReadable.getOperatorDisplayName(cc.getOperator()) ) + "</b>" );
            return sl;
        }
    }

    private ClickHandler createClickHandlerForClearImageButton( final SingleFieldConstraint sfc,
                                                                final int index ) {
        return new ClickHandler() {

            public void onClick( ClickEvent event ) {
                if ( Window.confirm( Constants.INSTANCE.RemoveThisItem() ) ) {
                    sfc.removeConnective( index );
                    modeller.makeDirty();
                    modeller.refreshWidget();
                }
            }
        };
    }
}
