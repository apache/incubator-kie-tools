/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.shared.model;

import java.util.List;
import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Runtime Service response model object that contains any useful information for the client.
 */
@Portable
public class RuntimeServiceResponse {

    private DashbuilderRuntimeMode mode;

    private Optional<RuntimeModel> runtimeModelOp;

    private List<String> availableModels;

    private boolean allowUpload;

    public RuntimeServiceResponse() {
        // not used
    }

    public RuntimeServiceResponse(@MapsTo("mode") DashbuilderRuntimeMode mode,
                                  @MapsTo("runtimeModelOp") Optional<RuntimeModel> runtimeModelOp,
                                  @MapsTo("availableModels") List<String> availableModels,
                                  @MapsTo("allowUpload") boolean allowUpload) {
        this.mode = mode;
        this.runtimeModelOp = runtimeModelOp;
        this.availableModels = availableModels;
        this.allowUpload = allowUpload;
    }

    public Optional<RuntimeModel> getRuntimeModelOp() {
        return runtimeModelOp;
    }

    public List<String> getAvailableModels() {
        return availableModels;
    }

    public DashbuilderRuntimeMode getMode() {
        return mode;
    }

    public boolean isAllowUpload() {
        return allowUpload;
    }

}
