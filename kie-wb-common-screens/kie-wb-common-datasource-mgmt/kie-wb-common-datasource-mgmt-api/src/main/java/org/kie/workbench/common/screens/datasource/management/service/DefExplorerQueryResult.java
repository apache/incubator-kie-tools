/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datasource.management.service;

import java.util.ArrayList;
import java.util.Collection;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDefInfo;
import org.kie.workbench.common.screens.datasource.management.model.DriverDefInfo;

@Portable
public class DefExplorerQueryResult {

    private Collection<OrganizationalUnit> organizationalUnits = new ArrayList<>();

    private Collection<Repository> repositories = new ArrayList<>();

    private Collection<Module> modules = new ArrayList<>();

    private Collection<DataSourceDefInfo> dataSourceDefs = new ArrayList<>();

    private Collection<DriverDefInfo> driverDefs = new ArrayList<>();

    public DefExplorerQueryResult() {
    }

    public Collection<OrganizationalUnit> getOrganizationalUnits() {
        return organizationalUnits;
    }

    public void setOrganizationalUnits(Collection<OrganizationalUnit> organizationalUnits) {
        this.organizationalUnits = organizationalUnits;
    }

    public Collection<Repository> getRepositories() {
        return repositories;
    }

    public void setRepositories(Collection<Repository> repositories) {
        this.repositories = repositories;
    }

    public Collection<Module> getModules() {
        return modules;
    }

    public void setModules(final Collection<Module> modules) {
        this.modules = modules;
    }

    public Collection<DataSourceDefInfo> getDataSourceDefs() {
        return dataSourceDefs;
    }

    public void setDataSourceDefs(Collection<DataSourceDefInfo> dataSourceDefs) {
        this.dataSourceDefs = dataSourceDefs;
    }

    public Collection<DriverDefInfo> getDriverDefs() {
        return driverDefs;
    }

    public void setDriverDefs(Collection<DriverDefInfo> driverDefs) {
        this.driverDefs = driverDefs;
    }
}
