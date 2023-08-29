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
import bpsim.NormalDistributionType;
import bpsim.Parameter;
import bpsim.ParameterValue;
import bpsim.PoissonDistributionType;
import bpsim.TimeParameters;
import bpsim.UniformDistributionType;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Match;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationAttributeSet;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpsim;

public class SimulationAttributeSets {

    public static SimulationAttributeSet of(ElementParameters eleType) {
        TimeParameters timeParams = eleType.getTimeParameters();
        if (timeParams == null) {
            return new SimulationAttributeSet();
        }
        Parameter processingTime = timeParams.getProcessingTime();
        if (processingTime == null
                || processingTime.getParameterValue() == null
                || processingTime.getParameterValue().isEmpty()) {
            return new SimulationAttributeSet();
        }
        ParameterValue paramValue = processingTime.getParameterValue().get(0);

        return Match.<ParameterValue, SimulationAttributeSet>of()
                .<NormalDistributionType>when(e -> e instanceof NormalDistributionType, ndt -> {
                    SimulationAttributeSet simulationSet = new SimulationAttributeSet();
                    simulationSet.getMean().setValue(ndt.getMean());
                    simulationSet.getStandardDeviation().setValue(ndt.getStandardDeviation());
                    simulationSet.getDistributionType().setValue("normal");
                    return simulationSet;
                })
                .<UniformDistributionType>when(e -> e instanceof UniformDistributionType, udt -> {
                    SimulationAttributeSet simulationSet = new SimulationAttributeSet();
                    simulationSet.getMin().setValue(udt.getMin());
                    simulationSet.getMax().setValue(udt.getMax());
                    simulationSet.getDistributionType().setValue("uniform");
                    return simulationSet;
                })
                .<PoissonDistributionType>when(e -> e instanceof PoissonDistributionType, pdt -> {
                    SimulationAttributeSet simulationSet = new SimulationAttributeSet();
                    simulationSet.getMean().setValue(pdt.getMean());
                    simulationSet.getDistributionType().setValue("poisson");
                    return simulationSet;
                })
                .apply(paramValue)
                .asSuccess()
                .value();
    }

    public static ElementParameters toElementParameters(SimulationAttributeSet simulationSet) {
        ElementParameters elementParameters = bpsim.createElementParameters();

        TimeParameters timeParameters = bpsim.createTimeParameters();
        Parameter processingTime = bpsim.createParameter();
        timeParameters.setProcessingTime(processingTime);

        switch (simulationSet.getDistributionType().getValue()) {
            case "normal":
                NormalDistributionType ndt = bpsim.createNormalDistributionType();
                ndt.setMean(simulationSet.getMean().getValue());
                ndt.setStandardDeviation(simulationSet.getStandardDeviation().getValue());
                processingTime.getParameterValue().add(ndt);

                break;
            case "uniform":
                UniformDistributionType udt = bpsim.createUniformDistributionType();
                udt.setMin(simulationSet.getMin().getValue());
                udt.setMax(simulationSet.getMax().getValue());
                processingTime.getParameterValue().add(udt);

                break;
            case "poisson":
                PoissonDistributionType pdt = bpsim.createPoissonDistributionType();
                pdt.setMean(simulationSet.getMean().getValue());
                processingTime.getParameterValue().add(pdt);

                break;
        }

        elementParameters.setTimeParameters(timeParameters);
        return elementParameters;
    }
}
