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

package org.kie.workbench.common.stunner.bpmn.definition.property.diagram;

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
public class DiagramSet implements BPMNPropertySet {

    @Name
    public static final transient String propertySetName = "BPMN Diagram";

    @Property
    @FieldDef( label = FIELDDEF_ID, property = "value" )
    @Valid
    private Id id;

    @Property
    @FieldDef( label = FIELDDEF_PACKAGE, property = "value" )
    @Valid
    private Package packageProperty;

    @Property
    @FieldDef( label = FIELDDEF_VERSION, property = "value" )
    @Valid
    private Version version;

    @Property
    private Executable executable;

    public DiagramSet() {
        this( new Id(), new Package(),
                new Version(), new Executable() );
    }

    public DiagramSet( @MapsTo( "id" ) Id id,
                       @MapsTo( "packageProperty" ) Package packageProperty,
                       @MapsTo( "version" ) Version version,
                       @MapsTo( "executable" ) Executable executable ) {
        this.id = id;
        this.packageProperty = packageProperty;
        this.version = version;
        this.executable = executable;
    }

    public DiagramSet( String id,
                       String packageProperty,
                       String version,
                       Boolean executable ) {
        this.id = new Id( id );
        this.packageProperty = new Package( packageProperty );
        this.version = new Version( version );
        this.executable = new Executable( executable );
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    public Id getId() {
        return id;
    }

    public Package getPackageProperty() {
        return packageProperty;
    }

    public Version getVersion() {
        return version;
    }

    public Executable getExecutable() {
        return executable;
    }

    public void setId( Id id ) {
        this.id = id;
    }

    public void setPackageProperty( Package packageProperty ) {
        this.packageProperty = packageProperty;
    }

    public void setVersion( Version version ) {
        this.version = version;
    }

    public void setExecutable( Executable executable ) {
        this.executable = executable;
    }
}
