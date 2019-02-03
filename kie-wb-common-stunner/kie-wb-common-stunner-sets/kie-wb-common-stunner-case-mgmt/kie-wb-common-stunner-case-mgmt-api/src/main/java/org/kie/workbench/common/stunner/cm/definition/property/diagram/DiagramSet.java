/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.cm.definition.property.diagram;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.SkipFormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.BaseDiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@PropertySet
@FormDefinition(
        policy = FieldPolicy.ONLY_MARKED,
        startElement = "name"
)
public class DiagramSet implements BaseDiagramSet {

    @Property
    @FormField
    @Valid
    private Name name;

    @Property
    @FormField(
            type = TextAreaFieldType.class,
            afterElement = "name"
    )
    @Valid
    private Documentation documentation;

    @Property
    @FormField(
            afterElement = "documentation"
    )
    @Valid
    private Id id;

    @Property
    @FormField(
            afterElement = "id"
    )
    @Valid
    private Package packageProperty;

    @Property
    @FormField(
            afterElement = "packageProperty"
    )
    @Valid
    private Version version;

    @Property
    @SkipFormField
    @Valid
    private AdHoc adHoc;

    @Property
    @FormField(
            afterElement = "version"
    )
    @Valid
    private ProcessInstanceDescription processInstanceDescription;

    @Property
    private Executable executable;

    public DiagramSet() {
        this(new Name(),
             new Documentation(),
             new Id(),
             new Package(),
             new Version(),
             new AdHoc(true),
             new ProcessInstanceDescription(),
             new Executable());
    }

    public DiagramSet(final @MapsTo("name") Name name,
                      final @MapsTo("documentation") Documentation documentation,
                      final @MapsTo("id") Id id,
                      final @MapsTo("packageProperty") Package packageProperty,
                      final @MapsTo("version") Version version,
                      final @MapsTo("adHoc") AdHoc adHoc,
                      final @MapsTo("processInstanceDescription") ProcessInstanceDescription processInstanceDescription,
                      final @MapsTo("executable") Executable executable) {
        this.name = name;
        this.documentation = documentation;
        this.id = id;
        this.packageProperty = packageProperty;
        this.version = version;
        this.adHoc = adHoc;
        this.processInstanceDescription = processInstanceDescription;
        this.executable = executable;
    }

    public DiagramSet(final String name) {
        this(new Name(name),
             new Documentation(),
             new Id(),
             new Package(),
             new Version(),
             new AdHoc(true),
             new ProcessInstanceDescription(),
             new Executable());
    }

    @Override
    public Name getName() {
        return name;
    }

    public void setName(final Name name) {
        this.name = name;
    }

    @Override
    public Documentation getDocumentation() {
        return documentation;
    }

    public void setDocumentation(final Documentation documentation) {
        this.documentation = documentation;
    }

    @Override
    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    @Override
    public Package getPackageProperty() {
        return packageProperty;
    }

    public void setPackageProperty(final Package packageProperty) {
        this.packageProperty = packageProperty;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    public void setVersion(final Version version) {
        this.version = version;
    }

    @Override
    public AdHoc getAdHoc() {
        return adHoc;
    }

    public void setAdHoc(final AdHoc adHoc) {
        this.adHoc = adHoc;
    }

    @Override
    public ProcessInstanceDescription getProcessInstanceDescription() {
        return processInstanceDescription;
    }

    public void setProcessInstanceDescription(final ProcessInstanceDescription processInstanceDescription) {
        this.processInstanceDescription = processInstanceDescription;
    }

    @Override
    public Executable getExecutable() {
        return executable;
    }

    public void setExecutable(final Executable executable) {
        this.executable = executable;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(name.hashCode(),
                                         documentation.hashCode(),
                                         id.hashCode(),
                                         packageProperty.hashCode(),
                                         version.hashCode(),
                                         adHoc.hashCode(),
                                         processInstanceDescription.hashCode(),
                                         executable.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DiagramSet) {
            DiagramSet other = (DiagramSet) o;
            return name.equals(other.name) &&
                    documentation.equals(other.documentation) &&
                    id.equals(other.id) &&
                    packageProperty.equals(other.packageProperty) &&
                    version.equals(other.version) &&
                    adHoc.equals(other.adHoc) &&
                    processInstanceDescription.equals(other.processInstanceDescription) &&
                    executable.equals(other.executable);
        }
        return false;
    }
}
