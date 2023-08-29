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

import bpsim.ElementParameters;
import bpsim.Parameter;
import bpsim.TimeParameters;
import bpsim.impl.BpsimFactoryImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationAttributeSet;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class SimulationAttributeSetsTest {

    private final BpsimFactoryImpl factory = new BpsimFactoryImpl();
    private final ElementParameters simulationParameters = factory.createElementParameters();

    @Test
    public void testNullTimeParameters() {
        assertEquals(new SimulationAttributeSet(), SimulationAttributeSets.of(simulationParameters));
    }

    @Test
    public void testTimeParamsWithNullValue() {
        TimeParameters timeParameters = factory.createTimeParameters();
        simulationParameters.setTimeParameters(timeParameters);

        assertEquals(new SimulationAttributeSet(), SimulationAttributeSets.of(simulationParameters));
    }

    @Test
    public void testTimeParamsWithEmptyParameter() {
        TimeParameters timeParameters = factory.createTimeParameters();
        Parameter parameter = factory.createParameter();
        timeParameters.setProcessingTime(parameter);
        simulationParameters.setTimeParameters(timeParameters);

        assertEquals(new SimulationAttributeSet(), SimulationAttributeSets.of(simulationParameters));
    }
}
