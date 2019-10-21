/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.editor.commons.client.history;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.MenuVisitor;

public class SaveButton
        implements MenuCustom<Widget> {

    private ButtonGroup buttonGroup = GWT.create(ButtonGroup.class);
    private Button saveButton = GWT.create(Button.class);
    private DropDownMenu dropDownMenu = GWT.create(DropDownMenu.class);
    private Button caretButton = GWT.create(Button.class);
    private AnchorListItem saveWithCommentsButton = GWT.create(AnchorListItem.class);

    private HandlerRegistration saveButtonClickHandler;
    private HandlerRegistration saveWithCommentsButtonClickHandler;

    public SaveButton() {
        buttonGroup.add(saveButton);

        saveButton.setSize(ButtonSize.SMALL);
        saveButton.setText(CommonConstants.INSTANCE.Save());

        buttonGroup.add(caretButton);

        caretButton.setToggleCaret(true);
        caretButton.setDataToggle(Toggle.DROPDOWN);
        caretButton.setSize(ButtonSize.SMALL);
        caretButton.setMarginRight(10);

        buttonGroup.add(dropDownMenu);

        dropDownMenu.add(saveWithCommentsButton);

        saveWithCommentsButton.setText(CommonConstants.INSTANCE.SaveWithComments());
    }

    @Override
    public Widget build() {
        return buttonGroup;
    }

    @Override
    public boolean isEnabled() {
        return saveButton.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        saveButton.setEnabled(enabled);
        caretButton.setEnabled(enabled);
    }

    @Override
    public String getContributionPoint() {
        return null;
    }

    @Override
    public String getCaption() {
        return null;
    }

    @Override
    public MenuPosition getPosition() {
        return null;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void accept(final MenuVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void addEnabledStateChangeListener(EnabledStateChangeListener listener) {

    }

    @Override
    public String getIdentifier() {
        return null;
    }

    public void setTextToSave() {
        saveButton.setText(CommonConstants.INSTANCE.Save());
        saveWithCommentsButton.setText(CommonConstants.INSTANCE.SaveWithComments());
    }

    public void setTextToRestore() {
        saveButton.setText(CommonConstants.INSTANCE.Restore());
        saveWithCommentsButton.setText(CommonConstants.INSTANCE.RestoreWithComments());
    }

    public void setCommand(Command command) {
        if (saveButtonClickHandler != null) {
            saveButtonClickHandler.removeHandler();
        }
        saveButtonClickHandler = saveButton.addClickHandler(e -> command.execute());

        if (saveWithCommentsButtonClickHandler != null) {
            saveWithCommentsButtonClickHandler.removeHandler();
        }
        caretButton.setVisible(false);
    }

    public void setCommand(ParameterizedCommand<Boolean> command) {
        if (saveButtonClickHandler != null) {
            saveButtonClickHandler.removeHandler();
        }
        saveButtonClickHandler = saveButton.addClickHandler(e -> command.execute(false));

        if (saveWithCommentsButtonClickHandler != null) {
            saveWithCommentsButtonClickHandler.removeHandler();
        }
        saveWithCommentsButtonClickHandler = saveWithCommentsButton.addClickHandler(e -> command.execute(true));
        caretButton.setVisible(true);
    }
}
