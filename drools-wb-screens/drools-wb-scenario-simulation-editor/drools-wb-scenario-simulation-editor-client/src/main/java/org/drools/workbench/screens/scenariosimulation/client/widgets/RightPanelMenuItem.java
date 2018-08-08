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
package org.drools.workbench.screens.scenariosimulation.client.widgets;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.scenariosimulation.client.commands.RightPanelMenuCommand;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelPresenter;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.MenuVisitor;

@Dependent
public class RightPanelMenuItem implements MenuCustom<Widget> {

    private Button button = GWT.create(Button.class);

    private PlaceManager placeManager;

    private RightPanelMenuCommand rightPanelMenuCommand;

    public RightPanelMenuItem() {

        // For CDI
    }

    @Inject
    public RightPanelMenuItem(final PlaceManager placeManager, final RightPanelMenuCommand rightPanelMenuCommand) {
        this.placeManager = placeManager;
        this.rightPanelMenuCommand = rightPanelMenuCommand;
        button.addClickHandler(event -> {
            if (rightPanelMenuCommand != null) {
                rightPanelMenuCommand.execute();
            }
        });
        button.setSize(ButtonSize.SMALL);
    }

    @PostConstruct
    public void init() {
        final boolean isRightPanelShown = PlaceStatus.OPEN.equals(placeManager.getStatus(RightPanelPresenter.IDENTIFIER));
        setButtonText(isRightPanelShown);
        placeManager.registerOnOpenCallback(new DefaultPlaceRequest(RightPanelPresenter.IDENTIFIER),
                                            () -> setButtonText(true));
        placeManager.registerOnCloseCallback(new DefaultPlaceRequest(RightPanelPresenter.IDENTIFIER),
                                             () -> setButtonText(false));
    }

    @Override
    public Widget build() {
        return button;
    }

    @Override
    public boolean isEnabled() {
        return button.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        button.setEnabled(enabled);
    }

    @Override
    public String getContributionPoint() {
        return null;
    }

    @Override
    public String getCaption() {
        return null;
    }

    @Override
    public MenuPosition getPosition() {
        return null;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void accept(final MenuVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void addEnabledStateChangeListener(EnabledStateChangeListener listener) {

    }

    @Override
    public String getIdentifier() {
        return null;
    }

    private void setButtonText(boolean isRightPanelShown) {
        button.setText(isRightPanelShown ? ScenarioSimulationEditorConstants.INSTANCE.hideRightPanel() : ScenarioSimulationEditorConstants.INSTANCE.showRightPanel());
    }
}
