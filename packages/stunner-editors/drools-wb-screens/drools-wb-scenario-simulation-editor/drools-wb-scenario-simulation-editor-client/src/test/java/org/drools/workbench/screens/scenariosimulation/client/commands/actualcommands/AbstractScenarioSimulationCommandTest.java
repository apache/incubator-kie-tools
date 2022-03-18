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

package org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public abstract class AbstractScenarioSimulationCommandTest extends AbstractScenarioSimulationTest {

    @Mock
    protected TestToolsPresenter testToolsPresenterMock;

    @Mock
    protected EventBus eventBusMock;

    protected AbstractScenarioSimulationCommand command;

    @Before
    public void setup() {
        super.setup();
    }

    @Test(expected = IllegalStateException.class)
    public void undo() {
        command.undo(scenarioSimulationContextLocal);
    }

    @Test
    public void execute() {
        command.execute(scenarioSimulationContextLocal);
        try {
            verify(command, times(1)).internalExecute(eq(scenarioSimulationContextLocal));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}