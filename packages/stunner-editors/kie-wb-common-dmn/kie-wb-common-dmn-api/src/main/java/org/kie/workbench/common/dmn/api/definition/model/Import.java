/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */
package org.kie.workbench.common.dmn.api.definition.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.uberfire.commons.UUID.uuid;

@Portable
public class Import extends NamedElement {

    protected String namespace;

    protected LocationURI locationURI;

    protected String importType;

    protected String uuid;

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
        this.uuid = uuid();
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
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

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (nameHolder != null ? !nameHolder.equals(that.nameHolder) : that.nameHolder != null) {
            return false;
        }
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
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         nameHolder != null ? nameHolder.hashCode() : 0,
                                         namespace != null ? namespace.hashCode() : 0,
                                         locationURI != null ? locationURI.hashCode() : 0,
                                         importType != null ? importType.hashCode() : 0);
    }
}
