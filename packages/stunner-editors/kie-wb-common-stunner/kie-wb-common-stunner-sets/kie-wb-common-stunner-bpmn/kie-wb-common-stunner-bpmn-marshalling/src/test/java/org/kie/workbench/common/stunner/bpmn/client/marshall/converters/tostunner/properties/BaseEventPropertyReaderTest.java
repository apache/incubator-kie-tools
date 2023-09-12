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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import bpsim.ElementParameters;
import bpsim.Parameter;
import bpsim.ParameterValue;
import bpsim.TimeParameters;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.LinkEventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.emf.common.util.EList;
import org.jboss.drools.DroolsPackage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.common.ConditionExpression;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationAttributeSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TestUtils.assertBounds;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TestUtils.mockBounds;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TestUtils.mockExtensionValues;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TestUtils.mockFormalExpression;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TestUtils.mockNormalDistributionType;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TestUtils.mockPoissonDistributionType;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TestUtils.mockUniformDistributionType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseEventPropertyReaderTest {

    protected static final double RESOLUTION_FACTOR = 1234;
    protected static final float X = 1f;
    protected static final float Y = 2f;

    protected static final String SIGNAL_REF_ID = "SIGNAL_REF_ID";
    protected static final String SIGNAL_NAME = "SIGNAL_NAME";
    protected static final String SCOPE_ELEMENT_NAME = "customScope";
    protected static final String SCOPE = "SCOPE";

    protected static final String LINK_REF_ID = "LINK_REF_ID";

    protected static final String SLADUEDATE_ELEMENT_NAME = "customSLADueDate";
    protected static final String SLADUEDATE = "12/25/1983";

    protected static final String TIME_CYCLE_LANGUAGE = "TIME_CYCLE_LANGUAGE";
    protected static final String TIME_CYCLE = "TIME_CYCLE";
    protected static final String TIME_DATE = "TIME_DATE";
    protected static final String TIME_DURATION = "TIME_DURATION";

    protected static final double MIN = 1;
    protected static final double MAX = 2;
    protected static final double MEAN = 3;
    protected static final double TIME_UNIT = 4;
    protected static final double STANDARD_DEVIATION = 5;

    protected static final String SCRIPT = "SCRIPT";

    @Mock
    protected DefinitionResolver definitionResolver;

    @Mock
    protected BPMNDiagram diagram;

    protected EventPropertyReader propertyReader;

    protected abstract EventPropertyReader newPropertyReader();

    protected abstract void setLinkEventDefinitionOnCurrentMock(EventDefinition eventDefinition);

    protected abstract void setSignalEventDefinitionOnCurrentMock(SignalEventDefinition eventDefinition);

    protected abstract Event getCurrentEventMock();

    @Before
    public void setUp() {
        when(definitionResolver.getResolutionFactor()).thenReturn(RESOLUTION_FACTOR);
        propertyReader = newPropertyReader();
    }

    @Test
    public void testGetSignalRef() {
        SignalEventDefinition eventDefinition = mock(SignalEventDefinition.class);
        when(eventDefinition.getSignalRef()).thenReturn(SIGNAL_REF_ID);
        setSignalEventDefinitionOnCurrentMock(eventDefinition);
        when(definitionResolver.resolveSignalName(SIGNAL_REF_ID)).thenReturn(SIGNAL_NAME);
        assertEquals(SIGNAL_NAME, propertyReader.getSignalRef());
    }

    // TODO: Kogito - @Test
    public void testGetSignalRefWithNoSignal() {
        assertEquals("", propertyReader.getSignalRef());
    }

    @Test
    public void testComputeBounds() {
        Bounds bounds = mockBounds(X, Y);
        org.kie.workbench.common.stunner.core.graph.content.Bounds result = propertyReader.computeBounds(bounds);
        assertBounds(X * RESOLUTION_FACTOR,
                     Y * RESOLUTION_FACTOR,
                     X * RESOLUTION_FACTOR + EventPropertyReader.WIDTH,
                     Y * RESOLUTION_FACTOR + EventPropertyReader.HEIGHT, result);
    }

    @Test
    public void testGetSignalScope() {
        EList<ExtensionAttributeValue> extensionValues = mockExtensionValues(DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA, SCOPE_ELEMENT_NAME, SCOPE);
        Event eventMock = getCurrentEventMock();
        when(eventMock.getExtensionValues()).thenReturn(extensionValues);
        assertEquals(SCOPE, propertyReader.getSignalScope());
    }

    @Test
    public void testGetLinkRef() {
        // Link Event can't be Boundary.
        if (this.getClass() == BoundaryEventPropertyReaderTest.class) {
            return;
        }
        LinkEventDefinition eventDefinition = mock(LinkEventDefinition.class);
        setLinkEventDefinitionOnCurrentMock(eventDefinition);

        when(eventDefinition.getName()).thenReturn(null);
        assertEquals("", propertyReader.getLinkRef());

        when(eventDefinition.getName()).thenReturn(LINK_REF_ID);
        assertEquals(LINK_REF_ID, propertyReader.getLinkRef());

        EventDefinition differentType = mock(EventDefinition.class);
        setLinkEventDefinitionOnCurrentMock(differentType);
        assertEquals("", propertyReader.getLinkRef());
    }

    // TODO: Kogito - @Test
    public void testGetTimerSettings() {
        TimerEventDefinition eventDefinition = mock(TimerEventDefinition.class);
        FormalExpression timeCycle = mockFormalExpression(TIME_CYCLE_LANGUAGE, TIME_CYCLE);
        FormalExpression timeDate = mockFormalExpression(TIME_DATE);
        FormalExpression timeDuration = mockFormalExpression(TIME_DURATION);
        when(eventDefinition.getTimeCycle()).thenReturn(timeCycle);
        when(eventDefinition.getTimeDate()).thenReturn(timeDate);
        when(eventDefinition.getTimeDuration()).thenReturn(timeDuration);
        TimerSettingsValue value = propertyReader.getTimerSettings(eventDefinition);
        assertEquals(TIME_CYCLE_LANGUAGE, value.getTimeCycleLanguage());
        assertEquals(TIME_CYCLE, value.getTimeCycle());
        assertEquals(TIME_DATE, value.getTimeDate());
        assertEquals(TIME_DURATION, value.getTimeDuration());
    }

    @Test
    public void testGetSimulationSetNormalDistribution() {
        ParameterValue paramValue = mockNormalDistributionType(MEAN, STANDARD_DEVIATION);
        testGetSimulationSet(new SimulationAttributeSet(0d, 0d, MEAN, "ms", STANDARD_DEVIATION, "normal"),
                             paramValue);
    }

    @Test
    public void testGetSimulationSetUniformDistribution() {
        ParameterValue paramValue = mockUniformDistributionType(MIN, MAX);
        testGetSimulationSet(new SimulationAttributeSet(MIN, MAX, 0d, "ms", 0d, "uniform"),
                             paramValue);
    }

    @Test
    public void testGetSimulationSetPoissonDistribution() {
        ParameterValue paramValue = mockPoissonDistributionType(MEAN);
        testGetSimulationSet(new SimulationAttributeSet(0d, 0d, MEAN, "ms", 0d, "poisson"),
                             paramValue);
    }

    private void testGetSimulationSet(SimulationAttributeSet expectedResult, ParameterValue distributionType) {
        ElementParameters parameters = mock(ElementParameters.class);
        TimeParameters timeParams = mock(TimeParameters.class);
        when(parameters.getTimeParameters()).thenReturn(timeParams);
        Parameter processingTime = mock(Parameter.class);
        when(timeParams.getProcessingTime()).thenReturn(processingTime);
        EList<ParameterValue> parameterList = mock(EList.class);
        when(processingTime.getParameterValue()).thenReturn(parameterList);
        when(parameterList.get(0)).thenReturn(distributionType);
        when(definitionResolver.resolveSimulationParameters(getCurrentEventMock().getId())).thenReturn(Optional.ofNullable(parameters));
        assertEquals(expectedResult, propertyReader.getSimulationSet());
    }

    // TODO: Kogito - @Test
    public void testGetConditionExpression() {
        for (Scripts.LANGUAGE language : Scripts.LANGUAGE.values()) {
            testGetConditionExpression(new ConditionExpression(new ScriptTypeValue(language.language(), SCRIPT)), language.format(), SCRIPT);
        }
    }

    private void testGetConditionExpression(ConditionExpression expectedValue, String languageFormat, String script) {
        ConditionalEventDefinition eventDefinition = mock(ConditionalEventDefinition.class);
        FormalExpression expression = mockFormalExpression(languageFormat, script);
        when(eventDefinition.getCondition()).thenReturn(expression);
        assertEquals(expectedValue, EventPropertyReader.getConditionExpression(eventDefinition));
    }

    @Test
    public void testGetConditionExpressionNotConfigured() {
        ConditionalEventDefinition eventDefinition = mock(ConditionalEventDefinition.class);
        assertEquals(new ConditionExpression(new ScriptTypeValue(Scripts.LANGUAGE.DROOLS.language(), "")), EventPropertyReader.getConditionExpression(eventDefinition));
    }

    @Test
    public void testCombineEventDefinitions() {
        EventDefinition definition1 = mock(EventDefinition.class);
        EventDefinition definition2 = mock(EventDefinition.class);
        EventDefinition definition3 = mock(EventDefinition.class);
        List<EventDefinition> eventDefinitions = Arrays.asList(definition1, definition2, definition3);

        EventDefinition definitionRef1 = mock(EventDefinition.class);
        EventDefinition definitionRef2 = mock(EventDefinition.class);
        List<EventDefinition> eventDefinitionsRefs = Arrays.asList(definitionRef1, definitionRef2, null);

        List<EventDefinition> result = EventPropertyReader.combineEventDefinitions(eventDefinitions, eventDefinitionsRefs);
        assertEquals(eventDefinitions, result);
    }

    @Test
    public void testSLADueDate() {
        EList<ExtensionAttributeValue> extensionValues = mockExtensionValues(DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA, SLADUEDATE_ELEMENT_NAME, SLADUEDATE);
        Event eventMock = getCurrentEventMock();
        when(eventMock.getExtensionValues()).thenReturn(extensionValues);
        assertEquals(SLADUEDATE, propertyReader.getSlaDueDate());
    }
}
