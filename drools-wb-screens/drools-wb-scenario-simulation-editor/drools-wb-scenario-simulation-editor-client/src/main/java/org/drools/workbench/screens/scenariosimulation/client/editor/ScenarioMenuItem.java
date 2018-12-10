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
package org.drools.workbench.screens.scenariosimulation.client.editor;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.MenuVisitor;

public class ScenarioMenuItem
        implements MenuCustom<Widget> {

    private Button button;

    public ScenarioMenuItem(final String title, final Command command) {
        this.button = new Button(title);
        button.setSize(ButtonSize.SMALL);
        button.addClickHandler(clickEvent -> command.execute());
    }

    public ScenarioMenuItem(final IconType icon, final Command command) {
        this.button = new Button();
        button.setIcon(icon);
        button.setSize(ButtonSize.SMALL);
        button.addClickHandler(clickEvent -> command.execute());
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
}
