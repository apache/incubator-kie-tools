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

package org.uberfire.ssh.client.editor.component.keys;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.ssh.client.editor.component.keys.key.SSHKeyEditor;
import org.uberfire.ssh.service.shared.editor.PortableSSHPublicKey;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SSHKeysDisplayerTest {

    @Mock
    private SSHKeysDisplayerView view;

    @Mock
    private ManagedInstance<SSHKeyEditor> editors;

    @Mock
    private Command addCommand;

    @Mock
    private ParameterizedCommand<PortableSSHPublicKey> deleteCommand;

    @Mock
    private PortableSSHPublicKey key;

    private List<SSHKeyEditor> createdEditors = new ArrayList<>();

    private SSHKeysDisplayer displayer;

    @Before
    public void init() {
        when(editors.get()).thenAnswer((Answer<SSHKeyEditor>) invocationOnMock -> {
            SSHKeyEditor editor = mock(SSHKeyEditor.class);

            createdEditors.add(editor);

            return editor;
        });

        displayer = new SSHKeysDisplayer(view, editors);
    }

    @Test
    public void testFunctionality() {
        verify(view).init(displayer);

        displayer.init(addCommand, deleteCommand);

        displayer.getElement();
        verify(view).getElement();

        displayer.clear();
        verify(view).clear();
        verify(editors).destroyAll();

        displayer.notifyAdd();
        verify(addCommand).execute();

        displayer.onDelete(key);
        verify(deleteCommand).execute(key);
    }

    @Test
    public void testRenderKeys() {
        List<PortableSSHPublicKey> keys = new ArrayList<>();
        keys.add(key);
        keys.add(mock(PortableSSHPublicKey.class));
        keys.add(mock(PortableSSHPublicKey.class));

        displayer.render(keys);

        assertEquals(keys.size(), createdEditors.size());

        verify(editors, times(keys.size())).get();

        createdEditors.forEach(editor -> verify(editor).render(any(), any()));
    }
}
