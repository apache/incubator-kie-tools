/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.scenariosimulation.client.commands;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelPresenter;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.mvp.Command;

@Dependent
public class RightPanelMenuCommand implements Command {

    private PlaceManager placeManager;

    public RightPanelMenuCommand() {
    }

    @Inject
    public RightPanelMenuCommand(PlaceManager placeManager) {
        this.placeManager = placeManager;
    }

    @Override
    public void execute() {
        if (PlaceStatus.OPEN.equals(placeManager.getStatus(RightPanelPresenter.IDENTIFIER))) {
            placeManager.closePlace(RightPanelPresenter.IDENTIFIER);
        } else {
            placeManager.goTo(RightPanelPresenter.IDENTIFIER);
        }
    }

}
