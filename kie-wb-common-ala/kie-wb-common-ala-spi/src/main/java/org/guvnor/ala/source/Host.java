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

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.guvnor.ala.security.Credentials;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;

/*
 * Service Host representation with credentials
*/
@JsonTypeInfo(use = CLASS, include = WRAPPER_OBJECT)
public interface Host<C extends Credentials> {

    /*
     * Get Host Id
     * @return String with the host id
    */
    String getId();

    /*
     * Get Host Name
     * @return String with the host name
    */
    String getName();

    /*
     * Get Repository for the Host
     * @param String repositoryId
     * @return Repository with the provided Repository Id
    */
    Repository getRepository(final String id);

    /*
    * Get Repository for the Host
    * @param String repositoryId
    * @param Map<String, String> with repository configurations
    * @return Repository with the provided Repository Id and configurations
   */
    Repository getRepository(final String id,
                             final Map<String, String> config);

    /*
     * Get Repository for the Host
     * @param Credentials credentials used to access the host
     * @param String repositoryId
     * @param Map<String, String> with repository configurations
     * @return Repository with the provided Repository Id and configurations
    */
    Repository getRepository(final C credential,
                             final String repositoryId,
                             final Map<String, String> config);
}
