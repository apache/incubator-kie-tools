/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.lookup;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Portable
public class VFSLookupRequest extends AbstractLookupRequest {

    public static final String CRITERIA_PATH = "path";

    private final Path path;

    public VFSLookupRequest(final @MapsTo("path") Path path,
                            final @MapsTo("criteria") String criteria,
                            final @MapsTo("page") int page,
                            final @MapsTo("pageSize") int pageSize) {
        super(criteria, page, pageSize);
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    @NonPortable
    public static class Builder extends AbstractLookupRequestBuilder<Builder> {

        private Path path;
        private final StringBuilder criteria = new StringBuilder();

        public Builder forPath(final Path path) {
            criteria.append(fromKeyValue(CRITERIA_PATH, path.toURI()));
            this.path = path;
            return this;
        }

        public VFSLookupRequest build() {
            checkNotNull("path", path);
            return new VFSLookupRequest(path,
                                        getCriteria(),
                                        page,
                                        pageSize);
        }

        public String getCriteria() {
            return criteria.toString();
        }

        public Path getPath() {
            return path;
        }
    }
}
