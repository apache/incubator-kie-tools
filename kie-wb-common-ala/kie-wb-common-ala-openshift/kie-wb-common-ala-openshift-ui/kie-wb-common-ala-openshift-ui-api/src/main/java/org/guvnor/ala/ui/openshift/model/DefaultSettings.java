/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.openshift.model;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DefaultSettings {

    public static final String DEFAULT_OPEN_SHIFT_TEMPLATE = "org.kie.provisioning.openshift.defaultTemplate";

    public static final String DEFAULT_OPEN_SHIFT_IMAGE_STREAMS = "org.kie.provisioning.openshift.defaultImageStreams";

    public static final String DEFAULT_OPEN_SHIFT_SECRETS = "org.kie.provisioning.openshift.defaultSecrets";

    private Map<String, Object> values = new HashMap<>();

    public DefaultSettings() {
    }

    public DefaultSettings(@MapsTo("values") final Map<String, Object> values) {
        this.values = values;
    }

    public Object getValue(final String name) {
        return values.get(name);
    }

    public void setValue(final String name,
                         final String value) {
        values.put(name,
                   value);
    }
}
