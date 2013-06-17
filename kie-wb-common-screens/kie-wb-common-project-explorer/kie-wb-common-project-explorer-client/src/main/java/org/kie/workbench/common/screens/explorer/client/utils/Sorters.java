/*
 * Copyright 2013 JBoss Inc
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
package org.kie.workbench.common.screens.explorer.client.utils;

import java.util.Comparator;

import org.kie.workbench.common.screens.explorer.model.Item;
import org.kie.workbench.common.services.shared.context.Package;
import org.kie.workbench.common.services.shared.context.Project;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.client.workbench.type.ClientResourceType;

/**
 * Sorters
 */
public class Sorters {

    public static Comparator<Group> GROUP_SORTER = new Comparator<Group>() {
        @Override
        public int compare( final Group o1,
                            final Group o2 ) {
            return o1.getName().compareTo( o2.getName() );
        }
    };

    public static Comparator<Repository> REPOSITORY_SORTER = new Comparator<Repository>() {
        @Override
        public int compare( final Repository o1,
                            final Repository o2 ) {
            return o1.getAlias().compareTo( o2.getAlias() );
        }
    };

    public static Comparator<Project> PROJECT_SORTER = new Comparator<Project>() {
        @Override
        public int compare( final Project o1,
                            final Project o2 ) {
            return o1.getTitle().compareTo( o2.getTitle() );
        }
    };

    public static Comparator<Package> PACKAGE_SORTER = new Comparator<Package>() {
        @Override
        public int compare( final Package o1,
                            final Package o2 ) {
            return o1.getCaption().compareTo( o2.getCaption() );
        }
    };

    public static Comparator<Item> ITEM_SORTER = new Comparator<Item>() {
        @Override
        public int compare( final Item o1,
                            final Item o2 ) {
            return o1.getFileName().compareTo( o2.getFileName() );
        }
    };

    public static Comparator<ClientResourceType> RESOURCE_TYPE_GROUP_SORTER = new Comparator<ClientResourceType>() {
        @Override
        public int compare( final ClientResourceType o1,
                            final ClientResourceType o2 ) {
            final String o1description = o1.getDescription();
            final String o2description = o2.getDescription();
            if ( o1description == null && o2description == null ) {
                return 0;
            }
            if ( o1description == null && o2description != null ) {
                return 1;
            }
            if ( o1description != null && o2description == null ) {
                return -1;
            }
            return o1description.compareTo( o2description );
        }
    };

}
