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

import bpsim.CostParameters;
import bpsim.ElementParameters;
import bpsim.FloatingParameterType;
import bpsim.NormalDistributionType;
import bpsim.Parameter;
import bpsim.ParameterValue;
import bpsim.PoissonDistributionType;
import bpsim.ResourceParameters;
import bpsim.TimeParameters;
import bpsim.UniformDistributionType;
import org.eclipse.emf.common.util.EList;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Match;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpsim;

public class SimulationSets {

    public static ElementParameters toElementParameters(SimulationSet simulationSet) {
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

        Double unitCost = simulationSet.getUnitCost().getValue();
        Double quantity = simulationSet.getQuantity().getValue();
        Double workingHours = simulationSet.getWorkingHours().getValue();

        CostParameters costParameters = bpsim.createCostParameters();
        costParameters.setUnitCost(toParameter(unitCost));
        elementParameters.setCostParameters(costParameters);

        ResourceParameters resourceParameters = bpsim.createResourceParameters();
        resourceParameters.setQuantity(toParameter(quantity));
        resourceParameters.setAvailability(toParameter(workingHours));
        elementParameters.setResourceParameters(resourceParameters);

        return elementParameters;
    }

    private static Parameter toParameter(Double value) {
        Parameter parameter = bpsim.createParameter();
        FloatingParameterType parameterValue = bpsim.createFloatingParameterType();
        parameterValue.setValue(value);
        parameter.getParameterValue().add(parameterValue);
        return parameter;
    }

    public static SimulationSet of(ElementParameters eleType) {

        TimeParameters timeParams = eleType.getTimeParameters();
        if (timeParams == null) {
            return new SimulationSet();
        }
        Parameter processingTime = timeParams.getProcessingTime();
        ParameterValue paramValue = processingTime.getParameterValue().get(0);

        SimulationSet simulationSet = Match.<ParameterValue, SimulationSet>of()
                .<NormalDistributionType>when(e -> e instanceof NormalDistributionType, ndt -> {
                    SimulationSet sset = new SimulationSet();
                    sset.getMean().setValue(ndt.getMean());
                    sset.getStandardDeviation().setValue(ndt.getStandardDeviation());
                    sset.getDistributionType().setValue("normal");
                    return sset;
                })
                .<UniformDistributionType>when(e -> e instanceof UniformDistributionType, udt -> {
                    SimulationSet sset = new SimulationSet();
                    sset.getMin().setValue(udt.getMin());
                    sset.getMax().setValue(udt.getMax());
                    sset.getDistributionType().setValue("uniform");
                    return sset;
                })
                .<PoissonDistributionType>when(e -> e instanceof PoissonDistributionType, pdt -> {
                    SimulationSet sset = new SimulationSet();
                    sset.getMean().setValue(pdt.getMean());
                    sset.getDistributionType().setValue("poisson");
                    return sset;
                }).apply(paramValue)
                .asSuccess()
                .value();

        CostParameters costParams = eleType.getCostParameters();
        if (costParams != null) {
            simulationSet.getUnitCost().setValue(extractDouble(costParams.getUnitCost()));
        }

        ResourceParameters resourceParams = eleType.getResourceParameters();

        if (resourceParams != null) {
            Double quantity = extractDouble(resourceParams.getQuantity());
            simulationSet.getQuantity().setValue(quantity);

            Double availability = extractDouble(resourceParams.getAvailability());
            simulationSet.getWorkingHours().setValue(availability);
        }

        return simulationSet;
    }

    private static Double extractDouble(Parameter parameter) {
        if (parameter == null) {
            return 0.0;
        }
        return extractDouble(parameter.getParameterValue());
    }

    private static Double extractDouble(EList<ParameterValue> parameterValues) {
        if (parameterValues.isEmpty()) {
            throw new IllegalArgumentException("failure params");
        }
        ParameterValue value = parameterValues.get(0);
        FloatingParameterType floatingValue = (FloatingParameterType) value;
        return floatingValue.getValue();
    }
}
