/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.validation.asset;

import org.guvnor.common.services.backend.file.DotFileFilter;
import org.guvnor.common.services.backend.file.PomFileFilter;
import org.kie.workbench.common.services.backend.validation.KModuleFileFilter;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.file.DirectoryStream;

public class GenericFilter
        implements Filter {

    //Exclude dot-files
    private final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> dotFileFilter = new DotFileFilter();

    //Exclude Project's kmodule.xml file (in case it contains errors causing build to fail)
    private final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> kmoduleFileFilter = new KModuleFileFilter();

    //Include Project's pom.xml (to ensure dependencies are set-up correctly)
    private final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> pomFileFilter = new PomFileFilter();

    //Set-up filters ignoring resource being validated
    private final ResourceFilter                                            resourceFilter;
    private final DirectoryStream.Filter<org.uberfire.java.nio.file.Path>[] supportingFileFilters;

    public GenericFilter( final Path resourcePath,
                          final DirectoryStream.Filter<org.uberfire.java.nio.file.Path>... supportingFileFilters ) {
        this.supportingFileFilters = supportingFileFilters;
        resourceFilter = new ResourceFilter( resourcePath );
    }

    public boolean accept( final org.uberfire.java.nio.file.Path path ) {
        if ( dotFileFilter.accept( path ) ) {
            return false;
        } else if ( kmoduleFileFilter.accept( path ) ) {
            return false;
        } else if ( pomFileFilter.accept( path ) ) {
            return true;
        } else if ( resourceFilter.accept( path ) ) {
            return false;
        }

        for ( DirectoryStream.Filter<org.uberfire.java.nio.file.Path> filter : supportingFileFilters ) {
            if ( filter.accept( path ) ) {
                return true;
            }
        }

        return false;
    }

}
