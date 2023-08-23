/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.docks.navigator.included.components;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class DecisionComponentsItem {

    private final View view;

    private DecisionComponent decisionComponent;

    @Inject
    public DecisionComponentsItem(final View view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public View getView() {
        return view;
    }

    public void setDecisionComponent(final DecisionComponent decisionComponent) {
        this.decisionComponent = decisionComponent;
        setupView();
    }

    private void setupView() {
        view.setIcon(getDecisionComponent().getIcon().getUri().asString());
        view.setName(getDecisionComponent().getName());
        view.setFile(getDecisionComponent().getFileName());
        view.setIsImported(getDecisionComponent().isImported());
    }

    DecisionComponent getDecisionComponent() {
        return decisionComponent;
    }

    public void show() {
        HiddenHelper.show(getView().getElement());
    }

    public void hide() {
        HiddenHelper.hide(getView().getElement());
    }

    public DRGElement getDrgElement() {
        return getDecisionComponent().getDrgElement();
    }

    public interface View extends UberElemental<DecisionComponentsItem>,
                                  IsElement {

        void setIcon(final String iconURI);

        void setName(final String name);

        void setFile(final String includedModelName);

        void setIsImported(final boolean imported);
    }
}
