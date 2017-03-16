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

package org.kie.workbench.common.services.backend.builder.ala;

import java.net.URI;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.ala.config.Config;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.source.Source;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

/**
 * Executor for a LocalSourceConfig configuration.
 */
@ApplicationScoped
public class LocalSourceConfigExecutor
        implements FunctionConfigExecutor< LocalSourceConfig, Source > {

    public LocalSourceConfigExecutor( ) {
        //Empty constructor for Weld proxying
    }

    /**
     * This executor mainly translates the sources configuration provided by the pipeline input into an internal format
     * convenient for the local build system.
     *
     * @param localSourceConfig the local sources configuration.
     *
     * @return the internal representation of the sources in the local build system.
     */
    @Override
    public Optional< Source > apply( LocalSourceConfig localSourceConfig ) {
        Path path = Paths.get( URI.create( localSourceConfig.getRootPath() ) );
        return Optional.of( new LocalSource( path ) );
    }

    @Override
    public Class< ? extends Config > executeFor( ) {
        return LocalSourceConfig.class;
    }

    @Override
    public String outputId( ) {
        return "local-source";
    }
}