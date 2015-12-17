/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.shared.kmodule;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public enum EventProcessingOption {

    CLOUD("cloud"),
    STREAM("stream");

    /**
     * The property name for the sequential mode option
     */
    public static final String PROPERTY_NAME = "drools.eventProcessingMode";

    private String             string;

    EventProcessingOption(String mode) {
        this.string = mode;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public String getMode() {
        return string;
    }

    public String toString() {
        return "EventProcessingOption( "+string+ " )";
    }

    public String toExternalForm() {
        return this.string;
    }

    public static EventProcessingOption determineEventProcessingMode(String mode) {
        if ( STREAM.getMode().equalsIgnoreCase( mode ) ) {
            return STREAM;
        } else if ( CLOUD.getMode().equalsIgnoreCase( mode ) ) {
            return CLOUD;
        }
        throw new IllegalArgumentException( "Illegal enum value '" + mode + "' for EventProcessingMode" );
    }
}
