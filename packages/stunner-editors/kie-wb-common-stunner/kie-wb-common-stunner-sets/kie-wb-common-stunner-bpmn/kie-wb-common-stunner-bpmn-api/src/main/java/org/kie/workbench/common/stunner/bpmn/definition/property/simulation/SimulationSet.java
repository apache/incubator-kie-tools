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

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@FormDefinition(
        startElement = "timeUnit",
        policy = FieldPolicy.ONLY_MARKED
)
public class SimulationSet implements BPMNPropertySet {

    @Property
    protected Min min;

    @Property
    protected Max max;

    @Property
    protected Mean mean;

    @Property
    @FormField
    @Valid
    protected TimeUnit timeUnit;

    @Property
    @FormField(
            afterElement = "timeUnit"
    )
    protected StandardDeviation standardDeviation;

    @Property
    @FormField(
            afterElement = "standardDeviation"
    )
    protected DistributionType distributionType;

    @Property
    @FormField(
            afterElement = "distributionType"
    )
    protected Quantity quantity;

    @Property
    @FormField(
            afterElement = "quantity"
    )
    protected WorkingHours workingHours;

    @Property
    @FormField(
            afterElement = "workingHours"
    )
    protected UnitCost unitCost;

    @Property
    @FormField(
            afterElement = "unitCost"
    )
    protected Currency currency;

    public SimulationSet() {
        this(new Min(),
             new Max(),
             new Mean(),
             new TimeUnit(),
             new StandardDeviation(),
             new DistributionType(),
             new Quantity(),
             new WorkingHours(),
             new UnitCost(),
             new Currency());
    }

    public SimulationSet(final @MapsTo("min") Min min,
                         final @MapsTo("max") Max max,
                         final @MapsTo("mean") Mean mean,
                         final @MapsTo("timeUnit") TimeUnit timeUnit,
                         final @MapsTo("standardDeviation") StandardDeviation standardDeviation,
                         final @MapsTo("distributionType") DistributionType distributionType,
                         final @MapsTo("quantity") Quantity quantity,
                         final @MapsTo("workingHours") WorkingHours workingHours,
                         final @MapsTo("unitCost") UnitCost unitCost,
                         final @MapsTo("currency") Currency currency) {
        this.min = min;
        this.max = max;
        this.mean = mean;
        this.timeUnit = timeUnit;
        this.standardDeviation = standardDeviation;
        this.distributionType = distributionType;
        this.quantity = quantity;
        this.workingHours = workingHours;
        this.unitCost = unitCost;
        this.currency = currency;
    }

    public Min getMin() {
        return min;
    }

    public void setMin(final Min min) {
        this.min = min;
    }

    public Max getMax() {
        return max;
    }

    public void setMax(final Max max) {
        this.max = max;
    }

    public Mean getMean() {
        return mean;
    }

    public void setMean(final Mean mean) {
        this.mean = mean;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(final TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public StandardDeviation getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(final StandardDeviation standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public DistributionType getDistributionType() {
        return distributionType;
    }

    public void setDistributionType(final DistributionType distributionType) {
        this.distributionType = distributionType;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public void setQuantity(final Quantity quantity) {
        this.quantity = quantity;
    }

    public WorkingHours getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(final WorkingHours workingHours) {
        this.workingHours = workingHours;
    }

    public UnitCost getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(final UnitCost unitCost) {
        this.unitCost = unitCost;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(final Currency currency) {
        this.currency = currency;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(min.hashCode(),
                                         max.hashCode(),
                                         mean.hashCode(),
                                         timeUnit.hashCode(),
                                         standardDeviation.hashCode(),
                                         distributionType.hashCode(),
                                         quantity.hashCode(),
                                         workingHours.hashCode(),
                                         unitCost.hashCode(),
                                         currency.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SimulationSet) {
            SimulationSet other = (SimulationSet) o;
            return min.equals(other.min) &&
                    max.equals(other.max) &&
                    mean.equals(other.mean) &&
                    timeUnit.equals(other.timeUnit) &&
                    standardDeviation.equals(other.standardDeviation) &&
                    distributionType.equals(other.distributionType) &&
                    quantity.equals(other.quantity) &&
                    workingHours.equals(other.workingHours) &&
                    unitCost.equals(other.unitCost) &&
                    currency.equals(other.currency);
        }
        return false;
    }
}
