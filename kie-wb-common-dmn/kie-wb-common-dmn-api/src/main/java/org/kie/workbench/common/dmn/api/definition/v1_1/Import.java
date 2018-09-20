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

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class Import extends DMNModelInstrumentedBase {

    protected String namespace;

    protected LocationURI locationURI;

    protected String importType;

    public Import() {
        this(null,
             new LocationURI(),
             null);
    }

    public Import(final String namespace,
                  final LocationURI locationURI,
                  final String importType) {
        this.namespace = namespace;
        this.locationURI = locationURI;
        this.importType = importType;
    }

    // -----------------------
    // DMN properties
    // -----------------------

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    public LocationURI getLocationURI() {
        return locationURI;
    }

    public void setLocationURI(final LocationURI locationURI) {
        this.locationURI = locationURI;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(final String importType) {
        this.importType = importType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Import)) {
            return false;
        }

        final Import that = (Import) o;

        if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) {
            return false;
        }
        if (locationURI != null ? !locationURI.equals(that.locationURI) : that.locationURI != null) {
            return false;
        }
        return importType != null ? importType.equals(that.importType) : that.importType == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(namespace != null ? namespace.hashCode() : 0,
                                         locationURI != null ? locationURI.hashCode() : 0,
                                         importType != null ? importType.hashCode() : 0);
    }
}
