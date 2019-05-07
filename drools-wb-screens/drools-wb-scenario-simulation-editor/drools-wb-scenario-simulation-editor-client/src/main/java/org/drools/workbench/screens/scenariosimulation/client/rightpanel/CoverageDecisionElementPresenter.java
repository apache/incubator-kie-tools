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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;

@Dependent
public class CoverageDecisionElementPresenter implements CoverageDecisionElementView.Presenter {

    @Inject
    protected ViewsProvider viewsProvider;

    private HTMLElement decisionList;

    @Override
    public void initDecisionList(HTMLElement decisionList) {
        this.decisionList = decisionList;
    }

    @Override
    public void addDecisionElementView(String decisionDescription, String decisionValue) {
        CoverageDecisionElementView coverageDecisionElementView = viewsProvider.getDecisionElementView();
        coverageDecisionElementView.setDescriptionValue(decisionDescription);
        coverageDecisionElementView.setDecisionValue(decisionValue);

        this.decisionList.appendChild(coverageDecisionElementView.getDecisionDescription());
        this.decisionList.appendChild(coverageDecisionElementView.getDecisionNumberOfTime());
    }

    public void clear() {
        while (decisionList.firstChild != null) {
            decisionList.removeChild(decisionList.firstChild);
        }
    }
}
