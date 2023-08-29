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


package org.kie.workbench.common.stunner.core.definition.impl;

import java.util.Objects;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DefinitionImpl {

    private final String id;
    private final PropertyImpl nameProperty;
    private final String category;
    private final String title;
    private final String description;
    private final Set<String> labels;
    private final Set<Object> propertySets;
    private final Set<Object> properties;

    public DefinitionImpl(final @MapsTo("id") String id,
                          final @MapsTo("category") String category,
                          final @MapsTo("title") String title,
                          final @MapsTo("description") String description,
                          final @MapsTo("labels") Set<String> labels,
                          final @MapsTo("propertySets") Set<Object> propertySets,
                          final @MapsTo("nameProperty") PropertyImpl nameProperty,
                          final @MapsTo("properties") Set<Object> properties) {
        this.category = checkNotNull("category", category);
        this.nameProperty = checkNotNull("nameProperty", nameProperty);
        this.title = checkNotNull("title", title);
        this.description = checkNotNull("description", description);
        this.labels = checkNotNull("labels", labels);
        this.id = checkNotNull("id", id);
        this.propertySets = checkNotNull("propertySets", propertySets);
        this.properties = checkNotNull("properties", properties);
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    public PropertyImpl getNameProperty() {
        return nameProperty;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public Set<Object> getPropertySets() {
        return propertySets;
    }

    public Set<Object> getProperties() {
        return properties;
    }
}
