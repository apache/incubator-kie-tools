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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.session.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class SaveDiagramSessionCommandTest {

    @Mock
    private ClientSession session;

    @Mock
    private ClientSessionCommand.Callback<Object> callback;

    private SaveDiagramSessionCommand command;

    @Before
    public void setup() {
        this.command = new SaveDiagramSessionCommand();
    }

    @Test
    public void testExecute() {
        command.execute(callback);

        verifyNoMoreInteractions(callback);
    }

    @Test
    public void testAccepts() {
        assertThat(command.accepts(null)).isFalse();
        assertThat(command.accepts(session)).isFalse();
    }

    @Test
    public void testEnable() {
        command.enable(false);
        assertThat(command.isEnabled()).isFalse();

        command.enable(true);
        assertThat(command.isEnabled()).isFalse();
    }

    @Test
    public void testSetEnabled() {
        command.setEnabled(false);
        assertThat(command.isEnabled()).isFalse();

        command.setEnabled(true);
        assertThat(command.isEnabled()).isFalse();
    }
}
