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

package org.drools.workbench.screens.scenariosimulation.client.metadata;

import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ScenarioHeaderMetaDataTest {

    @Mock
    ScenarioHeaderTextBoxSingletonDOMElementFactory factory;

    @Mock
    FactMapping factMapping;

    @Test
    public void editTest() {
        ScenarioHeaderMetaData scenarioHeaderMetaData = new ScenarioHeaderMetaData("", "", "", factory, false, false);
        scenarioHeaderMetaData.edit(null);

        verify(factory, times(1)).attachDomElement(any(), any(), any());
    }

    @Test(expected = IllegalStateException.class)
    public void editFailTest() {
        ScenarioHeaderMetaData scenarioHeaderMetaData = new ScenarioHeaderMetaData("", "", "", factory, true, false);

        scenarioHeaderMetaData.edit(null);
    }
}