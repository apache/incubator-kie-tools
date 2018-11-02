/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.property.DMNPropertySet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@PropertySet
@FormDefinition(policy = FieldPolicy.ONLY_MARKED, startElement = "id")
public class Definitions extends NamedElement implements HasName,
                                                         DMNPropertySet {

    public static final String DEFAULT_EXPRESSION_LANGUAGE = Namespace.FEEL.getUri();

    public static final String DEFAULT_TYPE_LANGUAGE = Namespace.FEEL.getUri();

    private List<Import> _import;
    private List<ItemDefinition> itemDefinition;
    private List<DRGElement> drgElement;
    private List<Artifact> artifact;
    private List<ElementCollection> elementCollection;
    private List<BusinessContextElement> businessContextElement;

    @Property
    @FormField(afterElement = "name")
    protected ExpressionLanguage expressionLanguage;

    private String typeLanguage;

    @Property
    @FormField(afterElement = "expressionLanguage")
    private Text namespace;

    private String exporter;
    private String exporterVersion;

    public Definitions() {
        this(new Id(),
             new Description(),
             new Name(),
             new ArrayList<>(),
             new ArrayList<>(),
             new ArrayList<>(),
             new ArrayList<>(),
             new ArrayList<>(),
             new ArrayList<>(),
             new ExpressionLanguage(),
             null,
             new Text(),
             null,
             null);
    }

    public Definitions(final Id id,
                       final Description description,
                       final Name name,
                       final List<Import> _import,
                       final List<ItemDefinition> itemDefinition,
                       final List<DRGElement> drgElement,
                       final List<Artifact> artifact,
                       final List<ElementCollection> elementCollection,
                       final List<BusinessContextElement> businessContextElement,
                       final ExpressionLanguage expressionLanguage,
                       final String typeLanguage,
                       final Text namespace,
                       final String exporter,
                       final String exporterVersion) {
        super(id,
              description,
              name);
        this._import = _import;
        this.itemDefinition = itemDefinition;
        this.drgElement = drgElement;
        this.artifact = artifact;
        this.elementCollection = elementCollection;
        this.businessContextElement = businessContextElement;
        this.expressionLanguage = expressionLanguage;
        this.typeLanguage = typeLanguage;
        this.namespace = namespace;
        this.exporter = exporter;
        this.exporterVersion = exporterVersion;
    }

    // -----------------------
    // DMN properties
    // -----------------------

    public List<Import> getImport() {
        if (_import == null) {
            _import = new ArrayList<>();
        }
        return this._import;
    }

    public List<ItemDefinition> getItemDefinition() {
        if (itemDefinition == null) {
            itemDefinition = new ArrayList<>();
        }
        return this.itemDefinition;
    }

    public List<DRGElement> getDrgElement() {
        if (drgElement == null) {
            drgElement = new ArrayList<>();
        }
        return this.drgElement;
    }

    public List<Artifact> getArtifact() {
        if (artifact == null) {
            artifact = new ArrayList<Artifact>();
        }
        return this.artifact;
    }

    public List<ElementCollection> getElementCollection() {
        if (elementCollection == null) {
            elementCollection = new ArrayList<>();
        }
        return this.elementCollection;
    }

    public List<BusinessContextElement> getBusinessContextElement() {
        if (businessContextElement == null) {
            businessContextElement = new ArrayList<>();
        }
        return this.businessContextElement;
    }

    public ExpressionLanguage getExpressionLanguage() {
        if (expressionLanguage == null) {
            return new ExpressionLanguage(DEFAULT_EXPRESSION_LANGUAGE);
        } else {
            return expressionLanguage;
        }
    }

    public void setExpressionLanguage(final ExpressionLanguage value) {
        this.expressionLanguage = value;
    }

    public String getTypeLanguage() {
        if (typeLanguage == null) {
            return DEFAULT_TYPE_LANGUAGE;
        } else {
            return typeLanguage;
        }
    }

    public void setTypeLanguage(final String value) {
        this.typeLanguage = value;
    }

    public Text getNamespace() {
        return namespace;
    }

    public void setNamespace(final Text value) {
        this.namespace = value;
    }

    public String getExporter() {
        return exporter;
    }

    public void setExporter(final String value) {
        this.exporter = value;
    }

    public String getExporterVersion() {
        return exporterVersion;
    }

    public void setExporterVersion(final String value) {
        this.exporterVersion = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Definitions)) {
            return false;
        }

        final Definitions that = (Definitions) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (_import != null ? !_import.equals(that._import) : that._import != null) {
            return false;
        }
        if (itemDefinition != null ? !itemDefinition.equals(that.itemDefinition) : that.itemDefinition != null) {
            return false;
        }
        if (drgElement != null ? !drgElement.equals(that.drgElement) : that.drgElement != null) {
            return false;
        }
        if (artifact != null ? !artifact.equals(that.artifact) : that.artifact != null) {
            return false;
        }
        if (elementCollection != null ? !elementCollection.equals(that.elementCollection) : that.elementCollection != null) {
            return false;
        }
        if (businessContextElement != null ? !businessContextElement.equals(that.businessContextElement) : that.businessContextElement != null) {
            return false;
        }
        if (expressionLanguage != null ? !expressionLanguage.equals(that.expressionLanguage) : that.expressionLanguage != null) {
            return false;
        }
        if (typeLanguage != null ? !typeLanguage.equals(that.typeLanguage) : that.typeLanguage != null) {
            return false;
        }
        if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) {
            return false;
        }
        if (exporter != null ? !exporter.equals(that.exporter) : that.exporter != null) {
            return false;
        }
        return exporterVersion != null ? exporterVersion.equals(that.exporterVersion) : that.exporterVersion == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         name != null ? name.hashCode() : 0,
                                         _import != null ? _import.hashCode() : 0,
                                         itemDefinition != null ? itemDefinition.hashCode() : 0,
                                         drgElement != null ? drgElement.hashCode() : 0,
                                         artifact != null ? artifact.hashCode() : 0,
                                         elementCollection != null ? elementCollection.hashCode() : 0,
                                         businessContextElement != null ? businessContextElement.hashCode() : 0,
                                         expressionLanguage != null ? expressionLanguage.hashCode() : 0,
                                         typeLanguage != null ? typeLanguage.hashCode() : 0,
                                         namespace != null ? namespace.hashCode() : 0,
                                         exporter != null ? exporter.hashCode() : 0,
                                         exporterVersion != null ? exporterVersion.hashCode() : 0);
    }
}
