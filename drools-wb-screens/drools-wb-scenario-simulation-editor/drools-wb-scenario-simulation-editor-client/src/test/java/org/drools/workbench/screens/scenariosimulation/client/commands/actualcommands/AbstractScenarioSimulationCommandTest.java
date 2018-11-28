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

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelPresenter;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.junit.Before;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static org.mockito.Mockito.when;

public abstract class AbstractScenarioSimulationCommandTest extends AbstractScenarioSimulationTest {

    @Mock
    protected ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenterMock;

    @Mock
    protected RightPanelPresenter rightPanelPresenterMock;

    @Mock
    protected EventBus eventBusMock;

    @Mock
    protected List<GridColumn.HeaderMetaData> headerMetaDatasMock;
    @Mock
    protected ScenarioHeaderMetaData informationHeaderMetaDataMock;
    @Mock
    protected ScenarioHeaderMetaData propertyHeaderMetaDataMock;

    protected final String COLUMN_ID = "COLUMN ID";

    protected final String COLUMN_GROUP = FactMappingType.EXPECT.name();

    protected final String FULL_PACKAGE = "test.scesim";

    protected final String VALUE = "VALUE";

    protected final String FULL_CLASS_NAME = FULL_PACKAGE + ".testclass";

    protected final String VALUE_CLASS_NAME = String.class.getName();

    protected final FactMappingType factMappingType = FactMappingType.valueOf(COLUMN_GROUP);


    @Before
    public void setup() {
        super.setup();
        when(informationHeaderMetaDataMock.getTitle()).thenReturn(VALUE);
        when(informationHeaderMetaDataMock.getColumnGroup()).thenReturn(COLUMN_GROUP);
        when(headerMetaDatasMock.get(1)).thenReturn(informationHeaderMetaDataMock);
        when(gridColumnMock.getHeaderMetaData()).thenReturn(headerMetaDatasMock);
        when(gridColumnMock.getInformationHeaderMetaData()).thenReturn(informationHeaderMetaDataMock);
        when(gridColumnMock.getPropertyHeaderMetaData()).thenReturn(propertyHeaderMetaDataMock);
    }
}