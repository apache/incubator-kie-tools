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

package org.drools.workbench.screens.testscenario.client;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.kie.workbench.common.widgets.client.widget.KSessionSelector;
import org.uberfire.backend.vfs.Path;

public class ScenarioKSessionSelector
        implements IsWidget {

    private Scenario         scenario;
    private KSessionSelector selector;

    public ScenarioKSessionSelector() {
    }

    @Inject
    public ScenarioKSessionSelector( final KSessionSelector selector ) {
        this.selector = selector;
        selector.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange( final SelectionChangeEvent selectionChangeEvent ) {
                scenario.getKSessions().clear();
                scenario.getKSessions().add( selector.getSelectedKSessionName() );
            }
        } );
    }

    public void init( Path path,
                      Scenario scenario ) {
        this.scenario = scenario;
        selector.init( path,
                       getKSessionName() );
    }

    private String getKSessionName() {
        if ( scenario.getKSessions().isEmpty() ) {
            return null;
        } else {
            return scenario.getKSessions().get( 0 );
        }
    }

    @Override
    public Widget asWidget() {
        return selector.asWidget();
    }
}
