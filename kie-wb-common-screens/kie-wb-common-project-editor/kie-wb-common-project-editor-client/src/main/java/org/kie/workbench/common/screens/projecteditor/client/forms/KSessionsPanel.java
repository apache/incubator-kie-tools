/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import org.kie.workbench.common.services.shared.kmodule.KSessionModel;
import org.kie.workbench.common.screens.projecteditor.client.widgets.XsdIDValidator;
import org.kie.workbench.common.widgets.client.popups.text.PopupSetFieldCommand;
import org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopup;
import org.uberfire.client.mvp.LockRequiredEvent;

public class KSessionsPanel
        implements KSessionsPanelView.Presenter,
        IsWidget {

    private final KSessionsPanelView view;
    private final TextBoxFormPopup namePopup;
    private List<KSessionModel> items;

    private javax.enterprise.event.Event<LockRequiredEvent> lockRequired;

    @Inject
    public KSessionsPanel(
            KSessionsPanelView view,
            TextBoxFormPopup namePopup,
            javax.enterprise.event.Event<LockRequiredEvent> lockRequired) {
        this.view = view;
        this.namePopup = namePopup;
        this.lockRequired = lockRequired;

        view.setPresenter(this);
    }

    public void makeReadOnly() {
        view.makeReadOnly();
    }

    public void setItems(List<KSessionModel> items) {
        this.items = items;
        view.setItemList(items);
    }

    @Override
    public void onAdd() {
        namePopup.show(new PopupSetFieldCommand() {
            @Override
            public void setName(String name) {
                if (XsdIDValidator.validate(name)) {
                    KSessionModel model = new KSessionModel();
                    model.setName(name);

                    items.add(model);
                    view.setItemList(items);

                    namePopup.setOldName("");
                    namePopup.hide();
                    lockRequired.fire( new LockRequiredEvent() );
                } else {
                    view.showXsdIDError();
                }
            }
        });
    }

    @Override
    public void onRename(KSessionModel model, String name) {
        if (XsdIDValidator.validate(name)) {
            model.setName(name);
        } else {
            view.refresh();
            view.showXsdIDError();
        }
    }

    @Override
    public void onDefaultChanged(KSessionModel modelThatChanged) {
        view.setItemList(items);
    }

    @Override
    public void onOptionsSelectedForKSessions(KSessionModel kSessionModel) {
        view.showOptionsPopUp(kSessionModel);
    }

    @Override
    public void onDelete(KSessionModel kSessionModel) {
        items.remove(kSessionModel);
        view.setItemList(items);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void makeEditable() {
        view.makeEditable();
    }
}
