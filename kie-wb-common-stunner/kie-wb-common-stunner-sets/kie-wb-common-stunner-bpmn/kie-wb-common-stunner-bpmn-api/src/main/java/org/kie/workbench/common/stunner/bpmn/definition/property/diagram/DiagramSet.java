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

package org.kie.workbench.common.stunner.bpmn.definition.property.diagram;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.metaModel.FieldDef;
import org.kie.workbench.common.forms.metaModel.TextArea;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

import javax.validation.Valid;

import static org.kie.workbench.common.stunner.bpmn.util.FieldLabelConstants.*;

@Portable
@Bindable
@PropertySet
public class DiagramSet {

    @org.kie.workbench.common.stunner.core.definition.annotation.Name
    public static final transient String propertySetName = "BPMN Process";

    @Property
    @FieldDef(label = FIELDDEF_NAME, property = "value", position = 0)
    @Valid
    private Name name;

    @Property
    @FieldDef(label = FIELDDEF_DOCUMENTATION, property = "value", position = 1)
    @TextArea(rows = 3)
    @Valid
    private Documentation documentation;

    @Property
    @FieldDef( label = FIELDDEF_ID, property = "value", position = 2)
    @Valid
    private Id id;

    @Property
    @FieldDef( label = FIELDDEF_PACKAGE, property = "value", position = 3 )
    @Valid
    private Package packageProperty;

    @Property
    @FieldDef( label = FIELDDEF_VERSION, property = "value", position = 4 )
    @Valid
    private Version version;

    @Property
    private Executable executable;

    public DiagramSet() {
        this( new Name(""), new Documentation(),
                new Id(""), new Package(""),
                new Version(  Version.defaultValue ), new Executable() );
    }

    public DiagramSet( @MapsTo( "name" ) Name name,
                       @MapsTo( "documentation" ) Documentation documentation,
                       @MapsTo( "id" ) Id id,
                       @MapsTo( "packageProperty" ) Package packageProperty,
                       @MapsTo( "version" ) Version version,
                       @MapsTo( "executable" ) Executable executable ) {
        this.name = name;
        this.documentation = documentation;
        this.id = id;
        this.packageProperty = packageProperty;
        this.version = version;
        this.executable = executable;
    }

    public DiagramSet( String name ) {
        this( new Name( name ), new Documentation(),
                new Id( "" ), new Package( "" ),
                new Version(  Version.defaultValue ), new Executable() );
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

    public void setName(Name name) {
        this.name = name;
    }

    public void setDocumentation(Documentation documentation) {
        this.documentation = documentation;
    }

    public void setId( Id id ) {
        this.id = id;
    }

    public void setPackageProperty( Package packageProperty ) {
        this.packageProperty = packageProperty;
    }

    public void setVersion( Version version ) { this.version = version; }

    public void setExecutable( Executable executable ) {
        this.executable = executable;
    }
}
