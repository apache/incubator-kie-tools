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

package org.kie.workbench.common.dmn.client.docks.navigator.common;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class LazyCanvasFocusUtils {

    private CanvasFocusUtils canvasFocusUtils;

    private String focusedNodeUUID;

    public LazyCanvasFocusUtils() {
        // CDI proxy
    }

    @Inject
    public LazyCanvasFocusUtils(final CanvasFocusUtils canvasFocusUtils) {
        this.canvasFocusUtils = canvasFocusUtils;
    }

    public void lazyFocus(final String nodeUUID) {
        this.focusedNodeUUID = nodeUUID;
    }

    public void releaseFocus() {
        getFocusedNodeUUID().ifPresent(canvasFocusUtils::focus);
        releaseNodeUUID();
    }

    private void releaseNodeUUID() {
        this.focusedNodeUUID = null;
    }

    private Optional<String> getFocusedNodeUUID() {
        return Optional.ofNullable(focusedNodeUUID);
    }
}
