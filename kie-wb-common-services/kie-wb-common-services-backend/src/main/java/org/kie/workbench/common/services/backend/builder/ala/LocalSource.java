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

import org.guvnor.ala.source.Source;
import org.uberfire.java.nio.file.Path;

/**
 * Internal representation of the sources in the local build system.
 */
public class LocalSource implements Source {

    private Path path;

    public LocalSource( Path path ) {
        this.path = path;
    }

    /**
     * @return the path where the sources are located.
     */
    @Override
    public Path getPath( ) {
        return path;
    }
}
