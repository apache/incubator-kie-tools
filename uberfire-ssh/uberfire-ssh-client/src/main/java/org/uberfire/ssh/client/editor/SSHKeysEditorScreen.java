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

package org.uberfire.ssh.client.editor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.ssh.client.editor.component.SSHKeysEditor;
import org.uberfire.ssh.client.resources.i18n.AppformerSSHConstants;

@ApplicationScoped
@WorkbenchScreen(identifier = SSHKeysEditorScreen.SCREEN_ID)
public class SSHKeysEditorScreen {

    public static final String SCREEN_ID = "SSHKeysEditorScreen";

    private SSHKeysEditor editor;
    private TranslationService translationService;

    @Inject
    public SSHKeysEditorScreen(SSHKeysEditor editor, TranslationService translationService) {
        this.editor = editor;
        this.translationService = translationService;
    }

    @OnOpen
    public void onOpen() {
        editor.load();
    }

    @WorkbenchPartView
    public IsElement getView() {
        return editor;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.getTranslation(AppformerSSHConstants.SSHKeysEditorScreenTitle);
    }
}
