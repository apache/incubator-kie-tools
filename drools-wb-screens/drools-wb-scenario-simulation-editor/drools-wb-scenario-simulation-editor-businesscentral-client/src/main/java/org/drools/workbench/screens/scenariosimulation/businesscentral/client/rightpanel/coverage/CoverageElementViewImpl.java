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

package org.drools.workbench.screens.scenariosimulation.businesscentral.client.rightpanel.coverage;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

/**
 * This class is used to represent a single row in the coverage decision/rule section
 */
@Templated
public class CoverageElementViewImpl implements CoverageElementView {

    @DataField
    protected HTMLElement description = (HTMLElement) DomGlobal.document.createElement("dt");

    @DataField
    protected HTMLElement numberOfTime = (HTMLElement) DomGlobal.document.createElement("dd");

    @Override
    public void setDescriptionValue(String description) {
        this.description.textContent = description;
    }

    @Override
    public void setElementValue(String numberOfTime) {
        this.numberOfTime.textContent = numberOfTime;
    }

    @Override
    public HTMLElement getDescription() {
        return description;
    }

    @Override
    public HTMLElement getNumberOfTime() {
        return numberOfTime;
    }
}
