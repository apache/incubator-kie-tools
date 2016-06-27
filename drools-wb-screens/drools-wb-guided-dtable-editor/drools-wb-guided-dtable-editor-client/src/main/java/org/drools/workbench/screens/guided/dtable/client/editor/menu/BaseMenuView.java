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

package org.drools.workbench.screens.guided.dtable.client.editor.menu;

import com.google.gwt.dom.client.Element;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectionsChangedEvent;
import org.uberfire.client.mvp.UberView;

public interface BaseMenuView<M extends BaseMenu> extends UberView<M> {

    interface BaseMenuPresenter {

        void initialise();

        void onDecisionTableSelectedEvent( final DecisionTableSelectedEvent event );

        void onDecisionTableSelectionsChangedEvent( final DecisionTableSelectionsChangedEvent event );

    }

    void enableElement( final Element element,
                        final boolean enabled );

    boolean isDisabled( final Element element );

}
