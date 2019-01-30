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

package org.kie.workbench.common.stunner.cm.client.command;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CaseManagementClearCommandTest extends CaseManagementAbstractCommandTest {

    private CaseManagementClearCommand tested;

    @Before
    public void setUp() throws Exception {
        super.setup();

        tested = new CaseManagementClearCommand();
    }

    @Test
    public void testExecute() throws Exception {
        tested.execute(canvasHandler);

        verify(canvasHandler, times(1)).deregister(eq(rootShape), eq(rootNode), eq(true));
        verify(canvasHandler, times(1)).register(eq(SHAPE_SET_ID), eq(rootNode));
    }
}