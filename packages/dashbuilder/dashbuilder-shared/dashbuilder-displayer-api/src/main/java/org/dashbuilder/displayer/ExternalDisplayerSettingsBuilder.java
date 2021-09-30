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
package org.dashbuilder.displayer;

/**
 * A displayer settings builder for external components
 *
 */
public interface ExternalDisplayerSettingsBuilder<T extends ExternalDisplayerSettingsBuilder> extends ChartSettingsBuilder<T> {

    /**
     * Set the external component id.
     * @param id
     *  The component Id
     * @return
     * This builder instance
     */
    T componentId(String id);

    /**
     * Set an external component property
     * @param key
     * The parameter key
     * @param value
     * The parameter value
     * @return
     * this builder instance
     */
    T componentProperty(String key, String value);

}