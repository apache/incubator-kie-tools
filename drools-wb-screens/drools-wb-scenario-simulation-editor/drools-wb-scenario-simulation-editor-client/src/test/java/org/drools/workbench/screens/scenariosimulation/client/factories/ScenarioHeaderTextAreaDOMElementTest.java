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
import org.drools.workbench.screens.scenariosimulation.client.events.SetCellValueEvent;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioHeaderTextAreaDOMElementTest extends AbstractFactoriesTest {

    private ScenarioHeaderTextAreaDOMElement scenarioHeaderTextAreaDOMElement;

    @Mock
    private ScenarioHeaderMetaData scenarioHeaderMetaDataMock;

    private final String VALUE = "VALUE";

    @Before
    public void setup() {
        super.setup();
        scenarioHeaderTextAreaDOMElement = spy(new ScenarioHeaderTextAreaDOMElement(textAreaMock, scenarioGridLayerMock, scenarioGridMock) {
            {
                this.context = contextMock;
            }
        });
    }

    @Test
    public void flushMetadataEqualsValue() {
        when(scenarioHeaderMetaDataMock.getTitle()).thenReturn(VALUE);
        scenarioHeaderTextAreaDOMElement.setScenarioHeaderMetaData(scenarioHeaderMetaDataMock);
        scenarioHeaderTextAreaDOMElement.flush(VALUE);
        verify(scenarioHeaderTextAreaDOMElement, never()).internalFlush(eq(VALUE));
    }

    @Test
    public void flushMetadataNotEqualsValue() {
        when(scenarioHeaderMetaDataMock.getTitle()).thenReturn("NOT_VALUE");
        scenarioHeaderTextAreaDOMElement.setScenarioHeaderMetaData(scenarioHeaderMetaDataMock);
        scenarioHeaderTextAreaDOMElement.flush(VALUE);
        verify(scenarioHeaderTextAreaDOMElement, times(1)).internalFlush(eq(VALUE));
    }

    @Test
    public void flushNullMetadata() {
        scenarioHeaderTextAreaDOMElement.setScenarioHeaderMetaData(null);
        scenarioHeaderTextAreaDOMElement.flush(VALUE);
        verify(scenarioHeaderTextAreaDOMElement, times(1)).internalFlush(eq(VALUE));
    }

    @Test
    public void internalFlushInvalidHeader() {
        when(scenarioGridModelMock.validateHeaderUpdate(eq(VALUE), eq(ROW_INDEX), eq(COLUMN_INDEX), anyBoolean())).thenReturn(false);
        scenarioHeaderTextAreaDOMElement.internalFlush(VALUE);
        verify(scenarioGridModelMock, never()).updateHeader(eq(COLUMN_INDEX), eq(ROW_INDEX), eq(VALUE));
    }

    @Test
    public void internalFlushValidHeader() {
        when(scenarioGridModelMock.validateHeaderUpdate(eq(VALUE), eq(ROW_INDEX), eq(COLUMN_INDEX), anyBoolean())).thenReturn(true);
        scenarioHeaderTextAreaDOMElement.internalFlush(VALUE);
        verify(eventBusMock, times(1)).fireEvent(isA(SetCellValueEvent.class));
    }

}