/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.explorer.backend.server;

import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.structure.repositories.Repository;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.service.Option;

public class UserExplorerLastData {

    public static final UserExplorerLastData EMPTY = new UserExplorerLastData();

    private LastPackage lastPackage = null;
    private LastFolderItem lastFolderItem = null;
    private Set<Option> options = new HashSet<Option>();

    public boolean isDataEmpty() {
        return lastPackage == null && lastFolderItem == null;
    }

    public boolean isEmpty() {
        return isDataEmpty() && options.isEmpty();
    }

    public void setFolderItem(final Repository repository,
                              final String branch,
                              final Module module,
                              final FolderItem item) {
        lastFolderItem = new LastFolderItem(repository,
                                            branch,
                                            module,
                                            item);
    }

    public void setPackage(final Repository repository,
                           final String branch,
                           final Module module,
                           final Package pkg) {
        lastPackage = new LastPackage(repository,
                                      branch,
                                      module,
                                      pkg);
    }

    public void setOptions(final ActiveOptions options) {
        this.options.clear();
        this.options.addAll(options.getValues());
    }

    public LastPackage getLastPackage() {
        return lastPackage;
    }

    public LastFolderItem getLastFolderItem() {
        return lastFolderItem;
    }

    public Set<Option> getOptions() {
        return options;
    }

    public boolean deleteModule(final Module module) {
        boolean changed = false;
        if (lastPackage != null && lastPackage.getModule().equals(module)) {
            lastPackage = null;
            changed = true;
        }
        if (lastFolderItem != null && lastFolderItem.getModule().equals(module)) {
            lastFolderItem = null;
            changed = true;
        }
        return changed;
    }

    static class LastPackage {

        Repository repository;
        String branch;
        Module module;
        org.guvnor.common.services.project.model.Package pkg;

        LastPackage() {

        }

        LastPackage(final Repository repository,
                    final String branch,
                    final Module module,
                    final Package pkg) {
            this.repository = repository;
            this.branch = branch;
            this.module = module;
            this.pkg = pkg;
        }

        Repository getRepository() {
            return repository;
        }

        String getBranch() {
            return branch;
        }

        Module getModule() {
            return module;
        }

        Package getPkg() {
            return pkg;
        }
    }

    static class LastFolderItem {

        Repository repository;
        String branch;
        Module module;
        FolderItem item;

        LastFolderItem() {

        }

        LastFolderItem(final Repository repository,
                       final String branch,
                       final Module module,
                       final FolderItem item) {
            this.repository = repository;
            this.branch = branch;
            this.module = module;
            this.item = item;
        }

        Repository getRepository() {
            return repository;
        }

        String getBranch() {
            return branch;
        }

        Module getModule() {
            return module;
        }

        FolderItem getItem() {
            return item;
        }
    }
}
