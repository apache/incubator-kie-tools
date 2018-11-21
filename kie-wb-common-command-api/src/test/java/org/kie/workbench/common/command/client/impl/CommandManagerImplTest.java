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
package org.kie.workbench.common.command.client.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.command.client.Command;
import org.kie.workbench.common.command.client.CommandListener;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CommandManagerImplTest {

    @Mock
    private CommandListener<Object, Object> commandListener;
    
    @Mock
    private Command<Object, Object> command;

    private CommandManagerImpl<Object, Object> commandManager;

    @Before
    public void setup() {
        commandManager = new CommandManagerImpl<>();
        commandManager.setCommandListener(commandListener);
    }

    @Test
    public void allowTest() {
        commandManager.allow(null, command);
        verify(commandListener, times(1)).onAllow(any(), any(), any());
    }

    @Test
    public void executeTest() {
        commandManager.execute(null, command);
        verify(commandListener, times(1)).onExecute(any(), any(), any());
    }

    @Test
    public void undoTest() {
        commandManager.undo(null, command);
        verify(commandListener, times(1)).onUndo(any(), any(), any());
    }

}
