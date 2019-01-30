/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.command.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.Command;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.omg.CORBA.Object;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ReverseCommandTest {

    @Mock
    private Command command;

    @Mock
    private Object context;

    private ReverseCommand reverseCommand;

    @Before
    public void setUp() throws Exception {
        reverseCommand = new ReverseCommand(command);
    }

    @Test
    public void testAllow() throws Exception {
        reverseCommand.allow(context);

        verify(command).allow(eq(context));
    }

    @Test
    public void testExecute() throws Exception {
        reverseCommand.execute(context);

        verify(command).undo(eq(context));
    }

    @Test
    public void testUndo() throws Exception {
        reverseCommand.undo(context);

        verify(command).execute(eq(context));
    }
}