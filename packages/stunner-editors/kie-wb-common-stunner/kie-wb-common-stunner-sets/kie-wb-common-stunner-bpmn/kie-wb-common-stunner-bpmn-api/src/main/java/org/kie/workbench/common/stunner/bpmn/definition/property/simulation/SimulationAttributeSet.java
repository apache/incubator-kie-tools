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


package org.kie.workbench.common.stunner.bpmn.definition.property.simulation;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
public class SimulationAttributeSet implements BPMNPropertySet {

    @Property
    private Min min;

    @Property
    private Max max;

    @Property
    private Mean mean;

    @Property
    private TimeUnit timeUnit;

    @Property
    private StandardDeviation standardDeviation;

    @Property
    private DistributionType distributionType;

    public SimulationAttributeSet() {
        this(new Min(),
             new Max(),
             new Mean(),
             new TimeUnit(),
             new StandardDeviation(),
             new DistributionType());
    }

    public SimulationAttributeSet(final @MapsTo("min") Min min,
                                  final @MapsTo("max") Max max,
                                  final @MapsTo("mean") Mean mean,
                                  final @MapsTo("timeUnit") TimeUnit timeUnit,
                                  final @MapsTo("standardDeviation") StandardDeviation standardDeviation,
                                  final @MapsTo("distributionType") DistributionType distributionType) {
        this.min = min;
        this.max = max;
        this.mean = mean;
        this.timeUnit = timeUnit;
        this.standardDeviation = standardDeviation;
        this.distributionType = distributionType;
    }

    public SimulationAttributeSet(final Double min,
                                  final Double max,
                                  final Double mean,
                                  final String timeUnit,
                                  final Double standardDeviation,
                                  final String distributionType) {
        this.min = new Min(min);
        this.max = new Max(max);
        this.mean = new Mean(mean);
        this.timeUnit = new TimeUnit(timeUnit);
        this.standardDeviation = new StandardDeviation(standardDeviation);
        this.distributionType = new DistributionType(distributionType);
    }

    public Min getMin() {
        return min;
    }

    public Max getMax() {
        return max;
    }

    public Mean getMean() {
        return mean;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public StandardDeviation getStandardDeviation() {
        return standardDeviation;
    }

    public DistributionType getDistributionType() {
        return distributionType;
    }

    public void setMin(final Min min) {
        this.min = min;
    }

    public void setMax(final Max max) {
        this.max = max;
    }

    public void setMean(final Mean mean) {
        this.mean = mean;
    }

    public void setTimeUnit(final TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public void setStandardDeviation(final StandardDeviation standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public void setDistributionType(final DistributionType distributionType) {
        this.distributionType = distributionType;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(min.hashCode(),
                                         max.hashCode(),
                                         mean.hashCode(),
                                         timeUnit.hashCode(),
                                         standardDeviation.hashCode(),
                                         distributionType.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SimulationAttributeSet) {
            SimulationAttributeSet other = (SimulationAttributeSet) o;
            return min.equals(other.min) &&
                    max.equals(other.max) &&
                    mean.equals(other.mean) &&
                    timeUnit.equals(other.timeUnit) &&
                    standardDeviation.equals(other.standardDeviation) &&
                    distributionType.equals(other.distributionType);
        }
        return false;
    }
}
