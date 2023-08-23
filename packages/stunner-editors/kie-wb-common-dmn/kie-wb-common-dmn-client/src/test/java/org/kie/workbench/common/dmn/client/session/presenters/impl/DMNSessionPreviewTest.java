/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.session.presenters.impl;

import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.SetKindCommand;
import org.kie.workbench.common.dmn.client.session.BaseCommandsTest;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPreviewImpl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddNodeCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DMNSessionPreviewTest extends BaseCommandsTest {

    @Mock
    private SessionPreviewImpl delegate;

    private DMNSessionPreview preview;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.preview = new DMNSessionPreview(delegate);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetTheRightFilter() {
        ArgumentCaptor<Predicate> predicateArgumentCaptor = ArgumentCaptor.forClass(Predicate.class);
        preview.init();
        verify(delegate).setCommandAllowed(predicateArgumentCaptor.capture());
        Predicate<Command<AbstractCanvasHandler, CanvasViolation>> predicate = predicateArgumentCaptor.getValue();
        assertTrue(predicate.test(mock(AddNodeCommand.class)));
        assertFalse(predicate.test(mock(SetKindCommand.class)));
    }
}
