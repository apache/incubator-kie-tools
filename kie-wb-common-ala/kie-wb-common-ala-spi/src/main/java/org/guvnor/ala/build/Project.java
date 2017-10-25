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
import org.guvnor.ala.config.ProjectConfig;
import org.uberfire.java.nio.file.Path;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;

/**
 * Represent one logical project Project. Implement this interface
 * to provider different project types.
 */
@JsonTypeInfo(use = CLASS, include = WRAPPER_OBJECT)
public interface Project extends ProjectConfig {

    /*
     * Get the project Id
     * @return the project Id
     */
    String getId();

    /*
     * Get the project type as String
     * @return the project Type
     */
    String getType();

    /*
     * Get the project name
     * @return the project Name
     */
    String getName();

    /*
     * Get the project expected binary name
     * @return the expected binary name
     */
    String getExpectedBinary();

    /*
     * Get the project root path
     * @return the project Root Path
     * @see Path
     */
    Path getRootPath();

    /*
     * Get the project path
     * @return the project Path
     * @see Path
     */
    Path getPath();

    /*
     * Get the project path where the produced binary will be stored
     * @return the project binary result Path
     * @see Path
     */
    Path getBinaryPath();

    String getTempDir();
}
