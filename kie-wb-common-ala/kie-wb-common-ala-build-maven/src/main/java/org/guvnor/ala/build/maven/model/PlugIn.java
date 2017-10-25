/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.build.maven.model;

import java.util.Map;

/*
 * This interface represent a MavenPlugin definition with its configurations
*/
public interface PlugIn {

    /*
     * Get the Plugin's Id
     * @return String with the Plugin id
    */
    String getId();

    /*
     * Returns the PlugIn configuration
     * @return Map<String, ?> with the configuration used by the plugin
    */
    Map<String, ?> getConfiguration();
}
