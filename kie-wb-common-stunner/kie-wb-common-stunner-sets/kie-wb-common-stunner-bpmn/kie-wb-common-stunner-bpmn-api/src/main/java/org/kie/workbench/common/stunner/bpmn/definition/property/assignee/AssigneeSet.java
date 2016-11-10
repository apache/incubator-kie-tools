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

package org.kie.workbench.common.stunner.bpmn.definition.property.assignee;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.metaModel.FieldDef;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.forms.meta.definition.AssigneeEditor;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeType;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

import javax.validation.Valid;

import static org.kie.workbench.common.stunner.basicset.util.FieldDefLabelConstants.*;

@Portable
@Bindable
@PropertySet
public class AssigneeSet implements BPMNPropertySet {
    @Name
    public static final transient String propertySetName = "Assigned to";

    @Property
    @FieldDef( label = FIELDDEF_ACTORS, property = "value" )
    @AssigneeEditor( type = AssigneeType.USER)
    @Valid
    private Actors actors;

    @Property
    @FieldDef( label = FIELDDEF_GROUPS, property = "value" )
    @AssigneeEditor( type = AssigneeType.GROUP)
    @Valid
    private Groupid groupid;

    public AssigneeSet() {
        this( new Actors(), new Groupid() );
    }

    public AssigneeSet( @MapsTo( "actors" ) Actors actors,
                        @MapsTo( "groupid" ) Groupid groupid ) {
        this.actors = actors;
        this.groupid = groupid;
    }

    public AssigneeSet( String actors,
                        String groupid ) {
        this.actors = new Actors( actors );
        this.groupid = new Groupid( groupid );
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    public Actors getActors() {
        return actors;
    }

    public Groupid getGroupid() {
        return groupid;
    }

    public void setActors( Actors actors ) {
        this.actors = actors;
    }

    public void setGroupid( Groupid groupid ) {
        this.groupid = groupid;
    }

}
