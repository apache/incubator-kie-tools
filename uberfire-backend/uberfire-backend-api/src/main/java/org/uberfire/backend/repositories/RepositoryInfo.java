package org.uberfire.backend.repositories;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.base.version.VersionRecord;

@Portable
public class RepositoryInfo {

    private String alias;
    private String owner;
    private Path root;
    private List<PublicURI> publicURIs = new ArrayList<PublicURI>();
    private List<VersionRecord> versionList = new ArrayList<VersionRecord>();

    public RepositoryInfo() {
    }

    public RepositoryInfo( final String alias,
                           final String owner,
                           final Path root,
                           final List<PublicURI> publicURIs,
                           final List<VersionRecord> versionList ) {
        this.alias = alias;
        this.owner = owner;
        this.root = root;
        this.publicURIs = publicURIs;
        this.versionList = versionList;
    }

    public List<PublicURI> getPublicURIs() {
        return publicURIs;
    }

    public String getAlias() {
        return alias;
    }

    public List<VersionRecord> getInitialVersionList() {
        return versionList;
    }

    public String getOwner() {
        return owner;
    }

    public Path getRoot() {
        return root;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof RepositoryInfo ) ) {
            return false;
        }

        RepositoryInfo that = (RepositoryInfo) o;

        if ( alias != null ? !alias.equals( that.alias ) : that.alias != null ) {
            return false;
        }
        if ( owner != null ? !owner.equals( that.owner ) : that.owner != null ) {
            return false;
        }
        if ( publicURIs != null ? !publicURIs.equals( that.publicURIs ) : that.publicURIs != null ) {
            return false;
        }
        if ( root != null ? !root.equals( that.root ) : that.root != null ) {
            return false;
        }
        if ( versionList != null ? !versionList.equals( that.versionList ) : that.versionList != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = alias != null ? alias.hashCode() : 0;
        result = 31 * result + ( owner != null ? owner.hashCode() : 0 );
        result = 31 * result + ( root != null ? root.hashCode() : 0 );
        result = 31 * result + ( publicURIs != null ? publicURIs.hashCode() : 0 );
        result = 31 * result + ( versionList != null ? versionList.hashCode() : 0 );
        return result;
    }

    @Override
    public String toString() {
        return "RepositoryInfo{" +
                "alias='" + alias + '\'' +
                ", owner='" + owner + '\'' +
                ", root=" + root +
                ", publicURIs=" + publicURIs +
                ", versionList=" + versionList +
                '}';
    }
}
