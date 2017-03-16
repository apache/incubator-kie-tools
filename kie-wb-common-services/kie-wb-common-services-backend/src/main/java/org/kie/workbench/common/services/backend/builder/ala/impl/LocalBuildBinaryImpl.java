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

package org.kie.workbench.common.services.backend.builder.ala.impl;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.kie.workbench.common.services.backend.builder.ala.LocalBinaryConfig;
import org.kie.workbench.common.services.backend.builder.core.Builder;

public class LocalBuildBinaryImpl
        implements LocalBinaryConfig {

    private BuildResults buildResults;

    private IncrementalBuildResults incrementalBuildResults;

    private Builder builder;

    public LocalBuildBinaryImpl( BuildResults buildResults ) {
        this.buildResults = buildResults;
    }

    public LocalBuildBinaryImpl( Builder builder, BuildResults buildResults ) {
        this.builder = builder;
        this.buildResults = buildResults;
    }

    public LocalBuildBinaryImpl( IncrementalBuildResults incrementalBuildResults ) {
        this.incrementalBuildResults = incrementalBuildResults;
    }

    @Override
    public BuildResults getBuildResults( ) {
        return buildResults;
    }

    @Override
    public IncrementalBuildResults getIncrementalBuildResults( ) {
        return incrementalBuildResults;
    }

    @Override
    public Builder getBuilder( ) {
        return builder;
    }
}