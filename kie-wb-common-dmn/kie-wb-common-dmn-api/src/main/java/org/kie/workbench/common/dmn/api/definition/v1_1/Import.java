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

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.property.DMNPropertySet;
import org.kie.workbench.common.dmn.api.property.dmn.ImportType;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Namespace;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldLabel;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

@Portable
@Bindable
@PropertySet
@FormDefinition(policy = FieldPolicy.ONLY_MARKED, startElement = "namespace")
public class Import extends DMNModelInstrumentedBase implements DMNPropertySet {

    @Name
    @FieldLabel
    public static final transient String propertySetName = "Import";

    @Property
    @FormField
    protected Namespace namespace;

    @Property
    @FormField(afterElement = "namespace")
    protected LocationURI locationURI;

    @Property
    @FormField(afterElement = "locationURI")
    protected ImportType importType;

    public Import() {
    }

    public Import(final Namespace namespace,
                  final LocationURI locationURI,
                  final ImportType importType) {
        this.namespace = namespace;
        this.locationURI = locationURI;
        this.importType = importType;
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    // -----------------------
    // DMN properties
    // -----------------------

    public Namespace getNamespace() {
        return namespace;
    }

    public void setNamespace(final Namespace namespace) {
        this.namespace = namespace;
    }

    public LocationURI getLocationURI() {
        return locationURI;
    }

    public void setLocationURI(final LocationURI locationURI) {
        this.locationURI = locationURI;
    }

    public ImportType getImportType() {
        return importType;
    }

    public void setImportType(final ImportType importType) {
        this.importType = importType;
    }
}
