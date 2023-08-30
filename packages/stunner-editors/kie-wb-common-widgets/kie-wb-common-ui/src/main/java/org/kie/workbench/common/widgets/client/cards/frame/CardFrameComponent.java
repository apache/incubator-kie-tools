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


package org.kie.workbench.common.widgets.client.cards.frame;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.widgets.client.cards.CardComponent;
import org.kie.workbench.common.widgets.client.cards.CardsGridComponent;
import org.uberfire.client.mvp.UberElemental;

import static org.kie.workbench.common.widgets.client.cards.CardComponent.DEFAULT_FUNCTION;

public class CardFrameComponent {

    private final View view;

    private CardComponent card;

    private CardsGridComponent grid;

    @Inject
    public CardFrameComponent(final View view) {
        this.view = view;
    }

    @PostConstruct
    void setup() {
        getView().init(this);
    }

    public void initialize(final CardsGridComponent grid,
                           final CardComponent card) {
        this.grid = grid;
        this.card = card;
        refreshView();
    }

    public HTMLElement getElement() {
        return getView().getElement();
    }

    public void refreshView() {
        getView().setupToggleTitle(isToggleTitleEnabled());
        getView().setUUID(getCard().getUUID());
        getView().setIcon(getCard().getIcon().getCssName());
        getView().setTitle(getCard().getTitle());
        getView().setContent(getCard().getContent());
        getView().enableReadOnlyMode();
    }

    void changeTitle() {
        if (getCard().onTitleChanged().apply(getView().getTitle())) {
            refreshView();
        }
    }

    boolean isToggleTitleEnabled() {
        return getCard().onTitleChanged() != DEFAULT_FUNCTION;
    }

    public void enableEditMode() {
        getGrid().enableReadOnlyModeForAllCards();
        getView().enableEditMode();
    }

    public void enableReadOnlyMode() {
        refreshView();
    }

    public View getView() {
        return view;
    }

    CardComponent getCard() {
        return card;
    }

    CardsGridComponent getGrid() {
        return grid;
    }

    public interface View extends UberElemental<CardFrameComponent>,
                                  IsElement {

        void setUUID(final String uuid);

        void setIcon(final String cssClassName);

        void setTitle(final String title);

        void setContent(final HTMLElement content);

        String getTitle();

        void enableReadOnlyMode();

        void enableEditMode();

        void setupToggleTitle(final boolean enabled);
    }
}
