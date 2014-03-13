/*
 * Copyright 2013 JBoss Inc
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

package org.kie.workbench.common.services.backend.source;

import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public abstract class DRLBaseSourceService
        extends BaseSourceService<String> {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Override
    public String getSource( final Path path,
                             final String drl ) {
        return drl;
    }

    @Override
    public String getSource(Path path) {
        return ioService.readAllString(path);
    }
}
