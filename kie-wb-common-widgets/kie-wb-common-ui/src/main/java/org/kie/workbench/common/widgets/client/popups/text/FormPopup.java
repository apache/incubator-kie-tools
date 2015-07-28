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

package org.kie.workbench.common.widgets.client.popups.text;


public abstract class FormPopup
        implements FormPopupView.Presenter {

    private PopupSetFieldCommand command;

    protected final FormPopupView view;

    public FormPopup(FormPopupView view) {
        this.view = view;
        view.setPresenter(this);
    }

    public void show(PopupSetFieldCommand command) {
        this.command = command;
        view.show();
    }

    @Override
    public void onOk() {
        if (view.getName() != null && !view.getName().trim().equals("")) {
            command.setName(view.getName());
            view.hide();
        } else {
            view.showFieldEmptyWarning();
        }
    }

    public void setOldName(String oldName) {
        view.setName(oldName);
    }

    public void hide() {
        view.hide();
    }

}
