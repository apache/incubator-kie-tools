/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.core.lookup.diagram;

import org.kie.workbench.common.stunner.core.lookup.VFSLookupRequest;
import org.uberfire.backend.vfs.Path;

public class DiagramLookupRequest extends VFSLookupRequest {

    public static final String CRITERIA_NAME = "name";

    public DiagramLookupRequest(final Path path,
                                final String criteria,
                                final int page,
                                final int pageSize) {
        super(path,
              criteria,
              page,
              pageSize);
    }

    public static class Builder extends VFSLookupRequest.Builder {

        private String name;

        public Builder withName(final String name) {
            this.name = null != name && name.trim().length() > 0 ? name : null;
            return this;
        }

        @Override
        public String getCriteria() {
            return null != name ?
                    fromKeyValue(CRITERIA_NAME, name) + super.getCriteria() :
                    super.getCriteria();
        }

        @Override
        public DiagramLookupRequest build() {
            return new DiagramLookupRequest(getPath(),
                                            getCriteria(),
                                            page,
                                            pageSize);
        }
    }
}
