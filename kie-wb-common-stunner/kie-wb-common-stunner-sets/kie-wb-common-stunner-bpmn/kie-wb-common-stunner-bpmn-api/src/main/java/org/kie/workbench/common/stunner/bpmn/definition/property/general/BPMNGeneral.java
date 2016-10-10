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

package org.kie.workbench.common.stunner.bpmn.definition.property.general;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.metaModel.FieldDef;
import org.kie.workbench.common.forms.metaModel.TextArea;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

import javax.validation.Valid;

@Portable
@Bindable
@PropertySet
public class BPMNGeneral implements BPMNPropertySet {

    @org.kie.workbench.common.stunner.core.definition.annotation.Name
    public static final transient String propertySetName = "BPMN General";

    @Property
    @FieldDef( label = "Name", property = "value", position = 0 )
    @Valid
    private Name name;

    @Property
    @FieldDef( label = "Documentation", property = "value", position = 1 )
    @TextArea( rows = 3 )
    @Valid
    private Documentation documentation;

    public BPMNGeneral() {
        this( new Name(), new Documentation() );
    }

    public BPMNGeneral( @MapsTo( "name" ) Name name,
                        @MapsTo( "documentation" ) Documentation documentation ) {
        this.name = name;
        this.documentation = documentation;
    }

    public BPMNGeneral( String name,
                        String documentation ) {
        this.name = new Name( name );
        this.documentation = new Documentation( documentation );
    }

    public BPMNGeneral( String name ) {
        this.name = new Name( name );
        this.documentation = new Documentation();
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    public Name getName() {
        return name;
    }

    public Documentation getDocumentation() {
        return documentation;
    }

    public void setName( Name name ) {
        this.name = name;
    }

    public void setDocumentation( Documentation documentation ) {
        this.documentation = documentation;
    }
}
