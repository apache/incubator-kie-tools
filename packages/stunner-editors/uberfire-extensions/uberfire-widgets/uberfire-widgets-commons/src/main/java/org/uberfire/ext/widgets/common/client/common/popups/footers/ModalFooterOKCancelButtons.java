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

import org.gwtproject.core.client.GWT;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.uibinder.client.UiBinder;
import org.gwtproject.uibinder.client.UiField;
import org.gwtproject.uibinder.client.UiHandler;
import org.gwtproject.uibinder.client.UiTemplate;
import org.gwtproject.user.client.Command;
import org.gwtproject.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.kie.soup.commons.validation.PortablePreconditions;

/**
 * A Modal Footer with OK and Cancel buttons
 */
public class ModalFooterOKCancelButtons extends ModalFooter {

    private static ModalFooterOKCancelButtonsBinder uiBinder = new ModalFooterOKCancelButtons_ModalFooterOKCancelButtonsBinderImpl();

    private final Command okCommand;
    private final Command cancelCommand;
    @UiField
    Button okButton;
    @UiField
    Button cancelButton;

    public ModalFooterOKCancelButtons(final Command okCommand,
                                      final Command cancelCommand) {
        this.okCommand = PortablePreconditions.checkNotNull("okCommand",
                                                            okCommand);
        this.cancelCommand = PortablePreconditions.checkNotNull("cancelCommand",
                                                                cancelCommand);
        add(uiBinder.createAndBindUi(this));
    }

    public void enableOkButton(final boolean enabled) {
        okButton.setEnabled(enabled);
    }

    public void enableCancelButton(final boolean enabled) {
        cancelButton.setEnabled(enabled);
    }

    @UiHandler("okButton")
    public void onOKButtonClick(final ClickEvent e) {
        okCommand.execute();
    }

    @UiHandler("cancelButton")
    public void onCancelButtonClick(final ClickEvent e) {
        cancelCommand.execute();
    }

    @UiTemplate
    interface ModalFooterOKCancelButtonsBinder
            extends
            UiBinder<Widget, ModalFooterOKCancelButtons> {

    }
}
