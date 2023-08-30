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


package org.kie.workbench.common.stunner.core.diagram;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.uberfire.backend.vfs.Path;

@Portable
public class MetadataImpl extends AbstractMetadata {

    public MetadataImpl() {
    }

    private MetadataImpl(final @MapsTo("definitionSetId") String definitionSetId) {
        super(definitionSetId);
    }

    @Override
    public Class<? extends Metadata> getMetadataType() {
        return Metadata.class;
    }

    @NonPortable
    public static class MetadataImplBuilder {

        private final String defSetId;
        private final DefinitionManager definitionManager;
        private String title;
        private String ssid;
        private Path path;

        public MetadataImplBuilder(final String defSetId) {
            this(defSetId,
                 null);
        }

        public MetadataImplBuilder(final String defSetId,
                                   final DefinitionManager definitionManager) {
            this.defSetId = defSetId;
            this.definitionManager = definitionManager;
        }

        public MetadataImplBuilder setPath(final Path path) {
            this.path = path;
            return this;
        }

        public MetadataImplBuilder setTitle(final String t) {
            this.title = t;
            return this;
        }

        public MetadataImplBuilder setShapeSetId(final String id) {
            this.ssid = id;
            return this;
        }

        public MetadataImpl build() {
            final MetadataImpl result = new MetadataImpl(defSetId);
            result.setPath(path);
            if (null != definitionManager) {
                final Object defSet = definitionManager.definitionSets().getDefinitionSetById(defSetId);
                if (null != defSet) {
                    result.setTitle(null != title ? title :
                                            definitionManager.adapters().forDefinitionSet().getDescription(defSet));
                }
            } else {
                result.setTitle(title);
                result.setShapeSetId(ssid);
            }
            return result;
        }
    }
}
