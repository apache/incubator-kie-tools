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

package org.kie.workbench.common.dmn.client.editors.included;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardsGridComponent;
import org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModal;
import org.uberfire.client.mvp.UberElemental;

public class IncludedModelsPagePresenter {

    private final View view;

    private final DMNCardsGridComponent gridComponent;

    private final IncludedModelModal modal;

    @Inject
    public IncludedModelsPagePresenter(final View view,
                                       final DMNCardsGridComponent gridComponent,
                                       final IncludedModelModal modal) {
        this.view = view;
        this.gridComponent = gridComponent;
        this.modal = modal;
    }

    @PostConstruct
    public void init() {
        getView().init(this);
        getView().setGrid(getGridComponent().getElement());
        getModal().init(this);
    }

    public HTMLElement getElement() {
        return getView().getElement();
    }

    public void refresh() {
        getGridComponent().refresh();
    }

    private DMNCardsGridComponent getGridComponent() {
        return gridComponent;
    }

    private View getView() {
        return view;
    }

    void openIncludeModelModal() {
        getModal().show();
    }

    private IncludedModelModal getModal() {
        return modal;
    }

    public interface View extends UberElemental<IncludedModelsPagePresenter>,
                                  IsElement {

        void setGrid(final HTMLElement grid);
    }
}
