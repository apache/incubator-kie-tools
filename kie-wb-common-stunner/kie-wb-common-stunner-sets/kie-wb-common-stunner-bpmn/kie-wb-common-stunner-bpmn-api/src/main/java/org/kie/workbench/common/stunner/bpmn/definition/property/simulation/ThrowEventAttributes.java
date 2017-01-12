/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

@Portable
@Bindable
@PropertySet
public class ThrowEventAttributes implements BPMNPropertySet {

    @Name
    public static final transient String propertySetName = "Throw Event Attributes";

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

    public ThrowEventAttributes() {
        this( new Min(),
              new Max(),
              new Mean(),
              new TimeUnit(),
              new StandardDeviation(),
              new DistributionType() );
    }

    public ThrowEventAttributes( @MapsTo( "min" ) Min min,
                                 @MapsTo( "max" ) Max max,
                                 @MapsTo( "mean" ) Mean mean,
                                 @MapsTo( "timeUnit" ) TimeUnit timeUnit,
                                 @MapsTo( "standardDeviation" ) StandardDeviation standardDeviation,
                                 @MapsTo( "distributionType" ) DistributionType distributionType ) {
        this.min = min;
        this.max = max;
        this.mean = mean;
        this.timeUnit = timeUnit;
        this.standardDeviation = standardDeviation;
        this.distributionType = distributionType;
    }

    public ThrowEventAttributes( Double min,
                                 Double max,
                                 Double mean,
                                 String timeUnit,
                                 Double standardDeviation,
                                 String distributionType ) {
        this.min = new Min( min );
        this.max = new Max( max );
        this.mean = new Mean( mean );
        this.timeUnit = new TimeUnit( timeUnit );
        this.standardDeviation = new StandardDeviation( standardDeviation );
        this.distributionType = new DistributionType( distributionType );
    }

    public String getPropertySetName() {
        return propertySetName;
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

    public void setMin( Min min ) {
        this.min = min;
    }

    public void setMax( Max max ) {
        this.max = max;
    }

    public void setMean( Mean mean ) {
        this.mean = mean;
    }

    public void setTimeUnit( TimeUnit timeUnit ) {
        this.timeUnit = timeUnit;
    }

    public void setStandardDeviation( StandardDeviation standardDeviation ) {
        this.standardDeviation = standardDeviation;
    }

    public void setDistributionType( DistributionType distributionType ) {
        this.distributionType = distributionType;
    }
}
