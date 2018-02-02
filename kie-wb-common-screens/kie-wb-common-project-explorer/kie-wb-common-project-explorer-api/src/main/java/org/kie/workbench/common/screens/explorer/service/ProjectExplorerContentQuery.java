/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.explorer.service;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.explorer.model.FolderItem;

@Portable
public class ProjectExplorerContentQuery {

    private Repository repository = null;
    private Branch branch = null;
    private Module module = null;
    private org.guvnor.common.services.project.model.Package pkg = null;
    private FolderItem item = null;
    private ActiveOptions options = null;

    public ProjectExplorerContentQuery() {
    }

    public ProjectExplorerContentQuery(final Repository repository,
                                       final Branch branch) {
        this.repository = repository;
        this.branch = branch;
    }

    public ProjectExplorerContentQuery(final Repository repository,
                                       final Branch branch,
                                       final Module module) {
        this.repository = repository;
        this.branch = branch;
        this.module = module;
    }

    public ProjectExplorerContentQuery(final Repository repository,
                                       final Branch branch,
                                       final Module module,
                                       final Package pkg) {
        this.repository = repository;
        this.branch = branch;
        this.module = module;
        this.pkg = pkg;
    }

    public ProjectExplorerContentQuery(final Repository repository,
                                       final Branch branch,
                                       final Module module,
                                       final Package pkg,
                                       final FolderItem item) {
        this.repository = repository;
        this.branch = branch;
        this.module = module;
        this.pkg = pkg;
        this.item = item;
    }

    public ProjectExplorerContentQuery(final Repository repository,
                                       final Branch branch,
                                       final Module module,
                                       final ActiveOptions activeOptions) {
        this.repository = repository;
        this.branch = branch;
        this.module = module;
        this.options = activeOptions;
    }

    public Repository getRepository() {
        return repository;
    }

    public Module getModule() {
        return module;
    }

    public Package getPkg() {
        return pkg;
    }

    public FolderItem getItem() {
        return item;
    }

    public ActiveOptions getOptions() {
        return options;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setOptions(ActiveOptions options) {
        this.options = options;
    }
}
