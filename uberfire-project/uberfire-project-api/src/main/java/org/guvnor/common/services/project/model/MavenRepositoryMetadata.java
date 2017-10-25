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

package org.guvnor.common.services.project.model;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;

@Portable
public class MavenRepositoryMetadata implements Serializable {

    private String id;
    private String url;
    private MavenRepositorySource source;

    public MavenRepositoryMetadata() {
        //Required for java.io.Serializable
    }

    public MavenRepositoryMetadata(final @MapsTo("id") String id,
                                   final @MapsTo("url") String url,
                                   final @MapsTo("source") MavenRepositorySource source) {
        this.id = PortablePreconditions.checkNotNull("id",
                                                     id);
        this.url = PortablePreconditions.checkNotNull("url",
                                                      url);
        this.source = PortablePreconditions.checkNotNull("source",
                                                         source);
    }

    public String getId() {
        return id;
    }

    public MavenRepositorySource getSource() {
        return source;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MavenRepositoryMetadata)) {
            return false;
        }

        MavenRepositoryMetadata that = (MavenRepositoryMetadata) o;

        if (!id.equals(that.id)) {
            return false;
        }
        if (!url.equals(that.url)) {
            return false;
        }
        return source == that.source;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = ~~result;
        result = 31 * result + url.hashCode();
        result = ~~result;
        result = 31 * result + source.hashCode();
        result = ~~result;
        return result;
    }
}
