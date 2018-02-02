/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.utils;

import java.util.Comparator;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;

/**
 * Sorters
 */
public class Sorters {

    /**
     * A comparator to sort OrganizationalUnits alphabetically by group name.
     */
    public static Comparator<OrganizationalUnit> ORGANIZATIONAL_UNIT_SORTER = new Comparator<OrganizationalUnit>() {
        @Override
        public int compare(final OrganizationalUnit o1,
                           final OrganizationalUnit o2) {
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
    };

    /**
     * A comparator to sort Projects alphabetically by repository name.
     */
    public static Comparator<WorkspaceProject> PROJECT_SORTER = new Comparator<WorkspaceProject>() {
        @Override
        public int compare(final WorkspaceProject o1,
                           final WorkspaceProject o2) {
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
    };

    /**
     * A comparator to sort Modules alphabetically by project name.
     */
    public static Comparator<Module> MODULE_SORTER = new Comparator<Module>() {
        @Override
        public int compare(final Module o1,
                           final Module o2) {
            return toLowerCase(o1.getRootPath()).compareTo(toLowerCase(o2.getRootPath()));
        }
    };

    /**
     * A comparator to sort Packages alphabetically by package name.
     */
    public static Comparator<Package> PACKAGE_SORTER = new Comparator<Package>() {
        @Override
        public int compare(final Package o1,
                           final Package o2) {
            return o1.getCaption().toLowerCase().compareTo(o2.getCaption().toLowerCase());
        }
    };

    /**
     * A comparator to sort FolderItems alphabetically by filename.
     */
    public static Comparator<FolderItem> ITEM_SORTER = new Comparator<FolderItem>() {
        @Override
        public int compare(final FolderItem o1,
                           final FolderItem o2) {

            if (o1.getItem() instanceof Package && o2.getItem() instanceof Path) {
                return -1;
            } else if (o1.getItem() instanceof Path && o2.getItem() instanceof Package) {
                return 1;
            }

            if (o1.getItem() instanceof Path) {
                if (!o1.getType().equals(o2.getType())) {
                    if (o1.getType().equals(FolderItemType.FOLDER)) {
                        return -1;
                    }
                    return 1;
                }
                return toLowerCase((Path) o1.getItem()).compareTo(toLowerCase((Path) o2.getItem()));
            }

            return ((Package) o1.getItem()).getCaption().toLowerCase().compareTo(((Package) o2.getItem()).getCaption().toLowerCase());
        }
    };

    /**
     * A comparator to sort ClientResourceTypes alphabetically by description.
     */
    public static Comparator<ClientResourceType> RESOURCE_TYPE_GROUP_SORTER = new Comparator<ClientResourceType>() {
        @Override
        public int compare(final ClientResourceType o1,
                           final ClientResourceType o2) {
            final String o1description = o1.getDescription();
            final String o2description = o2.getDescription();
            if (o1description == null && o2description == null) {
                return 0;
            }
            if (o1description == null && o2description != null) {
                return 1;
            }
            if (o1description != null && o2description == null) {
                return -1;
            }
            return o1description.compareTo(o2description);
        }
    };

    /**
     * A comparator to sort FolderItems alphabetically by folder and then files.
     */
    public static Comparator<FolderItem> FOLDER_LISTING_SORTER = new Comparator<FolderItem>() {

        @Override
        public int compare(final FolderItem o1,
                           final FolderItem o2) {
            final int comparison = o1.getType().compareTo(o2.getType());
            if (comparison == 0) {
                return compareTo(o1,
                                 o2);
            }
            return comparison;
        }

        public int compareTo(final FolderItem o1,
                             final FolderItem o2) {
            if (o1.getItem() instanceof Path) {
                return toLowerCase((Path) o1.getItem()).compareTo(toLowerCase((Path) o2.getItem()));
            }

            return ((Package) o1.getItem()).getCaption().toLowerCase().compareTo(((Package) o2.getItem()).getCaption().toLowerCase());
        }
    };

    private static String toLowerCase(final Path path) {
        return path == null ? "" : path.toURI().toLowerCase();
    }
}
