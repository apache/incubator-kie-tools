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

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.data.Triple;

public class UserExplorerData {

    private OrganizationalUnit organizationalUnit = null;
    private Map<String, Object> content = new HashMap<String, Object>();

    private Map<String, Set<String>> organizationalUnitKeys = new HashMap<String, Set<String>>();
    private Map<String, Set<String>> repositoryKeys = new HashMap<String, Set<String>>();
    private Map<String, Set<String>> projectKeys = new HashMap<String, Set<String>>();

    public UserExplorerData() {
    }

    public OrganizationalUnit getOrganizationalUnit() {
        return organizationalUnit;
    }

    public Repository get( final OrganizationalUnit organizationalUnit ) {
        if ( organizationalUnit == null ) {
            return null;
        }
        final Object obj = content.get( organizationalUnit.getName() );
        if ( obj != null && obj instanceof Repository ) {
            return (Repository) obj;
        }
        return null;
    }

    public Project get( final OrganizationalUnit organizationalUnit,
                        final Repository repository ) {
        if ( organizationalUnit == null || repository == null ) {
            return null;
        }

        final Object obj = content.get( Pair.newPair( organizationalUnit.getName(), repository.getRoot() ).toString() );
        if ( obj != null && obj instanceof Project ) {
            return (Project) obj;
        }
        return null;
    }

    public FolderItem getFolderItem( final OrganizationalUnit organizationalUnit,
                                     final Repository repository,
                                     final Project project ) {
        if ( organizationalUnit == null || repository == null || project == null ) {
            return null;
        }

        final Object obj = content.get( new FolderItemKey( organizationalUnit.getName(), repository.getRoot(), project.getPomXMLPath().toURI() ).toString() );
        if ( obj != null && obj instanceof FolderItem ) {
            return (FolderItem) obj;
        }
        return null;
    }

    public Package getPackage( final OrganizationalUnit organizationalUnit,
                               final Repository repository,
                               final Project project ) {
        if ( organizationalUnit == null || repository == null || project == null ) {
            return null;
        }

        final Object obj = content.get( new PackageKey( organizationalUnit.getName(), repository.getRoot(), project.getPomXMLPath().toURI() ).toString() );
        if ( obj != null && obj instanceof Package ) {
            return (Package) obj;
        }
        return null;
    }

    public void setOrganizationalUnit( OrganizationalUnit organizationalUnit ) {
        this.organizationalUnit = organizationalUnit;
    }

    public void addRepository( final OrganizationalUnit organizationalUnit,
                               final Repository repository ) {
        content.put( organizationalUnit.getName(), repository );

        indexOrganizationalUnit( organizationalUnit );
    }

    public void addProject( final OrganizationalUnit organizationalUnit,
                            final Repository repository,
                            final Project project ) {
        final String key = Pair.newPair( organizationalUnit.getName(), repository.getRoot() ).toString();
        content.put( key, project );

        indexOrganizationalUnit( organizationalUnit, key );
        indexRepository( repository, key );
    }

    public void addFolderItem( final OrganizationalUnit organizationalUnit,
                               final Repository repository,
                               final Project project,
                               final FolderItem item ) {
        final String key = new FolderItemKey( organizationalUnit.getName(), repository.getRoot(), project.getPomXMLPath().toURI() ).toString();
        content.put( key, item );

        indexOrganizationalUnit( organizationalUnit, key );
        indexRepository( repository, key );
        indexProject( project, key );
    }

    public void addPackage( final OrganizationalUnit organizationalUnit,
                            final Repository repository,
                            final Project project,
                            final Package pkg ) {
        final String key = new PackageKey( organizationalUnit.getName(), repository.getRoot(), project.getPomXMLPath().toURI() ).toString();
        content.put( key, pkg );

        indexOrganizationalUnit( organizationalUnit, key );
        indexRepository( repository, key );
        indexProject( project, key );
    }

    private void indexOrganizationalUnit( OrganizationalUnit organizationalUnit ) {
        if ( !organizationalUnitKeys.containsKey( organizationalUnit.getName() ) ) {
            organizationalUnitKeys.put( organizationalUnit.getName(), new HashSet<String>() );
        }
        organizationalUnitKeys.get( organizationalUnit.getName() ).add( organizationalUnit.getName() );
    }

    private void indexOrganizationalUnit( final OrganizationalUnit organizationalUnit,
                                          final String key ) {
        if ( !organizationalUnitKeys.containsKey( organizationalUnit.getName() ) ) {
            organizationalUnitKeys.put( organizationalUnit.getName(), new HashSet<String>() );
        }
        organizationalUnitKeys.get( organizationalUnit.getName() ).add( key );
    }

    private void indexRepository( final Repository repository,
                                  final String key ) {
        if ( !repositoryKeys.containsKey( repository.getUri() ) ) {
            repositoryKeys.put( repository.getUri(), new HashSet<String>() );
        }
        repositoryKeys.get( repository.getUri() ).add( key );

    }

    private void indexProject( final Project project,
                               final String key ) {
        final String projectRef = project.getPomXMLPath().toURI();
        if ( !projectKeys.containsKey( projectRef ) ) {
            projectKeys.put( projectRef, new HashSet<String>() );
        }
        projectKeys.get( projectRef ).add( key );
    }

    public boolean isEmpty() {
        return organizationalUnit == null && content.isEmpty();
    }

    public void deleteOrganizationalUnit( final OrganizationalUnit organizationalUnit ) {
        if ( this.organizationalUnit.equals( organizationalUnit ) ) {
            this.organizationalUnit = null;
        }
        if ( organizationalUnitKeys.containsKey( organizationalUnit.getName() ) ) {
            for ( final String key2Delete : organizationalUnitKeys.get( organizationalUnit.getName() ) ) {
                content.remove( key2Delete );
            }
        }
    }

    public void deleteRepository( final Repository repository ) {
        if ( repositoryKeys.containsKey( repository.getUri() ) ) {
            for ( final String key2Delete : repositoryKeys.get( repository.getUri() ) ) {
                content.remove( key2Delete );
            }
        }
    }

    public boolean deleteProject( final Project project ) {
        boolean changed = false;
        final String projectRef = project.getPomXMLPath().toURI();

        if ( projectKeys.containsKey( projectRef ) ) {
            changed = true;
            for ( final String key2Delete : projectKeys.get( projectRef ) ) {
                content.remove( key2Delete );
            }
        }
        return changed;
    }

    private static class FolderItemKey extends Triple<String, String, String> {


        public FolderItemKey(String name, Path root, String s3) {
            super(name, root.toURI(), s3);
        }

        @Override
        public boolean equals( Object o ) {
            if ( o == null ) {
                return false;
            }
            if ( this == o ) {
                return true;
            }
            if ( !( o instanceof FolderItemKey ) ) {
                return false;
            }

            return super.equals( o );
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
            super(name, root.toURI(), s3);
        }

        @Override
        public boolean equals( Object o ) {
            if ( o == null ) {
                return false;
            }
            if ( this == o ) {
                return true;
            }
            if ( !( o instanceof PackageKey ) ) {
                return false;
            }

            return super.equals( o );
        }

        @Override

        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + PackageKey.class.hashCode();
            return result;
        }
    }
}
