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

package org.uberfire.experimental.service.definition;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Definition of an experimental feature
 */
@Portable
public class ExperimentalFeatureDefinition {

    private String id;
    private boolean global;
    private String group;
    private String nameKey;
    private String descriptionKey;

    public ExperimentalFeatureDefinition(@MapsTo("id") String id, @MapsTo("global") boolean global, @MapsTo("group") String group, @MapsTo("nameKey") String nameKey, @MapsTo("descriptionKey") String descriptionKey) {
        this.id = id;
        this.global = global;
        this.group = group;
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
    }

    /**
     * Unique id for the feature definition
     * @return a String with the unique id
     */
    public String getId() {
        return id;
    }

    public boolean isGlobal() {
        return global;
    }

    public String getGroup() {
        return group;
    }

    /**
     * I18n key for that contains the name for the feature
     * @return a String containing the I18n key
     */
    public String getNameKey() {
        return nameKey;
    }

    /**
     * I18n key for that contains the description for the feature
     * @return a String containing the I18n key, can be null
     */
    public String getDescriptionKey() {
        return descriptionKey;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
    }

    public void setDescriptionKey(String descriptionKey) {
        this.descriptionKey = descriptionKey;
    }
}
