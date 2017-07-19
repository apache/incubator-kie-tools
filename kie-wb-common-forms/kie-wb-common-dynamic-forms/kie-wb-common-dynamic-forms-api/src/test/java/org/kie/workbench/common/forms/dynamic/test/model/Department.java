/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.test.model;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

@Bindable
@Portable
public class Department {

    private String name;

    private Address address;

    private List<Employee> employees;

    private MetaAddress metaAddress;

    private MetaAddresses metaAddresses;

    public Department(String name,
                      Address address,
                      List<Employee> employees) {
        this.name = name;
        this.address = address;
        this.employees = employees;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public MetaAddress getMetaAddress() {
        return metaAddress;
    }

    public void setMetaAddress(MetaAddress metaAddress) {
        this.metaAddress = metaAddress;
    }

    public MetaAddresses getMetaAddresses() {
        return metaAddresses;
    }

    public void setMetaAddresses(MetaAddresses metaAddresses) {
        this.metaAddresses = metaAddresses;
    }
}
