/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLUListElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder.FA_ANGLE_DOWN;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder.FA_ANGLE_RIGHT;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder.HIDDEN;

/**
 * This class is used to represent a single scenario with all its own decisions/rules
 */
@Templated
public class CoverageScenarioListViewImpl implements CoverageScenarioListView {

    @DataField
    protected HTMLLIElement scenarioElement = (HTMLLIElement) DomGlobal.document.createElement("li");

    @DataField
    protected HTMLUListElement scenarioContentList = (HTMLUListElement) DomGlobal.document.createElement("ul");

    @DataField
    protected HTMLElement faAngleRight = (HTMLElement) DomGlobal.document.createElement("span");

    @DataField
    protected HTMLElement itemLabelElement = (HTMLElement) DomGlobal.document.createElement("span");

    private Presenter presenter;

    @EventHandler("scenarioElement")
    public void onElementClick(ClickEvent clickEvent) {
        presenter.onElementClick(this);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public HTMLLIElement getScenarioElement() {
        return scenarioElement;
    }

    @Override
    public HTMLUListElement getScenarioContentList() {
        return scenarioContentList;
    }

    @Override
    public void setItemLabel(String itemLabel) {
        itemLabelElement.textContent = itemLabel;
    }

    @Override
    public boolean isVisible() {
        return !scenarioContentList.classList.contains(HIDDEN);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            faAngleRight.classList.remove(FA_ANGLE_RIGHT);
            faAngleRight.classList.add(FA_ANGLE_DOWN);
            scenarioContentList.classList.remove(HIDDEN);
        } else {
            faAngleRight.classList.remove(FA_ANGLE_DOWN);
            faAngleRight.classList.add(FA_ANGLE_RIGHT);
            scenarioContentList.classList.add(HIDDEN);
        }
    }
}
