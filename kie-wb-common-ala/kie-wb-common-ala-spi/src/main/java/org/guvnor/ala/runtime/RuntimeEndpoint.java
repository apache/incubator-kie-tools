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

package org.guvnor.ala.runtime;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;

/**
 * This interface represent the Runtime Endpoint, meaning the endpoint that
 * can be used to interact against this Runtime
 */
@JsonTypeInfo(use = CLASS, include = WRAPPER_OBJECT)
public interface RuntimeEndpoint {

    /*
     * Get the Runtime Endpoint protocol
     * @return String with the protocol
    */
    String getProtocol();

    /*
     * Get the Runtime Endpoint host
     * @return String with the host
    */
    String getHost();

    /*
     * Get the Runtime Endpoint port
     * @return Integer with the port number
     *   (or null if not defined)
    */
    Integer getPort();

    /*
     * Get the Runtime Endpoint context
     * @return String with the application context 
     *   (if multiple applications are hosted on the same host:port)
    */
    String getContext();

}
