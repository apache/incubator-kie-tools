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

package org.uberfire.experimental.service.editor;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.experimental.service.registry.ExperimentalFeature;

@Portable
public class EditableExperimentalFeature {

    private String featureId;
    private boolean enabled;

    public EditableExperimentalFeature(@MapsTo("featureId") String featureId, @MapsTo("enabled") boolean enabled) {
        this.featureId = featureId;
        this.enabled = enabled;
    }

    public EditableExperimentalFeature(ExperimentalFeature feature) {
        this.featureId = feature.getFeatureId();
        this.enabled = feature.isEnabled();
    }

    public String getFeatureId() {
        return featureId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
