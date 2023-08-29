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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponent;
import org.uberfire.client.mvp.UberElemental;

import static java.util.stream.Collectors.toList;

public class CardsGridComponent {

    private final View view;

    private final ManagedInstance<CardFrameComponent> cardFrameInstances;

    private HTMLElement emptyStateElement;

    private List<CardFrameComponent> cardFrames = new ArrayList<>();

    @Inject
    public CardsGridComponent(final View view,
                              final ManagedInstance<CardFrameComponent> cardFrameInstances) {
        this.view = view;
        this.cardFrameInstances = cardFrameInstances;
    }

    @PostConstruct
    void init() {
        view.init(this);
    }

    public void setupCards(final List<CardComponent> cards) {

        setCardFrames(asFrames(cards));

        getView().clearGrid();
        asElements(getCardFrames()).forEach(getView()::appendCard);

        setupEmptyState(getCardFrames().isEmpty());
    }

    public void setEmptyState(final HTMLElement emptyStateElement) {
        this.emptyStateElement = emptyStateElement;
    }

    private List<HTMLElement> asElements(final List<CardFrameComponent> cards) {
        return cards.stream().map(CardFrameComponent::getElement).collect(toList());
    }

    private List<CardFrameComponent> asFrames(final List<CardComponent> cards) {
        return cards.stream().map(this::makeFrame).collect(toList());
    }

    private void setupEmptyState(final boolean isEmptyStateEnabled) {
        if (isEmptyStateEnabled && getEmptyStateElement().isPresent()) {
            view.appendCard(getEmptyStateElement().get());
        }
    }

    public void enableReadOnlyModeForAllCards() {
        getCardFrames().forEach(CardFrameComponent::enableReadOnlyMode);
    }

    List<CardFrameComponent> getCardFrames() {
        return cardFrames;
    }

    void setCardFrames(final List<CardFrameComponent> cardFrames) {
        this.cardFrames = cardFrames;
    }

    View getView() {
        return view;
    }

    private Optional<HTMLElement> getEmptyStateElement() {
        return Optional.ofNullable(emptyStateElement);
    }

    private CardFrameComponent makeFrame(final CardComponent card) {
        final CardFrameComponent frame = cardFrameInstances.get();
        frame.initialize(this, card);
        return frame;
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public interface View extends UberElemental<CardsGridComponent>,
                                  IsElement {

        void clearGrid();

        void appendCard(final HTMLElement cardElement);
    }
}
