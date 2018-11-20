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

package org.drools.workbench.screens.scenariosimulation.client.factories;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioCellTextAreaDOMElementTest extends AbstractFactoriesTest {

    private ScenarioCellTextAreaDOMElement scenarioCellTextAreaDOMElement;

    @Before
    public void setup() {
        super.setup();
        scenarioCellTextAreaDOMElement = spy(new ScenarioCellTextAreaDOMElement(textAreaMock, scenarioGridLayerMock, scenarioGridMock) {
            {
                this.context = contextMock;
            }
        });
    }

    @Test
    public void flush() {
        scenarioCellTextAreaDOMElement.flush("");
        verify(scenarioGridModelMock, times(1)).setCellValue(eq(ROW_INDEX), eq(COLUMN_INDEX), isA(ScenarioGridCellValue.class));
        verify(scenarioGridModelMock, times(1)).resetErrors(anyInt());
    }
}