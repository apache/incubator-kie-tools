/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.bpmn.definition.property.simulation;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.metaModel.FieldDef;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

import javax.validation.Valid;

import static org.kie.workbench.common.stunner.basicset.util.FieldDefLabelConstants.*;

@Portable
@Bindable
@PropertySet
public class SimulationSet implements BPMNPropertySet {
    @Name
    public static final transient String propertySetName = "Process Simulation";

    @Property
    protected Min min;

    @Property
    protected Max max;

    @Property
    protected Mean mean;

    @Property
    @FieldDef( label = FIELDDEF_TIME_UNIT, property = "value" )
    @Valid
    protected TimeUnit timeUnit;

    @Property
    @FieldDef( label = FIELDDEF_STANDARD_DEVIATION, property = "value" )
    protected StandardDeviation standardDeviation;

    @Property
    @FieldDef( label = FIELDDEF_DISTRIBUTION_TYPE, property = "value" )
    protected DistributionType distributionType;

    @Property
    @FieldDef( label = FIELDDEF_QUANTITY, property = "value" )
    protected Quantity quantity;

    @Property
    @FieldDef( label = FIELDDEF_WORKINGHOURS, property = "value" )
    protected WorkingHours workingHours;

    @Property
    @FieldDef( label = FIELDDEF_UNITCOST, property = "value" )
    protected UnitCost unitCost;

    @Property
    @FieldDef( label = FIELDDEF_CURRENCY, property = "value" )
    protected Currency currency;

    public SimulationSet() {
        this( new Min(),
                new Max(),
                new Mean(),
                new TimeUnit(),
                new StandardDeviation(),
                new DistributionType(),
                new Quantity(),
                new WorkingHours(),
                new UnitCost(),
                new Currency() );
    }

    public SimulationSet( @MapsTo( "min" ) Min min,
                          @MapsTo( "max" ) Max max,
                          @MapsTo( "mean" ) Mean mean,
                          @MapsTo( "timeUnit" ) TimeUnit timeUnit,
                          @MapsTo( "standardDeviation" ) StandardDeviation standardDeviation,
                          @MapsTo( "distributionType" ) DistributionType distributionType,
                          @MapsTo( "quantity" ) Quantity quantity,
                          @MapsTo( "workingHours" ) WorkingHours workingHours,
                          @MapsTo( "unitCost" ) UnitCost unitCost,
                          @MapsTo( "currency" ) Currency currency ) {
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

    public void setMin( Min min ) {
        this.min = min;
    }

    public Max getMax() {
        return max;
    }

    public void setMax( Max max ) {
        this.max = max;
    }

    public Mean getMean() {
        return mean;
    }

    public void setMean( Mean mean ) {
        this.mean = mean;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit( TimeUnit timeUnit ) {
        this.timeUnit = timeUnit;
    }

    public StandardDeviation getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation( StandardDeviation standardDeviation ) {
        this.standardDeviation = standardDeviation;
    }

    public DistributionType getDistributionType() {
        return distributionType;
    }

    public void setDistributionType( DistributionType distributionType ) {
        this.distributionType = distributionType;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public void setQuantity( Quantity quantity ) {
        this.quantity = quantity;
    }

    public WorkingHours getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours( WorkingHours workingHours ) {
        this.workingHours = workingHours;
    }

    public UnitCost getUnitCost() {
        return unitCost;
    }

    public void setUnitCost( UnitCost unitCost ) {
        this.unitCost = unitCost;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency( Currency currency ) {
        this.currency = currency;
    }
}
