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

package org.drools.workbench.screens.scenariosimulation.client.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.workbench.screens.scenariosimulation.client.TestProperties;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioCellTextAreaSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.junit.Before;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_ID;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public abstract class AbstractUtilsTest {

    @Mock
    protected ScenarioCellTextAreaSingletonDOMElementFactory scenarioCellTextAreaSingletonDOMElementFactoryMock;

    @Mock
    protected ScenarioHeaderTextBoxSingletonDOMElementFactory scenarioHeaderTextBoxSingletonDOMElementFactoryMock;

    @Mock
    protected ScenarioSimulationBuilders.HeaderBuilder headerBuilderMock;

    @Mock
    protected ScenarioHeaderMetaData scenarioHeaderMetaDataMock;

    protected List<ScenarioHeaderMetaData> scenarioHeaderMetaDataList = new ArrayList<>();


    protected final FactMappingType factMappingType = FactMappingType.valueOf(TestProperties.COLUMN_GROUP_FIRST);

    @Before
    public void setup() {
        when(headerBuilderMock.getColumnId()).thenReturn(COLUMN_ID);
        scenarioHeaderMetaDataList = Collections.singletonList(scenarioHeaderMetaDataMock);
        doReturn(scenarioHeaderMetaDataList).when(headerBuilderMock).build();
    }
}