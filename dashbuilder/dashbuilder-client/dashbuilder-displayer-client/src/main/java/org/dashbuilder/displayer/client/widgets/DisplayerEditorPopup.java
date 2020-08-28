/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.web.bindery.event.shared.HandlerRegistration;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.resources.i18n.CommonConstants;
import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.gwtbootstrap3.client.shared.event.ModalShownEvent;
import org.gwtbootstrap3.client.shared.event.ModalShownHandler;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.ButtonPressed;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;
import org.uberfire.mvp.Command;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class DisplayerEditorPopup extends BaseModal {

    interface Binder extends UiBinder<ModalBody, DisplayerEditorPopup> {}
    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField(provided = true)
    DisplayerEditor editor;

    private HandlerRegistration showHandlerRegistration;
    private String editDisplayerTitle = CommonConstants.INSTANCE.displayer_editor_title();
    private String newDisplayerTitle = CommonConstants.INSTANCE.displayer_editor_new();
    private DisplayerType displayerType = null;
    private DisplayerSubType displayerSubType = null;
    private ButtonPressed buttonPressed = ButtonPressed.CLOSE;

    @Inject
    public DisplayerEditorPopup(DisplayerEditor editor) {
        this.editor = editor;
        add(uiBinder.createAndBindUi(this));
        ModalFooterOKCancelButtons footer = createModalFooterOKCancelButtons();
        footer.enableCancelButton(true);
        footer.enableOkButton(true);
        add(footer);
        setWidth(1200+"px");
        addHiddenHandler();
    }

    public DisplayerEditorPopup init(DisplayerSettings settings) {
        ModalShownHandler shownHandler = createShownHandler(settings);
        this.showHandlerRegistration = this.addShownHandler(shownHandler);
        show();
        return this;
    }

    public DisplayerSettings getDisplayerSettings() {
        return this.editor.getDisplayerSettings();
    }

    public void setNewDisplayerTitle(String newDisplayerTitle) {
        this.newDisplayerTitle = newDisplayerTitle;
    }

    public void setEditDisplayerTitle(String editDisplayerTitle) {
        this.editDisplayerTitle = editDisplayerTitle;
    }

    public void setDisplayerType(DisplayerType displayerType) {
        this.displayerType = displayerType;
    }

    public void setDisplayerSubType(DisplayerSubType displayerSubType) {
        this.displayerSubType = displayerSubType;
    }

    public void setOnSaveCommand(Command saveCommand) {
        this.editor.setOnSaveCommand(saveCommand);
    }

    public void setOnCloseCommand(Command closeCommand) {
        this.editor.setOnCloseCommand(closeCommand);
    }
    
    public void setTypeSelectorEnabled(boolean enableTypeSelector) {
        this.editor.setTypeSelectorEnabled(enableTypeSelector);
    }

    public void setExternalDisplayerEnabled(boolean enabled) {
        this.editor.setExternalComponentSettingsEnabled(enabled);
    }
    
    /**
     * <p>The popup must be visible in order that the table can display the different row's values. So after popup is shown, initialize the editor.</p>
     */
    protected ModalShownHandler createShownHandler(final DisplayerSettings settings) {

        return new ModalShownHandler() {
            @Override
            public void onShown(ModalShownEvent modalShownEvent) {
                editor.setDisplayerType(displayerType);
                editor.setDisplayerSubType(displayerSubType);
                editor.init(settings);
                setTitle(editor.isBrandNewDisplayer() ? newDisplayerTitle : editDisplayerTitle);
                removeShownHandler();
            }
        };
    }

    protected void removeShownHandler() {
        if (this.showHandlerRegistration != null) {
            this.showHandlerRegistration.removeHandler();
            this.showHandlerRegistration = null;
        }
    }

    protected void addHiddenHandler() {
        addHiddenHandler(new ModalHiddenHandler() {
            @Override
            public void onHidden(ModalHiddenEvent hiddenEvent) {
                if (userPressedCloseOrCancel()) {
                    editor.close();
                }
            }
        } );
    }

    private boolean userPressedCloseOrCancel() {
        return ButtonPressed.CANCEL.equals(buttonPressed) || ButtonPressed.CLOSE.equals(buttonPressed);
    }

    protected ModalFooterOKCancelButtons createModalFooterOKCancelButtons() {
        return new ModalFooterOKCancelButtons(
                new com.google.gwt.user.client.Command() {
                    @Override
                    public void execute() {
                        buttonPressed = ButtonPressed.OK;
                        hide();
                        editor.save();
                    }
                },
                new com.google.gwt.user.client.Command() {
                    @Override
                    public void execute() {
                        buttonPressed = ButtonPressed.CANCEL;
                        hide();
                    }
                });
    }
}
