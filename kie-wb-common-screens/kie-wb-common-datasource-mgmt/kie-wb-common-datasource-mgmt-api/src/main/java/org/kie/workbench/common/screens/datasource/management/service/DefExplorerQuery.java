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

import org.guvnor.common.services.project.model.Module;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DefExplorerQuery {

    private Repository repository;

    private OrganizationalUnit organizationalUnit;

    private Module module;

    private String branchName;

    private boolean globalQuery = false;

    public DefExplorerQuery() {
    }

    public DefExplorerQuery(final OrganizationalUnit organizationalUnit,
                            final Repository repository,
                            final Module module,
                            final String branchName) {
        this.organizationalUnit = organizationalUnit;
        this.repository = repository;
        this.module = module;
        this.branchName = branchName;
    }

    public DefExplorerQuery(boolean globalQuery) {
        this.globalQuery = globalQuery;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public OrganizationalUnit getOrganizationalUnit() {
        return organizationalUnit;
    }

    public void setOrganizationalUnit(OrganizationalUnit organizationalUnit) {
        this.organizationalUnit = organizationalUnit;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(final Module module) {
        this.module = module;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(final String branchName) {
        this.branchName = branchName;
    }

    public boolean isGlobalQuery() {
        return globalQuery;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefExplorerQuery that = (DefExplorerQuery) o;

        if (globalQuery != that.globalQuery) {
            return false;
        }
        if (repository != null ? !repository.equals(that.repository) : that.repository != null) {
            return false;
        }
        if (organizationalUnit != null ? !organizationalUnit.equals(that.organizationalUnit) : that.organizationalUnit != null) {
            return false;
        }
        if (module != null ? !module.equals(that.module) : that.module != null) {
            return false;
        }
        return !(branchName != null ? !branchName.equals(that.branchName) : that.branchName != null);
    }

    @Override
    public int hashCode() {
        int result = repository != null ? repository.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (organizationalUnit != null ? organizationalUnit.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (module != null ? module.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (branchName != null ? branchName.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (globalQuery ? 1 : 0);
        result = ~~result;
        return result;
    }
}
