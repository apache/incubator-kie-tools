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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.data.Triple;

public class UserExplorerData {

    private Map<String, Object> content = new HashMap<String, Object>();

    private Map<String, Set<String>> repositoryKeys = new HashMap<String, Set<String>>();
    private Map<String, Set<String>> moduleKeys = new HashMap<String, Set<String>>();

    public UserExplorerData() {
    }

    public Repository get(final OrganizationalUnit organizationalUnit) {
        if (organizationalUnit == null) {
            return null;
        }
        final Object obj = content.get(organizationalUnit.getName());
        if (obj != null && obj instanceof Repository) {
            return (Repository) obj;
        }
        return null;
    }

    public Module get(final OrganizationalUnit organizationalUnit,
                      final Repository repository) {
        if (organizationalUnit == null || repository == null || !repository.getDefaultBranch().isPresent()) {
            return null;
        }

        final Object obj = content.get(Pair.newPair(organizationalUnit.getName(),
                                                    repository.getDefaultBranch().get().getPath()).toString());
        if (obj != null && obj instanceof Module) {
            return (Module) obj;
        }
        return null;
    }

    public FolderItem getFolderItem(final WorkspaceProject project,
                                    final Module module) {
        if (project.getOrganizationalUnit() == null || project.getRepository() == null || module == null) {
            return null;
        }

        final Object obj = content.get(new FolderItemKey(project.getOrganizationalUnit().getName(),
                                                         project.getBranch().getPath(),
                                                         module.getPomXMLPath().toURI()).toString());
        if (obj != null && obj instanceof FolderItem) {
            return (FolderItem) obj;
        }
        return null;
    }

    public Package getPackage(final WorkspaceProject project,
                              final Module module) {
        if (project.getRepository() == null || module == null) {
            return null;
        }

        final Object obj = content.get(new PackageKey(project.getOrganizationalUnit().getName(),
                                                      project.getBranch().getPath(),
                                                      module.getPomXMLPath().toURI()).toString());
        if (obj != null && obj instanceof Package) {
            return (Package) obj;
        }
        return null;
    }

    public void addRepository(final OrganizationalUnit organizationalUnit,
                              final Repository repository) {
        content.put(organizationalUnit.getName(),
                    repository);
    }

    public void addModule(final OrganizationalUnit organizationalUnit,
                          final Repository repository,
                          final Module module) {

        if (repository.getDefaultBranch().isPresent()) {

            final String key = Pair.newPair(organizationalUnit.getName(),
                                            repository.getDefaultBranch().get().getPath()).toString();
            content.put(key,
                        module);

            indexRepository(repository,
                            key);
        }
    }

    public void addFolderItem(final OrganizationalUnit organizationalUnit,
                              final Repository repository,
                              final Module module,
                              final FolderItem item) {
        if (repository.getDefaultBranch().isPresent()) {
            final String key = new FolderItemKey(organizationalUnit.getName(),
                                                 repository.getDefaultBranch().get().getPath(),
                                                 module.getPomXMLPath().toURI()).toString();
            content.put(key,
                        item);

            indexRepository(repository,
                            key);
            indexModule(module,
                        key);
        }
    }

    public void addPackage(final OrganizationalUnit organizationalUnit,
                           final Repository repository,
                           final Module module,
                           final Package pkg) {
        if (repository.getDefaultBranch().isPresent()) {
            final String key = new PackageKey(organizationalUnit.getName(),
                                              repository.getDefaultBranch().get().getPath(),
                                              module.getPomXMLPath().toURI()).toString();
            content.put(key,
                        pkg);

            indexRepository(repository,
                            key);
            indexModule(module,
                        key);
        }
    }

    private void indexRepository(final Repository repository,
                                 final String key) {
        if (!repositoryKeys.containsKey(repository.getUri())) {
            repositoryKeys.put(repository.getUri(),
                               new HashSet<String>());
        }
        repositoryKeys.get(repository.getUri()).add(key);
    }

    private void indexModule(final Module module,
                             final String key) {
        final String moduleRef = module.getPomXMLPath().toURI();
        if (!moduleKeys.containsKey(moduleRef)) {
            moduleKeys.put(moduleRef,
                           new HashSet<String>());
        }
        moduleKeys.get(moduleRef).add(key);
    }

    public boolean deleteModule(final Module module) {
        boolean changed = false;
        final String moduleRef = module.getPomXMLPath().toURI();

        if (moduleKeys.containsKey(moduleRef)) {
            changed = true;
            for (final String key2Delete : moduleKeys.get(moduleRef)) {
                content.remove(key2Delete);
            }
        }
        return changed;
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }

    private static class FolderItemKey extends Triple<String, String, String> {

        public FolderItemKey(String name,
                             Path root,
                             String s3) {
            super(name,
                  root.toURI(),
                  s3);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (this == o) {
                return true;
            }
            if (!(o instanceof FolderItemKey)) {
                return false;
            }

            return super.equals(o);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + FolderItemKey.class.hashCode();
            return result;
        }
    }

    private static class PackageKey extends Triple<String, String, String> {

        public PackageKey(String name,
                          Path root,
                          String s3) {
            super(name,
                  root.toURI(),
                  s3);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (this == o) {
                return true;
            }
            if (!(o instanceof PackageKey)) {
                return false;
            }

            return super.equals(o);
        }

        @Override

        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + PackageKey.class.hashCode();
            return result;
        }
    }
}
