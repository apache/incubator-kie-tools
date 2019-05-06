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

import java.util.Objects;

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
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.GlobalVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.forms.model.VariablesEditorFieldType;
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
    @FormField(
            type = VariablesEditorFieldType.class
    )
    @Valid
    private GlobalVariables globalVariables;

    @Property
    @FormField(
            afterElement = "globalVariables"
    )
    private Executable executable;

    @Property
    @FormField(afterElement = "executable")
    @Valid
    private SLADueDate slaDueDate;

    public DiagramSet() {
        this(new Name(),
             new Documentation(),
             new Id(),
             new Package(),
             new Version(),
             new AdHoc(true),
             new ProcessInstanceDescription(),
             new GlobalVariables(),
             new Executable(),
             new SLADueDate());
    }

    public DiagramSet(final @MapsTo("name") Name name,
                      final @MapsTo("documentation") Documentation documentation,
                      final @MapsTo("id") Id id,
                      final @MapsTo("packageProperty") Package packageProperty,
                      final @MapsTo("version") Version version,
                      final @MapsTo("adHoc") AdHoc adHoc,
                      final @MapsTo("processInstanceDescription") ProcessInstanceDescription processInstanceDescription,
                      final @MapsTo("globalVariables") GlobalVariables globalVariables,
                      final @MapsTo("executable") Executable executable,
                      final @MapsTo("slaDueDate") SLADueDate slaDueDate) {
        this.name = name;
        this.documentation = documentation;
        this.id = id;
        this.packageProperty = packageProperty;
        this.version = version;
        this.adHoc = adHoc;
        this.processInstanceDescription = processInstanceDescription;
        this.globalVariables = globalVariables;
        this.executable = executable;
        this.slaDueDate = slaDueDate;
    }

    public DiagramSet(final String name) {
        this(new Name(name),
             new Documentation(),
             new Id(),
             new Package(),
             new Version(),
             new AdHoc(true),
             new ProcessInstanceDescription(),
             new GlobalVariables(),
             new Executable(),
             new SLADueDate());
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
    public GlobalVariables getGlobalVariables() {
        return globalVariables;
    }

    public void setGlobalVariables(GlobalVariables globalVariables) {
        this.globalVariables = globalVariables;
    }

    @Override
    public Executable getExecutable() {
        return executable;
    }

    public void setExecutable(final Executable executable) {
        this.executable = executable;
    }

    @Override
    public SLADueDate getSlaDueDate() {
        return slaDueDate;
    }

    public void setSlaDueDate(final SLADueDate slaDueDate) { this.slaDueDate = slaDueDate; }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(name),
                                         Objects.hashCode(documentation),
                                         Objects.hashCode(id),
                                         Objects.hashCode(packageProperty),
                                         Objects.hashCode(version),
                                         Objects.hashCode(adHoc),
                                         Objects.hashCode(processInstanceDescription),
                                         Objects.hashCode(globalVariables),
                                         Objects.hashCode(executable),
                                         Objects.hashCode(slaDueDate));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DiagramSet) {
            DiagramSet other = (DiagramSet) o;
            return Objects.equals(name, other.name) &&
                    Objects.equals(documentation, other.documentation) &&
                    Objects.equals(id, other.id) &&
                    Objects.equals(packageProperty, other.packageProperty) &&
                    Objects.equals(version, other.version) &&
                    Objects.equals(adHoc, other.adHoc) &&
                    Objects.equals(processInstanceDescription, other.processInstanceDescription) &&
                    Objects.equals(globalVariables, other.globalVariables) &&
                    Objects.equals(executable, other.executable) &&
                    Objects.equals(slaDueDate, other.slaDueDate);
        }
        return false;
    }
}
