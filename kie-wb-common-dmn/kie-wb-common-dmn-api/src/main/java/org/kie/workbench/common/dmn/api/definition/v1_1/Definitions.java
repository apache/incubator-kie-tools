/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;

@Portable
public class Definitions extends NamedElement {

    public static final String DEFAULT_EXPRESSION_LANGUAGE = "http://www.omg.org/spec/FEEL/20140401";

    public static final String DEFAULT_TYPE_LANGUAGE = "http://www.omg.org/spec/FEEL/20140401";

    private List<Import> _import;
    private List<ItemDefinition> itemDefinition;
    private List<DRGElement> drgElement;
    private List<Artifact> artifact;
    private List<ElementCollection> elementCollection;
    private List<BusinessContextElement> businessContextElement;
    private String expressionLanguage;
    private String typeLanguage;
    private String namespace;
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
             "",
             "",
             "",
             "",
             "");
    }

    public Definitions(final @MapsTo("id") Id id,
                       final @MapsTo("description") Description description,
                       final @MapsTo("name") Name name,
                       final @MapsTo("_import") List<Import> _import,
                       final @MapsTo("itemDefinition") List<ItemDefinition> itemDefinition,
                       final @MapsTo("drgElement") List<DRGElement> drgElement,
                       final @MapsTo("artifact") List<Artifact> artifact,
                       final @MapsTo("elementCollection") List<ElementCollection> elementCollection,
                       final @MapsTo("businessContextElement") List<BusinessContextElement> businessContextElement,
                       final @MapsTo("expressionLanguage") String expressionLanguage,
                       final @MapsTo("typeLanguage") String typeLanguage,
                       final @MapsTo("namespace") String namespace,
                       final @MapsTo("exporter") String exporter,
                       final @MapsTo("exporterVersion") String exporterVersion) {
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

    public String getExpressionLanguage() {
        if (expressionLanguage == null) {
            return DEFAULT_EXPRESSION_LANGUAGE;
        } else {
            return expressionLanguage;
        }
    }

    public void setExpressionLanguage(final String value) {
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

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(final String value) {
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
}
