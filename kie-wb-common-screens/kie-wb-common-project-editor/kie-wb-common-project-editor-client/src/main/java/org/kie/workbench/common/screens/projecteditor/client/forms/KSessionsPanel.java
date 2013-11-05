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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import java.util.List;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.KSessionModel;
import org.kie.workbench.common.widgets.client.popups.text.PopupSetFieldCommand;
import org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopup;

public class KSessionsPanel
        implements KSessionsPanelView.Presenter,
        IsWidget {

    private final KSessionsPanelView view;
    private final TextBoxFormPopup namePopup;
    private List<KSessionModel> items;

    @Inject
    public KSessionsPanel(
            KSessionsPanelView view,
            TextBoxFormPopup namePopup) {
        this.view = view;
        this.namePopup = namePopup;

        view.setPresenter(this);
    }
//
//    @Override
//    protected KSessionModel createNew(String name) {
//        KSessionModel kSessionModel = new KSessionModel();
//        kSessionModel.setName(name);
//        return kSessionModel;
//    }

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
            @Override public void setName(String name) {
                KSessionModel model = new KSessionModel();
                model.setName(name);

                items.add(model);
                view.setItemList(items);

                namePopup.setOldName("");
                namePopup.hide();
            }
        });
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
