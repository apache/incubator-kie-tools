/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.backend.service;

import java.util.Map;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.ui.model.ProviderKey;

/**
 * Helper class for building the pipeline input parameters given a runtime name, a provider and the sources.
 */
public class PipelineInputBuilder {

    private String runtimeName;

    private ProviderKey providerKey;

    private Map<String, String> params;

    public static PipelineInputBuilder newInstance() {
        return new PipelineInputBuilder();
    }

    private PipelineInputBuilder() {
    }

    public PipelineInputBuilder withRuntimeName(final String runtimeName) {
        this.runtimeName = runtimeName;
        return this;
    }

    public PipelineInputBuilder withProvider(final ProviderKey providerKey) {
        this.providerKey = providerKey;
        return this;
    }

    public PipelineInputBuilder withParams(final Map<String, String> params) {
        this.params = params;
        return this;
    }

    public Input build() {
        final Input input = new Input();

        if (runtimeName != null) {
            input.put(RuntimeConfig.RUNTIME_NAME,
                      runtimeName);
        }
        if (providerKey != null) {
            input.put(ProviderConfig.PROVIDER_NAME,
                      providerKey.getId());
        }
        if (params != null) {
            input.putAll(params);
        }

        return input;
    }
}
