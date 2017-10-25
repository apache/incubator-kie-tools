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

package org.guvnor.ala.source;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.guvnor.ala.exceptions.SourcingException;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;

/**
 * Type to represent a Source Code Repository
 */
@JsonTypeInfo(use = CLASS, include = WRAPPER_OBJECT)
public interface Repository {

    /*
     * Get the Repository Id
     * @return String with the repository id
    */
    String getId();

    /*
     * Get the Repository Name
     * @return String with the repository name
    */
    String getName();

    /*
    * Retrieve the source meta data from a Repository
    * @return a Souce from the current repository
    * @see Source
     */
    Source getSource() throws SourcingException;

    /*
    * Retrieve the source code from a Repository
    * returns the location (path) of the obtained code
    * @throws SourcingException if the repository cannot  be located or the code cannot be retrieved
    * @param String root path 
    * @param String... path inside the repository where the Source can be located
    * @return the Source from the specified repository path
    * @see Source
     */
    Source getSource(final String root,
                     final String... path) throws SourcingException;
}
