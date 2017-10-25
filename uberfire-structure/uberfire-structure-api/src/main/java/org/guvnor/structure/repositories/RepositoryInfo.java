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

package org.guvnor.structure.repositories;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.base.version.VersionRecord;

@Portable
public class RepositoryInfo {

    private String id;
    private String alias;
    private String owner;
    private Path root;
    private List<PublicURI> publicURIs = new ArrayList<PublicURI>();
    private List<VersionRecord> versionList = new ArrayList<VersionRecord>();

    public RepositoryInfo() {
    }

    public RepositoryInfo(final String id,
                          final String alias,
                          final String owner,
                          final Path root,
                          final List<PublicURI> publicURIs,
                          final List<VersionRecord> versionList) {
        this.id = id;
        this.alias = alias;
        this.owner = owner;
        this.root = root;
        this.publicURIs = publicURIs;
        this.versionList = versionList;
    }

    public String getId() {
        return id;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RepositoryInfo)) {
            return false;
        }

        RepositoryInfo that = (RepositoryInfo) o;

        if (alias != null ? !alias.equals(that.alias) : that.alias != null) {
            return false;
        }
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) {
            return false;
        }
        if (publicURIs != null ? !publicURIs.equals(that.publicURIs) : that.publicURIs != null) {
            return false;
        }
        if (root != null ? !root.equals(that.root) : that.root != null) {
            return false;
        }
        if (versionList != null ? !versionList.equals(that.versionList) : that.versionList != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = alias != null ? alias.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (root != null ? root.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (publicURIs != null ? publicURIs.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (versionList != null ? versionList.hashCode() : 0);
        result = ~~result;
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
