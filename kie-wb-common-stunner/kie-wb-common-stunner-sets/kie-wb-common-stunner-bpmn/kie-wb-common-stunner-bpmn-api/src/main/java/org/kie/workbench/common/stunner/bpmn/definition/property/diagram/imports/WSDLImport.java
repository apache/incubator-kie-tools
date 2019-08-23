/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
public class WSDLImport {

    protected static final String DELIMITER = "|";
    protected static final String IDENTIFIER = "wsdl";

    private String location = null;

    private String namespace = null;

    public WSDLImport() {
    }

    public WSDLImport(@MapsTo("location") final String location,
                      @MapsTo("namespace") final String namespace) {
        this.location = location;
        this.namespace = namespace;
    }

    public static Boolean isValidString(String importValue) {
        String[] importParts = splitImportString(importValue);

        if (importParts.length != 3) {
            return false;
        }

        if (!importParts[0].equals(IDENTIFIER)) {
            return false;
        }

        if (importParts[1].isEmpty()) {
            return false;
        }

        if (importParts[2].isEmpty()) {
            return false;
        }

        return true;
    }

    public static WSDLImport fromString(String importValue) throws Exception {
        if (!isValidString(importValue)) {
            throw new Exception("The value: " + importValue + " is not a valid WSDL Import.");
        }

        String[] importParts = splitImportString(importValue);
        WSDLImport wsdlImport = new WSDLImport(importParts[1], importParts[2]);

        return wsdlImport;
    }

    private static String[] splitImportString(String importValue) {
        return importValue.split("\\" + DELIMITER);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WSDLImport) {
            WSDLImport other = (WSDLImport) o;
            return Objects.equals(location, other.location) &&
                    Objects.equals(namespace, other.namespace);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(location),
                                         Objects.hashCode(namespace));
    }

    @Override
    public String toString() {
        if (location == null || namespace == null ||
                location.isEmpty() || namespace.isEmpty()) {
            return "";
        } else {
            return IDENTIFIER + DELIMITER + location + DELIMITER + namespace;
        }
    }
}