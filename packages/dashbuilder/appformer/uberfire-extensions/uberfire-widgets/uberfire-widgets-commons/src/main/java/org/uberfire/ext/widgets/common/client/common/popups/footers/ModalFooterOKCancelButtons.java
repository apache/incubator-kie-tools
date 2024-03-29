/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.uberfire.ext.widgets.common.client.common.popups.footers;

import java.util.Objects;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ModalFooter;

/**
 * A Modal Footer with OK and Cancel buttons
 */
public class ModalFooterOKCancelButtons extends ModalFooter {

    private static ModalFooterOKCancelButtonsBinder uiBinder = GWT.create(ModalFooterOKCancelButtonsBinder.class);

    private final Command okCommand;
    private final Command cancelCommand;
    @UiField
    Button okButton;
    @UiField
    Button cancelButton;

    public ModalFooterOKCancelButtons(final Command okCommand,
                                      final Command cancelCommand) {
        this.okCommand = checkNotNull("okCommand", okCommand);
        this.cancelCommand = checkNotNull("cancelCommand", cancelCommand);
        add(uiBinder.createAndBindUi(this));
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
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

    interface ModalFooterOKCancelButtonsBinder
            extends
            UiBinder<Widget, ModalFooterOKCancelButtons> {

    }
}
