/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.bpmn.definition.property.event;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.metaModel.FieldDef;
import org.kie.workbench.common.forms.metaModel.ListBox;
import org.kie.workbench.common.forms.metaModel.SelectorDataProvider;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

import static org.kie.workbench.common.stunner.bpmn.util.FieldLabelConstants.FIELDDEF_TIME_CYCLE;
import static org.kie.workbench.common.stunner.bpmn.util.FieldLabelConstants.FIELDDEF_TIME_CYCLE_LANGUAGE;
import static org.kie.workbench.common.stunner.bpmn.util.FieldLabelConstants.FIELDDEF_TIME_DATE;
import static org.kie.workbench.common.stunner.bpmn.util.FieldLabelConstants.FIELDDEF_TIME_DURATION;

@Portable
@Bindable
@PropertySet
public class IntermediateTimerEventExecutionSet implements BPMNPropertySet {

    @Name
    public static final transient String propertySetName = "Implementation/Execution";

    @Property
    @FieldDef( label = FIELDDEF_TIME_CYCLE, property = "value", position = 1 )
    @Valid
    private TimeCycle timeCycle;

    @Property
    @FieldDef( label = FIELDDEF_TIME_CYCLE_LANGUAGE, property = "value", position = 2 )
    @ListBox
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.REMOTE,
            className = "org.kie.workbench.common.stunner.bpmn.backend.dataproviders.TimeCycleLanguageProvider" )
    @Valid
    protected TimeCycleLanguage timeCycleLanguage;

    @Property
    @FieldDef( label = FIELDDEF_TIME_DATE, property = "value", position = 3 )
    @Valid
    private TimeDate timeDate;

    @Property
    @FieldDef( label = FIELDDEF_TIME_DURATION, property = "value", position = 4 )
    @Valid
    private TimeDuration timeDuration;

    public IntermediateTimerEventExecutionSet() {
        this( new TimeCycle(),
              new TimeCycleLanguage(),
              new TimeDate(),
              new TimeDuration() );
    }

    public IntermediateTimerEventExecutionSet( @MapsTo( "timeCycle" ) TimeCycle timeCycle,
                                               @MapsTo( "timeCycleLanguage" ) TimeCycleLanguage timeCycleLanguage,
                                               @MapsTo( "timeDate" ) TimeDate timeDate,
                                               @MapsTo( "timeDuration" ) TimeDuration timeDuration ) {
        this.timeCycle = timeCycle;
        this.timeCycleLanguage = timeCycleLanguage;
        this.timeDate = timeDate;
        this.timeDuration = timeDuration;
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    public TimeCycle getTimeCycle() {
        return timeCycle;
    }

    public void setTimeCycle( TimeCycle timeCycle ) {
        this.timeCycle = timeCycle;
    }

    public TimeCycleLanguage getTimeCycleLanguage() {
        return timeCycleLanguage;
    }

    public void setTimeCycleLanguage( TimeCycleLanguage timeCycleLanguage ) {
        this.timeCycleLanguage = timeCycleLanguage;
    }

    public TimeDate getTimeDate() {
        return timeDate;
    }

    public void setTimeDate( TimeDate timeDate ) {
        this.timeDate = timeDate;
    }

    public TimeDuration getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration( TimeDuration timeDuration ) {
        this.timeDuration = timeDuration;
    }
}
