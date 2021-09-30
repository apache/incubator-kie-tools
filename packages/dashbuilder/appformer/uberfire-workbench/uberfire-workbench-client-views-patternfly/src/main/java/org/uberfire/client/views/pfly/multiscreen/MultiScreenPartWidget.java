/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.views.pfly.multiscreen;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.PartDefinition;

@Dependent
@Named("MultiScreenPartWidget")
@Templated
public class MultiScreenPartWidget extends Composite implements MultiPartWidget {

    @Inject
    @DataField("parts")
    HTMLDivElement content;

    @Inject
    ManagedInstance<MultiScreenView> multiScreenViews;

    @Inject
    PanelManager panelManager;

    @Inject
    MultiScreenMenuBuilder menuBuilder;

    private HashMap<PartDefinition, MultiScreenView> parts = new LinkedHashMap<>();

    @Override
    public void setPresenter(final WorkbenchPanelPresenter presenter) {
        //no-op
    }

    @Override
    public void setDndManager(WorkbenchDragAndDropManager dndManager) {
        //no-op
    }

    @Override
    public void clear() {
        multiScreenViews.destroyAll();
        parts.values().forEach(s -> content.removeChild(s.getElement()));
        parts.clear();
    }

    @Override
    public void addPart(final WorkbenchPartPresenter.View view) {
        view.getPresenter().getMenus(menus -> {
            final PartDefinition partDefinition = view.getPresenter().getDefinition();
            if (parts.containsKey(partDefinition) == false) {
                final MultiScreenView screen = multiScreenViews.get();
                screen.setContent(view);
                screen.setTitle(view.getPresenter().getTitle());
                if (view.getPresenter().getTitleDecoration() != null) {
                    screen.setTitleWidget(view.getPresenter().getTitleDecoration());
                }
                if (parts.isEmpty() && partDefinition.getParentPanel().getPosition() == null) {
                    screen.disableClose();
                }
                screen.setCloseHandler(() -> panelManager.closePart(partDefinition));

                Optional.ofNullable(menus)
                        .ifPresent(m -> m.getItems().stream().map(menuBuilder)
                                .forEachOrdered(e -> e.ifPresent(element -> screen.addMenus(element)))
                        );

                content.appendChild(screen.getElement());
                parts.put(partDefinition,
                          screen);
            }

            selectPart(partDefinition);
        });
    }

    @Override
    public void changeTitle(final PartDefinition part,
                            final String title,
                            final IsWidget titleDecoration) {
        final MultiScreenView screen = parts.get(part);
        if (screen != null) {
            screen.setTitle(title);
            if (titleDecoration != null) {
                screen.setTitleWidget(titleDecoration);
            }
        }
    }

    @Override
    public boolean selectPart(final PartDefinition part) {
        if (parts.containsKey(part) == false) {
            return false;
        }

        parts.entrySet().forEach(e -> {
            if (e.getKey().equals(part)) {
                e.getValue().show();
            } else {
                e.getValue().hide();
            }
        });
        return true;
    }

    @Override
    public boolean remove(final PartDefinition part) {
        final MultiScreenView screen = parts.remove(part);
        if (screen == null) {
            return false;
        }

        multiScreenViews.destroy(screen);
        content.removeChild(screen.getElement());

        parts.values().stream().reduce((f, s) -> s).ifPresent(s -> s.show());

        return true;
    }

    @Override
    public void setFocus(boolean hasFocus) {
        //no-op
    }

    @Override
    public void addOnFocusHandler(final Command doWhenFocused) {
        //no-op
    }

    @Override
    public int getPartsSize() {
        return parts.size();
    }

    @Override
    public Collection<PartDefinition> getParts() {
        return Collections.unmodifiableSet(parts.keySet());
    }

    @Override
    public HandlerRegistration addBeforeSelectionHandler(final BeforeSelectionHandler<PartDefinition> handler) {
        return addHandler(handler,
                          BeforeSelectionEvent.getType());
    }

    @Override
    public HandlerRegistration addSelectionHandler(final SelectionHandler<PartDefinition> handler) {
        return addHandler(handler,
                          SelectionEvent.getType());
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void onResize() {
        parts.values().stream().filter(s -> s.isVisible()).forEach(s -> s.onResize());
    }
}
