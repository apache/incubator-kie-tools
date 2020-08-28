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

package org.dashbuilder.external.model;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ExternalComponent {
    
    public static final String COMPONENT_ID_KEY = "componentId";
    public static final String COMPONENT_PARTITION_KEY = "componentPartition";
    
    private String id;
    private String name;
    private String icon;
    private boolean noData;
    private List<ComponentParameter> parameters;

    public ExternalComponent() {
        // do nothing
    }

    public ExternalComponent(@MapsTo("id") String id,
                             @MapsTo("name") String name,
                             @MapsTo("icon") String icon,
                             @MapsTo("noData") boolean noData,
                             @MapsTo("parameters") List<ComponentParameter> parameters) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.noData = noData;
        this.parameters = parameters;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public boolean isNoData() {
        return noData;
    }

    public List<ComponentParameter> getParameters() {
        return parameters;
    }

}