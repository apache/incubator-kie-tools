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

package org.kie.workbench.common.stunner.bpmn.definition.property.service;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
public class GenericServiceTaskValue {

    private static final String JAVA = "Java"; // Default value

    private String serviceImplementation = JAVA;

    private String serviceInterface = "";

    private String serviceOperation = "";

    public GenericServiceTaskValue() {

    }

    public GenericServiceTaskValue(@MapsTo("serviceImplementation") final String serviceImplementation,
                                   @MapsTo("serviceInterface") final String serviceInterface,
                                   @MapsTo("serviceOperation") final String serviceOperation) {
        this.serviceImplementation = serviceImplementation;
        this.serviceInterface = serviceInterface;
        this.serviceOperation = serviceOperation;
    }

    public String getServiceImplementation() {
        return serviceImplementation;
    }

    public void setServiceImplementation(String serviceImplementation) {
        this.serviceImplementation = serviceImplementation;
    }

    public String getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public String getServiceOperation() {
        return serviceOperation;
    }

    public void setServiceOperation(String serviceOperation) {
        this.serviceOperation = serviceOperation;
    }

    @Override
    public String toString() {
        return "GenericServiceTaskValue{" +
                "serviceImplementation='" + serviceImplementation + '\'' +
                ", serviceInterface='" + serviceInterface + '\'' +
                ", serviceOperation='" + serviceOperation + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(serviceImplementation),
                                         Objects.hashCode(serviceInterface),
                                         Objects.hashCode(serviceOperation));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GenericServiceTaskValue) {
            GenericServiceTaskValue other = (GenericServiceTaskValue) o;
            return Objects.equals(serviceImplementation, other.serviceImplementation) &&
                    Objects.equals(serviceInterface, other.serviceInterface) &&
                    Objects.equals(serviceOperation, other.serviceOperation);
        }
        return false;
    }
}
