/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.metaModel.FieldDef;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

import static org.kie.workbench.common.stunner.basicset.util.FieldDefLabelConstants.*;

@Portable
@Bindable
@PropertySet
public class UserTaskExecutionSet implements BPMNPropertySet {

    @Name
    public static final transient String propertySetName = "Implementation/Execution";

    @Property
    @FieldDef( label = FIELDDEF_TASK_NAME, property = "value", position = 1 )
    @Valid
    protected TaskName taskName;

    public UserTaskExecutionSet() {
        this( new TaskName( "" ) );
    }

    public UserTaskExecutionSet( @MapsTo( "taskName" ) TaskName taskName ) {
        this.taskName = taskName;
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    public TaskName getTaskName() {
        return taskName;
    }

    public void setTaskName( TaskName taskName ) {
        this.taskName = taskName;
    }
}
