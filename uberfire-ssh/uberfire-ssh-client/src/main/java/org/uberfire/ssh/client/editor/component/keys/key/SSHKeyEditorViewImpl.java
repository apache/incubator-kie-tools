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

package org.uberfire.ssh.client.editor.component.keys.key;

import java.util.Date;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLLabelElement;
import org.gwtbootstrap3.client.ui.ModalSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ssh.client.resources.i18n.AppformerSSHConstants;
import org.uberfire.ssh.service.shared.editor.PortableSSHPublicKey;

@Templated
public class SSHKeyEditorViewImpl implements SSHKeyEditorView,
                                             IsElement {

    private static final DateTimeFormat format = DateTimeFormat.getFormat("MMMM dd, yyyy");

    @Inject
    @DataField
    private HTMLLabelElement name;

    @Inject
    @DataField
    private HTMLLabelElement added;

    @Inject
    @DataField
    private HTMLLabelElement lastUsed;

    @Inject
    @DataField
    private HTMLButtonElement delete;

    @Inject
    private TranslationService translationService;

    private Presenter presenter;

    @Override
    public void clear() {
        name.textContent = "";
        added.textContent = "";
        lastUsed.textContent = "";
    }

    @Override
    public void render(PortableSSHPublicKey key) {
        name.textContent = key.getName();
        added.textContent = format(key.getCreationDate(), AppformerSSHConstants.SSHKeyEditorViewImplAddedOn);
        lastUsed.textContent = format(key.getLastTimeUsed(), AppformerSSHConstants.SSHKeyEditorViewImplLastUsed);
    }

    private String format(Date date, String key) {
        if (date == null) {
            return "";
        }

        return translationService.format(key, format.format(date));
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("delete")
    public void onDelete(ClickEvent event) {
        YesNoCancelPopup popupup = YesNoCancelPopup.newYesNoCancelPopup(translationService.getTranslation(AppformerSSHConstants.SSHKeyEditorViewImplDelete),
                                                                        translationService.getTranslation(AppformerSSHConstants.SSHKeyEditorViewImplDeleteMessage),
                                                                        null, null, null,
                                                                        () -> {
                                                                        },
                                                                        translationService.getTranslation(AppformerSSHConstants.SSHKeyEditorViewImplCancel),
                                                                        ButtonType.DEFAULT,
                                                                        () -> presenter.notifyDelete(),
                                                                        translationService.getTranslation(AppformerSSHConstants.SSHKeyEditorViewImplDelete),
                                                                        ButtonType.DANGER);

        popupup.setSize(ModalSize.SMALL);
        popupup.clearScrollHeight();

        popupup.show();
    }
}
