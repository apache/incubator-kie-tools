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

package org.kie.workbench.common.stunner.core.diagram;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * A generic Exception representing failure to parse and load a {@link Diagram} instance.
 */
@Portable
public class DiagramParsingException extends RuntimeException {

    private Metadata metadata;
    private String xml;

    public DiagramParsingException() {
        //Required for serialization
    }

    public DiagramParsingException(final Metadata metadata,
                                   final String xml) {
        this.metadata = metadata;
        this.xml = xml;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public String getXml() {
        return xml;
    }
}
