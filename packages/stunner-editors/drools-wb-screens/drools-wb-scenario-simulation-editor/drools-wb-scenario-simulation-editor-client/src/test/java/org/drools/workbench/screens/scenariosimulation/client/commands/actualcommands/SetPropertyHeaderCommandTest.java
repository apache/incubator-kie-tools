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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.FactMappingValueType;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioCellTextAreaSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.CLASS_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FULL_PACKAGE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MULTIPART_VALUE_ELEMENTS;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.VALUE_CLASS_NAME;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class SetPropertyHeaderCommandTest extends AbstractSelectedColumnCommandTest {

    @Before
    public void setup() {
        super.setup();
        commandSpy = spy(new SetPropertyHeaderCommand(GridWidget.SIMULATION, FactMappingValueType.NOT_EXPRESSION) {

            @Override
            protected ScenarioGridColumn getScenarioGridColumnLocal(String instanceTitle, String propertyTitle, String columnId, String columnGroup,
                                                                    FactMappingType factMappingType, ScenarioHeaderTextBoxSingletonDOMElementFactory factoryHeader,
                                                                    ScenarioCellTextAreaSingletonDOMElementFactory factoryCell, String placeHolder) {
                return gridColumnMock;
            }
        });
    }

    @Test
    public void executeIfSelected() {
        ((SetPropertyHeaderCommand) commandSpy).executeIfSelectedColumn(scenarioSimulationContextLocal, gridColumnMock);
        verify((SetPropertyHeaderCommand) commandSpy, times(1)).setPropertyHeader(eq(scenarioSimulationContextLocal),
                                                                                  eq(gridColumnMock),
                                                                                  eq(FULL_PACKAGE + "." + CLASS_NAME),
                                                                                  eq(MULTIPART_VALUE_ELEMENTS),
                                                                                  eq(VALUE_CLASS_NAME));
    }

    @Test
    @SuppressWarnings("squid:S2699")
    public void getPropertyHeaderTitle() {
        super.getPropertyHeaderTitle();
    }

    @Test
    @SuppressWarnings("squid:S2699")
    public void getPropertyHeaderType_Expression() {
        super.getPropertyHeaderTitle_Expression();
    }

    @Test
    @SuppressWarnings("squid:S2699")
    public void getPropertyHeaderType_Value() {
        super.getPropertyHeaderTitle_Value();
    }

    @Test
    @SuppressWarnings("squid:S2699")
    public void getMatchingExpressionAlias() {
        super.getMatchingExpressionAlias();
    }

    @Test
    @SuppressWarnings("squid:S2699")
    public void navigateComplexObject() {
        super.navigateComplexObject();
    }

    @Test
    @SuppressWarnings("squid:S2699")
    public void navigateComplexObject3Levels() {
        super.navigateComplexObject3Levels();
    }

    @Test
    @SuppressWarnings("squid:S2699")
    public void manageCollectionProperty() {
        super.manageCollectionProperty();
    }

    @Test
    @SuppressWarnings("squid:S2699")
    public void manageSimpleTypeCollectionProperty() {
        super.manageSimpleTypeCollectionProperty();
    }

}