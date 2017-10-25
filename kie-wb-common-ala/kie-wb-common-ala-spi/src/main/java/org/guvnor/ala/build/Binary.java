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

package org.guvnor.ala.build;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.guvnor.ala.config.BinaryConfig;
import org.uberfire.java.nio.file.Path;

/**
 * Store information about the generated binary. Implement this interface
 * to support different Binary types.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface Binary extends BinaryConfig {

    /*
     * Get the binary related project that was used to create it
     * @return the Project
     * @see Project
     */
    Project getProject();

    /*
     * Get the path location for the generated binary
     * @return the Path
     * @see Path
     */
    Path getPath();

    /*
     * Get the type of the binary
     * @return the type as String
     */
    String getType();

    /*
     * Get the binary's name
     * @return the binary name as String
     */
    String getName();
}
