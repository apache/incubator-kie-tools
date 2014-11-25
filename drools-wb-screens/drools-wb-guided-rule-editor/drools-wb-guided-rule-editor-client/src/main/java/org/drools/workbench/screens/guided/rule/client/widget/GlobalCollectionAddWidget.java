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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.SimplePanel;
import org.drools.workbench.models.datamodel.rule.ActionGlobalCollectionAdd;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import com.google.gwt.user.client.ui.FlexTable;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;

/**
 * Add Variable to global collection Widget
 */
public class GlobalCollectionAddWidget extends RuleModellerWidget {

    private FlexTable layout = new FlexTable();
    private boolean readOnly;

    private boolean isFactTypeKnown;

    public GlobalCollectionAddWidget( RuleModeller mod,
                                      EventBus eventBus,
                                      ActionGlobalCollectionAdd action ) {
        this( mod,
              eventBus,
              action,
              null );
    }

    /**
     * Creates a new FactPatternWidget
     * @param modeller
     * @param eventBus
     * @param action
     * @param readOnly if the widget should be in RO mode. If this parameter is null,
     * the readOnly attribute is calculated.
     */
    public GlobalCollectionAddWidget( RuleModeller modeller,
                                      EventBus eventBus,
                                      ActionGlobalCollectionAdd action,
                                      Boolean readOnly ) {

        super( modeller,
               eventBus );

        this.isFactTypeKnown = modeller.getDataModelOracle().isFactTypeRecognized( modeller.getModel().getLHSBindingType( action.getFactName() ) );
        if ( readOnly == null ) {
            this.readOnly = !this.isFactTypeKnown;
        } else {
            this.readOnly = readOnly;
        }

        ActionGlobalCollectionAdd gca = (ActionGlobalCollectionAdd) action;
        SimplePanel sp = new SimplePanel();
        sp.setStyleName( "model-builderInner-Background" ); //NON-NLS
        sp.add( new SmallLabel( "&nbsp;" + GuidedRuleEditorResources.CONSTANTS.AddXToListY( gca.getFactName(),
                                                                           gca.getGlobalName() ) ) );

        if ( this.readOnly ) {
            this.layout.addStyleName( "editor-disabled-widget" );
            sp.addStyleName( "editor-disabled-widget" );
        }

        layout.setWidget( 0,
                          0,
                          sp );
        initWidget( layout );

        //This widget couldn't be modified
        this.setModified( false );
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public boolean isFactTypeKnown() {
        return this.isFactTypeKnown;
    }

}
