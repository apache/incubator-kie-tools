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
import org.kie.workbench.common.stunner.core.command.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class CompositeCommandBuilderTest {

    private CompositeCommand.Builder builder;

    @Before
    public void setUp() throws Exception {
        builder = new CompositeCommand.Builder();
    }

    @Test
    public void testAddFirstCommand() throws Exception {
        Command c1 = mock(Command.class);
        Command c2 = mock(Command.class);
        Command c3 = mock(Command.class);

        builder.addFirstCommand(c1);
        builder.addFirstCommand(c2);
        builder.addFirstCommand(c3);

        assertEquals(c3, builder.get(0));
        assertEquals(c2, builder.get(1));
        assertEquals(c1, builder.get(2));
    }
}