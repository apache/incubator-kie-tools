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


package org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
public class ImportsValue {

    private List<DefaultImport> defaultImports = new ArrayList<>();
    private List<WSDLImport> wsdlImports = new ArrayList<>();

    public ImportsValue() {
    }

    public ImportsValue(@MapsTo("defaultImports") final List<DefaultImport> defaultImports,
                        @MapsTo("wsdlImports") final List<WSDLImport> wsdlImports) {
        this.defaultImports = defaultImports;
        this.wsdlImports = wsdlImports;
    }

    public List<DefaultImport> getDefaultImports() {
        return defaultImports;
    }

    public void setDefaultImports(final List<DefaultImport> defaultImports) {
        this.defaultImports = defaultImports;
    }

    public List<WSDLImport> getWSDLImports() {
        return wsdlImports;
    }

    public void setWSDLImports(final List<WSDLImport> wsdlImports) {
        this.wsdlImports = wsdlImports;
    }

    public void addImport(final DefaultImport defaultImport) {
        defaultImports.add(defaultImport);
    }

    public void addImport(final WSDLImport wsdlImport) {
        wsdlImports.add(wsdlImport);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ImportsValue) {
            ImportsValue other = (ImportsValue) o;
            return Objects.equals(defaultImports, other.defaultImports) &&
                    Objects.equals(wsdlImports, other.wsdlImports);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(defaultImports),
                                         Objects.hashCode(wsdlImports));
    }

    @Override
    public String toString() {
        final Collection<String> defaultImports = getDefaultImports().stream()
                .map(DefaultImport::toString)
                .collect(Collectors.toList());

        final Collection<String> wsdlImports = getWSDLImports().stream()
                .map(WSDLImport::toString)
                .collect(Collectors.toList());

        final String serializedValue = Stream.of(defaultImports, wsdlImports)
                .flatMap(Collection::stream)
                .collect(Collectors.joining(","));

        return serializedValue;
    }
}