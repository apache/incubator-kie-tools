/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.session.presenter.impl;

import org.kie.workbench.common.stunner.client.widgets.session.toolbar.command.ClearSelectionCommand;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.command.SwitchGridCommand;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.command.VisitGraphCommand;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.impl.AbstractToolbar;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramServices;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultCanvasReadOnlySession;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultCanvasSessionManager;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;

// TODO: @Dependent - As there is no bean injection for AbstractToolbar<DefaultCanvasReadOnlySession> yet, this class
// is abstract and not eligible by the bean manager for now.
public abstract class ReadOnlySessionPresenterImpl extends AbstractReadOnlySessionPresenter<DefaultCanvasReadOnlySession>
        implements DefaultReadOnlySessionPresenter {

    public ReadOnlySessionPresenterImpl( final DefaultCanvasSessionManager canvasSessionManager,
                                         final ClientDiagramServices clientDiagramServices,
                                         final AbstractToolbar<DefaultCanvasReadOnlySession> toolbar,
                                         final ClearSelectionCommand clearSelectionCommand,
                                         final VisitGraphCommand visitGraphCommand,
                                         final SwitchGridCommand switchGridCommand,
                                         final ErrorPopupPresenter errorPopupPresenter,
                                         final View view ) {
        super( canvasSessionManager, clientDiagramServices, toolbar, clearSelectionCommand, visitGraphCommand,
                switchGridCommand, errorPopupPresenter, view );
    }

}
