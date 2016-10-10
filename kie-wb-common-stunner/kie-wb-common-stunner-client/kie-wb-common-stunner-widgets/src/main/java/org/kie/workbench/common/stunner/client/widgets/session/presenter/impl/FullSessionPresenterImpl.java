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

import org.kie.workbench.common.stunner.client.widgets.session.toolbar.command.*;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.impl.AbstractToolbar;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.command.factory.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramServices;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryServices;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultCanvasFullSession;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultCanvasSessionManager;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class FullSessionPresenterImpl extends AbstractFullSessionPresenter<DefaultCanvasFullSession> implements DefaultFullSessionPresenter {

    @Inject
    public FullSessionPresenterImpl( final DefaultCanvasSessionManager canvasSessionManager,
                                     final ClientDefinitionManager clientDefinitionManager,
                                     final ClientFactoryServices clientFactoryServices,
                                     final AbstractToolbar<DefaultCanvasFullSession> toolbar,
                                     final ClearSelectionCommand clearSelectionCommand,
                                     final ClearCommand clearCommand,
                                     final DeleteSelectionCommand deleteSelectionCommand,
                                     final SaveCommand saveCommand,
                                     final UndoCommand undoCommand,
                                     final ValidateCommand validateCommand,
                                     final VisitGraphCommand visitGraphCommand,
                                     final SwitchGridCommand switchGridCommand,
                                     final CanvasCommandFactory commandFactory,
                                     final ClientDiagramServices clientDiagramServices,
                                     final ErrorPopupPresenter errorPopupPresenter,
                                     final View view ) {
        super( canvasSessionManager, clientDefinitionManager, clientFactoryServices,
                commandFactory, clientDiagramServices, toolbar, clearSelectionCommand, clearCommand,
                deleteSelectionCommand, saveCommand, undoCommand, validateCommand, visitGraphCommand,
                switchGridCommand, errorPopupPresenter, view );
    }

}
