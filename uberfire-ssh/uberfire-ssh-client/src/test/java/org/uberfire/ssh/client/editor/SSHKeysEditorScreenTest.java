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

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ssh.client.editor.component.SSHKeysEditor;
import org.uberfire.ssh.client.resources.i18n.AppformerSSHConstants;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SSHKeysEditorScreenTest {

    @Mock
    private SSHKeysEditor editor;

    @Mock
    private TranslationService translationService;

    private SSHKeysEditorScreen screen;

    @Before
    public void init() {
        screen = new SSHKeysEditorScreen(editor, translationService);
    }

    @Test
    public void testOnOpen() {
        screen.onOpen();

        verify(editor).load();
    }

    @Test
    public void testGetView() {
        assertSame(editor, screen.getView());
    }

    @Test
    public void testGetTitle() {
        screen.getTitle();

        verify(translationService).getTranslation(AppformerSSHConstants.SSHKeysEditorScreenTitle);
    }
}
