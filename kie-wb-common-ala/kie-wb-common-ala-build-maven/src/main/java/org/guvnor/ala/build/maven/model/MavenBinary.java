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

import org.guvnor.ala.build.Binary;

/*
 * This interface represent the basic information about a Binary produced by a Maven Build process
 * @see Binary
 */
public interface MavenBinary extends Binary {

    default String getType() {
        return "Maven";
    }

    String getArtifactId();

    String getVersion();

    String getGroupId();
}
