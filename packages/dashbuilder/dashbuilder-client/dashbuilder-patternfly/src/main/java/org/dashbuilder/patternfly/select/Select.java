/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.patternfly.select;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class Select {

    private static final int NO_SELECTION = -1;
    @Inject
    View view;
    @Inject
    protected SyncBeanManager beanManager;

    List<SelectItem> itemsRegister;
    private int selectedIndex;
    private Runnable changeAction;

    public interface View extends UberElemental<Select> {

        void addItem(SelectItem selectItem);

        void setPromptText(String hint);

        void clear();

        void closeMenu();

    }

    @PostConstruct
    public void init() {
        view.init(this);
        itemsRegister = new ArrayList<>();
        selectedIndex = NO_SELECTION;
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public void clear() {
        itemsRegister.clear();
        view.clear();
        selectedIndex = NO_SELECTION;
    }

    public void addItem(String value, String id) {
        var selectItem = beanManager.lookupBean(SelectItem.class).newInstance();
        selectItem.setId(id);
        selectItem.setText(value);
        itemsRegister.add(selectItem);
        selectItem.setOnSelectAction(() -> {
            selectedIndex = itemsRegister.indexOf(selectItem);
            // item was unselected
            if (!selectItem.isSelected()) {
                selectedIndex = NO_SELECTION;
            } else {
                itemsRegister.forEach(SelectItem::unselect);
                selectItem.select();
            }
            if (changeAction != null) {
                changeAction.run();
            }
            view.closeMenu();
            itemsRegister.stream()
                    .filter(SelectItem::isSelected)
                    .map(SelectItem::getValue)
                    .findFirst()
                    .ifPresent(this::setPromptText);
        });
        view.addItem(selectItem);
    }

    public String getSelectedValue() {
        return getSelectedItem().map(SelectItem::getValue).orElse(null);
    }

    public int getItemCount() {
        return itemsRegister.size();
    }

    public void addChangeAction(Runnable changeAction) {
        this.changeAction = changeAction;
    }

    public void setSelectedIndex(int i) {
        itemsRegister.forEach(SelectItem::unselect);
        itemsRegister.get(i).select();
    }

    public void setItemTitle(int index, String title) {
        getSelectedItem().ifPresent(item -> item.setText(title));
    }

    public void setPromptText(String hint) {
        view.setPromptText(hint);
    }

    public String getSelectedId() {
        return getSelectedItem().map(SelectItem::getId).orElse(null);
    }

    private Optional<SelectItem> getSelectedItem() {
        if (selectedIndex == NO_SELECTION) {
            return Optional.empty();
        }
        return Optional.ofNullable(itemsRegister.get(selectedIndex));
    }
}
