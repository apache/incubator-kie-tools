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

package org.uberfire.ssh.client.editor.component.empty;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SSHKeysEditorEmptyStateDisplayerTest {

    @Mock
    private SSHKeysEditorEmptyStateDisplayerView view;

    @Mock
    private Command addCommand;

    private SSHKeysEditorEmptyStateDisplayer displayer;

    @Before
    public void init() {
        displayer = new SSHKeysEditorEmptyStateDisplayer(view);
    }

    @Test
    public void testFunctionality() {

        verify(view).init(displayer);

        displayer.getElement();
        verify(view).getElement();

        displayer.init(addCommand);

        displayer.notifyAdd();
        verify(addCommand).execute();
    }

    @Test
    public void testInitNull() {
        Assertions.assertThatThrownBy(() -> displayer.init(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'addCommand' should be not null!");
    }
}
