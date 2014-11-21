/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.table;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.SetGuidedDecisionTableModelEvent;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;

/**
 * A Vertical Decision Table composed of a VerticalDecoratedGridWidget
 */
public class VerticalDecisionTableWidget extends AbstractDecisionTableWidget {

    private DecisionTableControlsWidget ctrls;

    public VerticalDecisionTableWidget( GuidedDecisionTable52 model,
                                        AsyncPackageDataModelOracle oracle,
                                        User identity,
                                        boolean isReadOnly,
                                        EventBus eventBus ) {
        super( model,
               oracle,
               identity,
               isReadOnly,
               eventBus );

        VerticalPanel vp = new VerticalPanel();

        ctrls = new DecisionTableControlsWidget( this,
                                                 model,
                                                 identity,
                                                 isReadOnly );
        vp.add( ctrls );

        // Construct the widget from which we're composed
        widget = new VerticalDecoratedDecisionTableGridWidget( resources,
                                                               cellFactory,
                                                               cellValueFactory,
                                                               dropDownManager,
                                                               isReadOnly,
                                                               eventBus );
        vp.add( widget );

        initWidget( vp );

        //Fire event for UI components to set themselves up
        SetGuidedDecisionTableModelEvent sme = new SetGuidedDecisionTableModelEvent( model );
        eventBus.fireEvent( sme );
    }

    @Override
    protected void setEnableOtherwiseButton( boolean isEnabled ) {
        ctrls.setEnableOtherwiseButton( isEnabled );
    }

    public void onFocus() {
        ( (VerticalDecoratedDecisionTableGridWidget) widget ).onFocus();
    }

}
