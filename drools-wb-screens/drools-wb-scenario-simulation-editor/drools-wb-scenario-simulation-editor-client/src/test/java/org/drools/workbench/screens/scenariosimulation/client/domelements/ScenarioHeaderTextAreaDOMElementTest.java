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

package org.drools.workbench.screens.scenariosimulation.client.domelements;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioNotificationEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SetHeaderCellValueEvent;
import org.drools.workbench.screens.scenariosimulation.client.factories.AbstractFactoriesTest;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_INDEX;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MULTIPART_VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ROW_INDEX;
import static org.mockito.Matchers.any;
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

    @Before
    public void setup() {
        super.setup();
        when(scenarioGridLayerMock.getDomElementContainer()).thenReturn(new AbsolutePanel());
        scenarioHeaderTextAreaDOMElement = spy(new ScenarioHeaderTextAreaDOMElement(textAreaMock, scenarioGridLayerMock, scenarioGridMock) {
            {
                this.context = contextMock;
            }

            @Override
            public String getValue() {
                return "value";
            }
        });
    }

    @Test
    public void flushMetadataEqualsValue() {
        when(scenarioHeaderMetaDataMock.getTitle()).thenReturn(MULTIPART_VALUE);
        scenarioHeaderTextAreaDOMElement.setScenarioHeaderMetaData(scenarioHeaderMetaDataMock);
        scenarioHeaderTextAreaDOMElement.flush(MULTIPART_VALUE);
        verify(scenarioHeaderTextAreaDOMElement, never()).internalFlush(eq(MULTIPART_VALUE));
    }

    @Test
    public void flushMetadataNotEqualsValue() {
        when(scenarioHeaderMetaDataMock.getTitle()).thenReturn("NOT_VALUE");
        scenarioHeaderTextAreaDOMElement.setScenarioHeaderMetaData(scenarioHeaderMetaDataMock);
        scenarioHeaderTextAreaDOMElement.flush(MULTIPART_VALUE);
        verify(scenarioHeaderTextAreaDOMElement, times(1)).internalFlush(eq(MULTIPART_VALUE));
    }

    @Test
    public void flushNullString() {
        scenarioHeaderTextAreaDOMElement.flush(null);
        verify(eventBusMock, times(1)).fireEvent(isA(ScenarioNotificationEvent.class));
        verify(scenarioHeaderTextAreaDOMElement, never()).internalFlush(any());
    }

    @Test
    public void flushEmptyString() {
        scenarioHeaderTextAreaDOMElement.flush("");
        verify(eventBusMock, times(1)).fireEvent(isA(ScenarioNotificationEvent.class));
        verify(scenarioHeaderTextAreaDOMElement, never()).internalFlush(any());
    }

    @Test
    public void flushEmptyStringMultipleSpaces() {
        scenarioHeaderTextAreaDOMElement.flush("            ");
        verify(eventBusMock, times(1)).fireEvent(isA(ScenarioNotificationEvent.class));
        verify(scenarioHeaderTextAreaDOMElement, never()).internalFlush(any());
    }

    @Test
    public void flushNullMetadata() {
        scenarioHeaderTextAreaDOMElement.setScenarioHeaderMetaData(null);
        scenarioHeaderTextAreaDOMElement.flush(MULTIPART_VALUE);
        verify(scenarioHeaderTextAreaDOMElement, times(1)).internalFlush(eq(MULTIPART_VALUE));
    }

    @Test
    public void internalFlushInvalidHeader() {
        scenarioHeaderTextAreaDOMElement.internalFlush(MULTIPART_VALUE);
        verify(scenarioGridModelMock, never()).updateHeader(eq(COLUMN_INDEX), eq(ROW_INDEX), eq(MULTIPART_VALUE));
    }

    @Test
    public void internalFlushValidHeader() {
        scenarioHeaderTextAreaDOMElement.internalFlush(MULTIPART_VALUE);
        verify(eventBusMock, times(1)).fireEvent(isA(SetHeaderCellValueEvent.class));
    }

    @Test
    public void testDetachCancelEditMode() {
        scenarioHeaderTextAreaDOMElement.detach();

        verify(scenarioHeaderTextAreaDOMElement).flush(eq("value"));
    }
}