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

package org.kie.workbench.common.stunner.kogito.client.service;

import elemental2.promise.Promise;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

public interface KogitoClientDiagramService {

    void transform(final String xml,
                   final ServiceCallback<Diagram> callback);

    /**
     * Transforms an XML into a Diagram. The fileName is provided to set a Id and Name same as fileName
     *
     * @param fileName FileName of file
     * @param xml      XML representation
     * @param callback Callback to signal success or failure
     */
    default void transform(final String fileName, final String xml, final ServiceCallback<Diagram> callback) {
        transform(xml, callback);
    }

    Promise<String> transform(final Diagram diagram);
}
