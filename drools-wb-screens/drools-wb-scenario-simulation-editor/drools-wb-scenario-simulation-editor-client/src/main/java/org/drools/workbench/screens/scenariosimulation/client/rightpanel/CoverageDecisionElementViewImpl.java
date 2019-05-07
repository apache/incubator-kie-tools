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

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

/**
 * This class is used to represent a single row in the coverage decision section
 */
@Templated
public class CoverageDecisionElementViewImpl implements CoverageDecisionElementView {

    @DataField("decisionDescription")
    protected HTMLElement decisionDescription = (HTMLElement) DomGlobal.document.createElement("dt");

    @DataField("decisionNumberOfTime")
    protected HTMLElement decisionNumberOfTime = (HTMLElement) DomGlobal.document.createElement("dd");

    @Override
    public void setDescriptionValue(String decisionDescription) {
        this.decisionDescription.textContent = decisionDescription;
    }

    @Override
    public void setDecisionValue(String decisionValue) {
        this.decisionNumberOfTime.textContent = decisionValue;
    }

    @Override
    public HTMLElement getDecisionDescription() {
        return decisionDescription;
    }

    @Override
    public HTMLElement getDecisionNumberOfTime() {
        return decisionNumberOfTime;
    }
}
