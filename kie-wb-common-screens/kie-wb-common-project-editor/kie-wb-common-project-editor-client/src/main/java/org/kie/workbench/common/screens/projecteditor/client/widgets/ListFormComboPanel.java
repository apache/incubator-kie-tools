/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.screens.projecteditor.client.widgets;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.services.project.service.model.HasListFormComboPanelProperties;
import org.kie.workbench.common.widgets.client.popups.text.FormPopup;
import org.kie.workbench.common.widgets.client.popups.text.PopupSetFieldCommand;

import java.util.Map;

public abstract class ListFormComboPanel<T extends HasListFormComboPanelProperties>
        implements IsWidget, ListFormComboPanelView.Presenter {

    private Map<String, T> items;
    protected final ListFormComboPanelView view;
    private final FormPopup namePopup;

    private String selectedItemName = null;
    private final Form<T> form;

    public ListFormComboPanel(ListFormComboPanelView view,
                              Form<T> form,
                              FormPopup namePopup) {
        this.view = view;
        this.namePopup = namePopup;
        this.form = form;
        view.setForm(form);
        view.setPresenter(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void setItems(Map<String, T> sessions) {
        view.clearList();

        this.items = sessions;

        for (String name : sessions.keySet()) {
            view.addItem(name);
        }
    }

    @Override
    public void onSelect(String fullName) {
        selectedItemName = fullName;
        T selected = items.get(fullName);
        form.setModel(selected);
        if (selected.isDefault()) {
            view.disableMakeDefault();
        } else {
            view.enableMakeDefault();
        }
    }

    @Override
    public void onAdd() {
        namePopup.setOldName("");
        namePopup.show(new PopupSetFieldCommand() {
            @Override
            public void setName(String name) {
                T model = createNew(name);

                view.addItem(model.getName());
                items.put(model.getName(), model);
                setSelected(model);
            }
        });
    }

    @Override
    public void onRename() {
        namePopup.setOldName(selectedItemName);

        namePopup.show(new PopupSetFieldCommand() {
            @Override
            public void setName(String name) {
                T model = items.get(selectedItemName);
                items.remove(selectedItemName);
                model.setName(name);
                items.put(name, model);

                view.remove(selectedItemName);
                view.addItem(name);

                setSelected(model);
            }
        });
    }

    private void setSelected(T model) {
        selectedItemName = model.getName();
        view.setSelected(model.getName());
        form.setModel(model);
    }

    protected abstract T createNew(String name);

    @Override
    public void onRemove() {
        if (selectedItemName == null) {
            view.showPleaseSelectAnItem();
        } else {
            items.remove(selectedItemName);
            view.remove(selectedItemName);
            selectedItemName = null;
        }
    }

    @Override
    public void onMakeDefault() {
        if (selectedItemName == null) {
            view.showPleaseSelectAnItem();
        } else {
            for (HasListFormComboPanelProperties item : items.values()) {
                item.setDefault(false);
            }
            items.get(selectedItemName).setDefault(true);
            view.disableMakeDefault();
        }
    }
}
