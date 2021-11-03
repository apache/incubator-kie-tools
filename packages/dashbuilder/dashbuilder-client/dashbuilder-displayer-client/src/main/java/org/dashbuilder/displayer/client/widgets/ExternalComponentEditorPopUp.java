/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.displayer.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;
import org.uberfire.mvp.Command;

@Dependent
public class ExternalComponentEditorPopUp extends BaseModal {
    
    private final CommonConstants i18n = CommonConstants.INSTANCE;

    @Inject
    ExternalComponentEditor externalComponentEditor;
    private Command closeCommand;
    private Command saveCommand;

    @PostConstruct
    public void setup() {
        ModalFooterOKCancelButtons footer = createModalFooterOKCancelButtons();
        footer.enableCancelButton(true);
        footer.enableOkButton(true);
        setBody(externalComponentEditor.asWidget());
        add(footer);
        setTitle(i18n.componentEditor());
        setWidth(1200 + "px");
    }

    public void init(String componentId, Map<String, String> properties, Command closeCommand, Command saveCommand) {
        this.closeCommand = closeCommand;
        this.saveCommand = saveCommand;
        this.addShowHandler(e -> externalComponentEditor.withComponent(componentId, properties));
        show();
    }

    protected ModalFooterOKCancelButtons createModalFooterOKCancelButtons() {
        return new ModalFooterOKCancelButtons(() -> {
            hide();
            saveCommand.execute();

        }, () -> {
            hide();
            closeCommand.execute();
        });
    }

    public Map<String, String> getProperties() {
        return externalComponentEditor.getNewProperties();
    }

}