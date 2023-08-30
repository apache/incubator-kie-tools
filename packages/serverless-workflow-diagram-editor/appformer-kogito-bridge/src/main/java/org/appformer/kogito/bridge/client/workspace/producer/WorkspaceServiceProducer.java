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


package org.appformer.kogito.bridge.client.workspace.producer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.appformer.kogito.bridge.client.interop.WindowRef;
import org.appformer.kogito.bridge.client.workspace.WorkspaceService;
import org.appformer.kogito.bridge.client.workspace.impl.DefaultWorkspaceServiceImpl;
import org.appformer.kogito.bridge.client.workspace.impl.WorkspaceServiceImpl;

/**
 * Produces {@link WorkspaceService} beans according to whether the envelope API is available or not.
 */
public class WorkspaceServiceProducer {

    @Produces
    @ApplicationScoped
    public WorkspaceService produce() {
        if (WindowRef.isEnvelopeAvailable()) {
            return new WorkspaceServiceImpl();
        }
        return new DefaultWorkspaceServiceImpl();
    }
}
