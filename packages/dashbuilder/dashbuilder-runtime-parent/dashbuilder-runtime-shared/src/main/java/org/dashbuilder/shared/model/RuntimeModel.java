/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.shared.model;

import java.util.List;
import java.util.Map;

import org.dashbuilder.dataset.def.ExternalDataSetDef;
import org.dashbuilder.navigation.NavTree;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

/**
 * Representation of assets and information needed to build Runtime Dashbuilder client.
 *
 */
@Portable
public class RuntimeModel {

    GlobalSettings globalSettings;

    NavTree navTree;

    Map<String, String> properties;

    List<LayoutTemplate> layoutTemplates;

    List<ExternalDataSetDef> clientDataSets;

    Long lastModified;

    public RuntimeModel(@MapsTo("navTree") final NavTree navTree,
                        @MapsTo("layoutTemplates") final List<LayoutTemplate> layoutTemplates,
                        @MapsTo("lastModified") Long lastModified,
                        @MapsTo("clientDataSets") List<ExternalDataSetDef> clientDataSets,
                        @MapsTo("properties") Map<String, String> properties,
                        @MapsTo("globalSettings") GlobalSettings globalSettings) {
        this.navTree = navTree;
        this.layoutTemplates = layoutTemplates;
        this.lastModified = lastModified;
        this.clientDataSets = clientDataSets;
        this.properties = properties;
        this.globalSettings = globalSettings;
    }

    public NavTree getNavTree() {
        return navTree;
    }

    public List<LayoutTemplate> getLayoutTemplates() {
        return layoutTemplates;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public List<ExternalDataSetDef> getClientDataSets() {
        return clientDataSets;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public GlobalSettings getGlobalSettings() {
        return globalSettings;
    }

}
