/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.appformer.kogito.bridge.client.capability;

import java.util.stream.Stream;

/**
 * Available status for capability responses.
 */
public enum CapabilityResponseStatus {
    /**
     * Response completed.
     */
    OK("OK"),

    /**
     * Infrastructure for capabilities is not available.
     */
    MISSING_INFRA("MISSING_INFRA"),

    /**
     * Capability could not be resolved.
     */
    NOT_AVAILABLE("NOT_AVAILABLE");

    private final String name;

    CapabilityResponseStatus(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * String to {@link CapabilityResponseStatus} conversion.
     *
     * @param name Name in string format.
     * @return The corresponding enum value.
     * @throws IllegalArgumentException In case no match found between the given name and {@link CapabilityResponseStatus}.
     */
    public static CapabilityResponseStatus withName(final String name) {
        return Stream.of(CapabilityResponseStatus.values())
                .filter(status -> status.getName().equalsIgnoreCase(name))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Name not recognized: " + name));
    }
}
