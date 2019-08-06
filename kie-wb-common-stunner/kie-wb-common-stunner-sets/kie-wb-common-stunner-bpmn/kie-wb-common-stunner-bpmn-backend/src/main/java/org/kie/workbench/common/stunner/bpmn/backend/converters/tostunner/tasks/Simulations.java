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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.tasks;

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
import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.backend.converters.VoidMatch;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationAttributeSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;

public class Simulations {

    public static SimulationSet simulationSet(ElementParameters eleType) {
        SimulationSet simulationSet = new SimulationSet();

        TimeParameters timeParams = eleType.getTimeParameters();
        if (timeParams == null) {
            return simulationSet;
        }
        Parameter processingTime = timeParams.getProcessingTime();
        ParameterValue paramValue = processingTime.getParameterValue().get(0);

        VoidMatch.of(ParameterValue.class)
                .when(NormalDistributionType.class, ndt -> {
                    simulationSet.getMean().setValue(ndt.getMean());
                    simulationSet.getStandardDeviation().setValue(ndt.getStandardDeviation());
                    simulationSet.getDistributionType().setValue("normal");
                })
                .when(UniformDistributionType.class, udt -> {
                    simulationSet.getMin().setValue(udt.getMin());
                    simulationSet.getMax().setValue(udt.getMax());
                    simulationSet.getDistributionType().setValue("uniform");
                })
                .when(PoissonDistributionType.class, pdt -> {
                    simulationSet.getMean().setValue(pdt.getMean());
                    simulationSet.getDistributionType().setValue("poisson");
                }).apply(paramValue).asSuccess().value();

        CostParameters costParams = eleType.getCostParameters();
        if (costParams != null) {
            simulationSet.getUnitCost().setValue(extractDouble(costParams.getUnitCost()));
        }

        //controlParams(eleType, simulationSet);
        ResourceParameters resourceParams = eleType.getResourceParameters();

        if (resourceParams != null) {
            Double quantity = extractDouble(resourceParams.getQuantity());
            simulationSet.getQuantity().setValue(quantity);

            Double availability = extractDouble(resourceParams.getAvailability());
            simulationSet.getWorkingHours().setValue(availability);
        }

        return simulationSet;
    }

    public static SimulationAttributeSet simulationAttributeSet(ElementParameters eleType) {
        SimulationAttributeSet simulationSet = new SimulationAttributeSet();

        TimeParameters timeParams = eleType.getTimeParameters();
        if (timeParams == null) {
            return simulationSet;
        }
        Parameter processingTime = timeParams.getProcessingTime();
        ParameterValue paramValue = processingTime.getParameterValue().get(0);

        return Match.of(ParameterValue.class, SimulationAttributeSet.class)
                .when(NormalDistributionType.class, ndt -> {
                    simulationSet.getMean().setValue(ndt.getMean());
                    simulationSet.getStandardDeviation().setValue(ndt.getStandardDeviation());
                    simulationSet.getDistributionType().setValue("normal");
                    return simulationSet;
                })
                .when(UniformDistributionType.class, udt -> {
                    simulationSet.getMin().setValue(udt.getMin());
                    simulationSet.getMax().setValue(udt.getMax());
                    simulationSet.getDistributionType().setValue("uniform");
                    return simulationSet;
                })
                .when(PoissonDistributionType.class, pdt -> {
                    simulationSet.getMean().setValue(pdt.getMean());
                    simulationSet.getDistributionType().setValue("poisson");
                    return simulationSet;
                })
                .apply(paramValue)
                .asSuccess()
                .value();
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
