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

package org.kie.workbench.common.dmn.client.editors.included.grid;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.appformer.kogito.bridge.client.workspace.WorkspaceService;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.client.common.KogitoChannelHelper;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.DMNIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageState;
import org.kie.workbench.common.dmn.client.editors.included.PMMLIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.grid.empty.DMNCardsEmptyStateView;
import org.kie.workbench.common.widgets.client.cards.CardComponent;
import org.kie.workbench.common.widgets.client.cards.CardsGridComponent;

public class DMNCardsGridComponent {

    private final ManagedInstance<DMNCardComponent> dmnCardComponent;

    private final ManagedInstance<PMMLCardComponent> pmmlCardComponent;

    private final ManagedInstance<DefaultCardComponent> defaultCardComponent;

    private final CardsGridComponent cardsGridComponent;

    private final IncludedModelsPageState pageState;

    private final DMNCardsEmptyStateView emptyStateView;

    private final WorkspaceService workspaceService;

    private final KogitoChannelHelper kogitoChannelHelper;

    @Inject
    public DMNCardsGridComponent(final ManagedInstance<DMNCardComponent> dmnCardComponent,
                                 final ManagedInstance<PMMLCardComponent> pmmlCardComponent,
                                 final ManagedInstance<DefaultCardComponent> defaultCardComponent,
                                 final CardsGridComponent cardsGridComponent,
                                 final IncludedModelsPageState pageState,
                                 final DMNCardsEmptyStateView emptyStateView,
                                 final WorkspaceService workspaceService,
                                 final KogitoChannelHelper kogitoChannelHelper) {
        this.dmnCardComponent = dmnCardComponent;
        this.pmmlCardComponent = pmmlCardComponent;
        this.defaultCardComponent = defaultCardComponent;
        this.cardsGridComponent = cardsGridComponent;
        this.pageState = pageState;
        this.emptyStateView = emptyStateView;
        this.workspaceService = workspaceService;
        this.kogitoChannelHelper = kogitoChannelHelper;
    }

    @PostConstruct
    public void init() {
        cardsGridComponent.setEmptyState(emptyStateView.getElement());
    }

    public void refresh() {
        cardsGridComponent.setupCards(cards(generateIncludedModels()));
    }

    public HTMLElement getElement() {
        return cardsGridComponent.getElement();
    }

    public boolean presentPathAsLink() {
        return kogitoChannelHelper.isIncludedModelLinkEnabled();
    }

    public void openPathLink(final String normalizedPosixPathRelativeToTheOpenFile) {
        workspaceService.openFile(normalizedPosixPathRelativeToTheOpenFile);
    }

    private List<CardComponent> cards(final List<BaseIncludedModelActiveRecord> includes) {
        return includes.stream().map(this::card).collect(Collectors.toList());
    }

    private BaseCardComponent card(final BaseIncludedModelActiveRecord includedModel) {
        BaseCardComponent card;
        if (includedModel instanceof DMNIncludedModelActiveRecord) {
            card = dmnCardComponent.get();
        } else if (includedModel instanceof PMMLIncludedModelActiveRecord) {
            card = pmmlCardComponent.get();
        } else {
            card = defaultCardComponent.get();
        }
        card.setup(this, includedModel);
        return card;
    }

    private List<BaseIncludedModelActiveRecord> generateIncludedModels() {
        return pageState.generateIncludedModels();
    }
}
