package org.kie.workbench.common.screens.explorer.backend.server;

import java.util.HashMap;
import java.util.Map;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.data.Triple;

public class UserExplorerData {

    private OrganizationalUnit organizationalUnit = null;
    private Map<Object, Object> content = new HashMap<Object, Object>();

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

        final Object obj = content.get( Pair.newPair( organizationalUnit.getName(), repository.getAlias() ) );
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

        final Object obj = content.get( new FolderItemKey( organizationalUnit.getName(), repository.getAlias(), project.getPomXMLPath().toURI() ) );
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

        final Object obj = content.get( new PackageKey( organizationalUnit.getName(), repository.getAlias(), project.getPomXMLPath().toURI() ) );
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
    }

    public void addProject( final OrganizationalUnit organizationalUnit,
                            final Repository repository,
                            final Project project ) {
        content.put( Pair.newPair( organizationalUnit.getName(), repository.getAlias() ), project );
    }

    public void addFolderItem( final OrganizationalUnit organizationalUnit,
                               final Repository repository,
                               final Project project,
                               final FolderItem item ) {
        content.put( new FolderItemKey( organizationalUnit.getName(), repository.getAlias(), project.getPomXMLPath().toURI() ), item );
    }

    public void addPackage( final OrganizationalUnit organizationalUnit,
                            final Repository repository,
                            final Project project,
                            final Package pkg ) {
        content.put( new PackageKey( organizationalUnit.getName(), repository.getAlias(), project.getPomXMLPath().toURI() ), pkg );
    }

    public boolean isEmpty() {
        return organizationalUnit == null && content.isEmpty();
    }

    private static class FolderItemKey extends Triple<String, String, String> {

        public FolderItemKey( final String s,
                              final String s2,
                              final String s3 ) {
            super( s, s2, s3 );
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

        public PackageKey( final String s,
                           final String s2,
                           final String s3 ) {
            super( s, s2, s3 );
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
