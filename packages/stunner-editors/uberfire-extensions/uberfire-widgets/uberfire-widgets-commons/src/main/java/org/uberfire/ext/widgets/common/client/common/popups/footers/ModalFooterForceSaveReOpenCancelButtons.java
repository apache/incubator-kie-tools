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
package org.uberfire.ext.widgets.common.client.common.popups.footers;

import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.uibinder.client.UiBinder;
import org.gwtproject.uibinder.client.UiField;
import org.gwtproject.uibinder.client.UiHandler;
import org.gwtproject.uibinder.client.UiTemplate;
import org.gwtproject.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.uberfire.mvp.Command;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * A Modal Footer with OK and Cancel buttons
 */
public class ModalFooterForceSaveReOpenCancelButtons extends ModalFooter {

    private static ModalFooterForceSaveReOpenCancelButtonsBinder uiBinder = new ModalFooterForceSaveReOpenCancelButtons_ModalFooterForceSaveReOpenCancelButtonsBinderImpl();

    private final Command forceSaveCommand;
    private final Command reopenCommand;
    private final Command cancelCommand;

    private final Modal panel;
    @UiField
    Button forceSaveButton;
    @UiField
    Button reopenButton;
    @UiField
    Button cancelButton;

    public ModalFooterForceSaveReOpenCancelButtons(final Modal panel,
                                                   final Command forceSaveCommand,
                                                   final Command reopenCommand,
                                                   final Command cancelCommand) {
        this.forceSaveCommand = checkNotNull("forceSaveCommand",
                                             forceSaveCommand);
        this.reopenCommand = checkNotNull("reopenCommand",
                                          reopenCommand);
        this.cancelCommand = checkNotNull("cancelCommand",
                                          cancelCommand);
        this.panel = checkNotNull("panel",
                                  panel);
        add(uiBinder.createAndBindUi(this));
    }

    @UiHandler("forceSaveButton")
    public void onForceSaveButtonClick(final ClickEvent e) {
        if (forceSaveCommand != null) {
            forceSaveCommand.execute();
        }
        panel.hide();
    }

    @UiHandler("reopenButton")
    public void onReOpenButtonClick(final ClickEvent e) {
        if (reopenCommand != null) {
            reopenCommand.execute();
        }
        panel.hide();
    }

    @UiHandler("cancelButton")
    public void onCancelButtonClick(final ClickEvent e) {
        if (cancelCommand != null) {
            cancelCommand.execute();
        }
        panel.hide();
    }

    @UiTemplate
    interface ModalFooterForceSaveReOpenCancelButtonsBinder
            extends
            UiBinder<Widget, ModalFooterForceSaveReOpenCancelButtons> {

    }
}
