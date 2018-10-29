/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.experimental.screens.explorer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import org.gwtbootstrap3.client.ui.TextBox;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKButton;

@Dependent
public class NewAssetPopup extends BaseModal {

    @Inject
    private TextBox assetName;

    private ModalFooterOKButton footer;

    @PostConstruct
    public void init() {
        setTitle("New Asset");
        setBody(assetName);
    }

    public String getAssetName() {
        return assetName.getText();
    }

    public void init(final Command command) {
        footer = new ModalFooterOKButton(command);
        add(footer);
    }

    @Override
    public void show() {
        assetName.setText("");
        super.show();
    }
}
