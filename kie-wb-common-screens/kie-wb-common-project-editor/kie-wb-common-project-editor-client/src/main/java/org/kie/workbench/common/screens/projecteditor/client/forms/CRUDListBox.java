/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import org.kie.workbench.common.widgets.client.popups.text.PopupSetFieldCommand;
import org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopup;
import org.uberfire.client.mvp.LockRequiredEvent;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

//@Dependent
public class CRUDListBox
        implements HasRemoveItemHandlers,
        HasAddItemHandlers,
        IsWidget,
        CRUDListBoxView.Presenter {

    private CRUDListBoxView view;
    private TextBoxFormPopup newItemPopup;

    @Inject
    javax.enterprise.event.Event<LockRequiredEvent> lockRequired;
    
    public CRUDListBox() {
    }

    @Inject
    public CRUDListBox(final CRUDListBoxView view,
                       TextBoxFormPopup newItemPopup) {
        this.view = view;
        this.newItemPopup = newItemPopup;
        view.setPresenter(this);
    }

    @Override
    public void onAdd() {
        newItemPopup.setOldName("");
        newItemPopup.show(new PopupSetFieldCommand() {
            @Override
            public void setName(String name) {
                view.addItemAndFireEvent(name);
                newItemPopup.hide();
                fireLockRequiredEvent();
            }
        });
    }

    @Override
    public void onDelete() {
        if (view.getSelectedItem() != null) {
            view.removeItem(view.getSelectedItem());
            fireLockRequiredEvent();
        }
    }

    @Override
    public HandlerRegistration addRemoveItemHandler(RemoveItemHandler handler) {
        return view.addRemoveItemHandler(handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        view.fireEvent(event);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void addItem(String name) {
        view.addItem(name);
    }

    @Override
    public HandlerRegistration addAddItemHandler(AddItemHandler handler) {
        return view.addAddItemHandler(handler);
    }

    public void makeReadOnly() {
        view.makeReadOnly();
    }

    public void makeEditable() {
        view.makeEditable();
    }

    public void clear() {
        view.clear();
    }
    
    private void fireLockRequiredEvent() {
        if (lockRequired != null) {
            lockRequired.fire( new LockRequiredEvent() );
        }
    }
}
