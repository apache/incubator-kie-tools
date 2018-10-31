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

package org.uberfire.ssh.client.editor.component;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.ssh.client.editor.component.creation.NewSSHKeyModal;
import org.uberfire.ssh.client.editor.component.empty.SSHKeysEditorEmptyStateDisplayer;
import org.uberfire.ssh.client.editor.component.keys.SSHKeysDisplayer;
import org.uberfire.ssh.service.shared.editor.PortableSSHPublicKey;
import org.uberfire.ssh.service.shared.editor.SSHKeyEditorService;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SSHKeysEditorTest {

    @Mock
    private SSHKeysEditorView view;

    @Mock
    private SSHKeysDisplayer keysDisplayer;

    @Mock
    private SSHKeysEditorEmptyStateDisplayer emptyStateDisplayer;

    @Mock
    private NewSSHKeyModal newSSHKeyModal;

    @Mock
    private SSHKeyEditorService service;

    private CallerMock<SSHKeyEditorService> serviceCaller;

    private SSHKeysEditor editor;

    @Before
    public void init() {

        serviceCaller = new CallerMock<>(service);

        editor = spy(new SSHKeysEditor(view, keysDisplayer, emptyStateDisplayer, newSSHKeyModal, serviceCaller));
    }

    @Test
    public void testBasicFunctionality() {
        editor.init();

        verify(view).init(editor);
        verify(keysDisplayer).init(any(), any());
        verify(emptyStateDisplayer).init(any());
        verify(newSSHKeyModal).init(any());

        editor.getElement();

        verify(view).getElement();

        editor.clear();

        verify(view).clear();

        editor.showNewKeyModal();
        verify(newSSHKeyModal).show();

        editor.onAddKey();
        verify(newSSHKeyModal).hide();
        verify(editor).load();
    }

    @Test
    public void testLoadUserKeys() {
        final List<PortableSSHPublicKey> keys = new ArrayList<>();

        keys.add(mock(PortableSSHPublicKey.class));
        keys.add(mock(PortableSSHPublicKey.class));
        keys.add(mock(PortableSSHPublicKey.class));
        keys.add(mock(PortableSSHPublicKey.class));

        loadUserKeys(keys);
    }

    @Test
    public void testLoadUsersEmptyList() {

        loadUserKeys(new ArrayList<>());
    }

    private void loadUserKeys(final List<PortableSSHPublicKey> keys) {

        when(service.getUserKeys()).thenReturn(keys);

        editor.load();

        verify(view).clear();
        verify(view).show(any());

        if (keys.isEmpty()) {
            verify(emptyStateDisplayer).getElement();
        } else {
            verify(keysDisplayer).render(keys);
            verify(keysDisplayer).getElement();
        }
    }

    @Test
    public void testDeleteKey() {
        editor.delete(mock(PortableSSHPublicKey.class));

        verify(service).deleteKey(any());
        verify(editor).load();
    }
}
