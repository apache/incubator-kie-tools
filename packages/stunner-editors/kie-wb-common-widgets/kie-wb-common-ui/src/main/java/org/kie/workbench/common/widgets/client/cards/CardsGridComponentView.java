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


package org.kie.workbench.common.widgets.client.cards;

import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class CardsGridComponentView implements CardsGridComponent.View {

    private CardsGridComponent presenter;

    @DataField("card-grid")
    private final HTMLDivElement cardGrid;

    @Inject
    public CardsGridComponentView(final HTMLDivElement cardGrid) {
        this.cardGrid = cardGrid;
    }

    @Override
    public void init(final CardsGridComponent presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clearGrid() {
        cardGrid.innerHTML = "";
    }

    @Override
    public void appendCard(final HTMLElement cardElement) {
        cardGrid.appendChild(cardElement);
    }
}
