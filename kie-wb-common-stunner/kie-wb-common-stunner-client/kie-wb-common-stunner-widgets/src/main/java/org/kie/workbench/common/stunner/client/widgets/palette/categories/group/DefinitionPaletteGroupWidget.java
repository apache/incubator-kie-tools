/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.palette.categories.group;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteGroup;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteItemMouseEvent;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;

@Dependent
public class DefinitionPaletteGroupWidget implements DefinitionPaletteGroupWidgetView.Presenter {

    private enum State {
        COMPACT,
        FULL_LIST
    }

    public static final int COMPACT_ELEMENTS_LIST_SIZE = 3;

    private State state = State.COMPACT;

    private List<DefinitionPaletteItemWidget> hiddenList = new ArrayList<>();

    private DefinitionPaletteGroupWidgetView view;

    private DefaultPaletteGroup group;

    private ManagedInstance<DefinitionPaletteItemWidget> definitionPaletteItemWidgets;

    private Consumer<PaletteItemMouseEvent> itemMouseDownCallback;

    @Inject
    public DefinitionPaletteGroupWidget(DefinitionPaletteGroupWidgetView view,
                                        ManagedInstance<DefinitionPaletteItemWidget> definitionPaletteItemWidgets) {
        this.view = view;
        this.definitionPaletteItemWidgets = definitionPaletteItemWidgets;
    }

    @PostConstruct
    public void setUp() {
        view.init(this);
    }

    @Override
    public void initialize(DefaultPaletteGroup group,
                           ShapeFactory<?, ?> shapeFactory,
                           Consumer<PaletteItemMouseEvent> itemMouseDownCallback) {
        this.group = group;
        this.itemMouseDownCallback = (event) -> {
            switchState(State.COMPACT);
            itemMouseDownCallback.accept(new PaletteItemMouseEvent(event.getId(),
                                                                   event.getMouseX(),
                                                                   event.getMouseY(),
                                                                   event.getItemX(),
                                                                   event.getItemY()));
        };

        loadItems(shapeFactory);
    }

    private void loadItems(ShapeFactory<?, ?> shapeFactory) {
        view.initView();
        definitionPaletteItemWidgets.destroyAll();

        List<DefaultPaletteItem> items = group.getItems();

        for (int i = 0; i < items.size(); i++) {
            DefaultPaletteItem item = items.get(i);
            DefinitionPaletteItemWidget itemWidget = definitionPaletteItemWidgets.get();

            itemWidget.initialize(item,
                                  shapeFactory,
                                  itemMouseDownCallback);
            if (i >= COMPACT_ELEMENTS_LIST_SIZE) {
                itemWidget.getElement().getStyle().setProperty("display",
                                                               "none");
                hiddenList.add(itemWidget);
            }
            view.addItem(itemWidget);
        }
        if (!hiddenList.isEmpty()) {
            view.addAnchors();
            view.showMoreAnchor();
        }
    }

    private void switchState(final State state) {
        if (!this.state.equals(state)) {
            this.state = state;
            String displayStyle = state.equals(State.COMPACT) ? "none" : "block";
            hiddenList.forEach(item -> {
                item.getElement().getStyle().setProperty("display",
                                                         displayStyle);
            });

            if (state.equals(State.COMPACT)) {
                view.showMoreAnchor();
            } else {
                view.showLessAnchor();
            }
        }
    }

    @Override
    public void showMore() {
        switchState(State.FULL_LIST);
    }

    @Override
    public void showLess() {
        switchState(State.COMPACT);
    }

    @Override
    public DefaultPaletteGroup getItem() {
        return group;
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @PreDestroy
    public void destroy() {
        definitionPaletteItemWidgets.destroyAll();
    }
}
