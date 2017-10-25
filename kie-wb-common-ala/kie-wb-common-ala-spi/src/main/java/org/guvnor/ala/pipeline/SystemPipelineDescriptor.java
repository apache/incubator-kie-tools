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

package org.guvnor.ala.pipeline;

import java.util.Optional;

import org.guvnor.ala.runtime.providers.ProviderType;

/**
 * Contract for components that produces pipelines that will be automatically registered when the system starts.
 */
public interface SystemPipelineDescriptor {

    /**
     * @return the pipeline to be registered.
     */
    Pipeline getPipeline();

    /**
     * Indicates if the pipeline to be registered must be associated to a given provider type.
     * @return an optional provider type. If present, the pipeline must be associated to the given provider type.
     */
    Optional<ProviderType> getProviderType();
}