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
package org.guvnor.ala.build.maven.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.guvnor.ala.config.BuildConfig;

/*
 * Maven specific Project builds configuration. This interface represents the basic information needed
 *  to build a Maven Project. 
 * @see BuildConfig
 */
public interface MavenBuildConfig extends BuildConfig {

    /*
     * Get the maven Goals to build the project
     * @return List<String> with the goals to be executed
    */
    default List<String> getGoals() {
        final List<String> result = new ArrayList<>();
        result.add("package");
        return result;
    }

    default Properties getProperties() {
        final Properties result = new Properties();
        result.setProperty("failIfNoTests",
                           "false");
        return result;
    }
}
