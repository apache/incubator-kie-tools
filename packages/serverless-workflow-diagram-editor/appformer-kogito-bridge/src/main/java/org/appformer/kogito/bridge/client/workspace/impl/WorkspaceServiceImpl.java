/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.appformer.kogito.bridge.client.workspace.impl;

import org.appformer.kogito.bridge.client.workspace.WorkspaceService;
import org.appformer.kogito.bridge.client.workspace.interop.WorkspaceServiceWrapper;

/**
 * A {@link WorkspaceService} implementation used when envelope API is available.
 */
public class WorkspaceServiceImpl implements WorkspaceService {

    @Override
    public void openFile(final String path) {
        WorkspaceServiceWrapper.get().openFile(path);
    }
}
