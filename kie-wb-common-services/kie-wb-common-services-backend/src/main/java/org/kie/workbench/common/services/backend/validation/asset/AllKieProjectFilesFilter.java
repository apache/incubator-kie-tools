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

import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.backend.file.DotFileFilter;
import org.guvnor.common.services.backend.file.JavaFileFilter;
import org.kie.workbench.common.services.backend.file.DRLFileFilter;
import org.kie.workbench.common.services.backend.file.DSLFileFilter;
import org.kie.workbench.common.services.backend.file.DSLRFileFilter;
import org.kie.workbench.common.services.backend.file.GlobalsFileFilter;
import org.kie.workbench.common.services.backend.file.RDRLFileFilter;
import org.kie.workbench.common.services.backend.file.RDSLRFileFilter;
import org.kie.workbench.common.services.backend.validation.KModuleFileFilter;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Path;

/**
 * Filter for building a full Kie project
 */
public class AllKieProjectFilesFilter
        implements Filter {

    private final Set<DirectoryStream.Filter<Path>> filters = new HashSet<DirectoryStream.Filter<Path>>();

    private final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> dotFileFilter = new DotFileFilter();

    public AllKieProjectFilesFilter( final DirectoryStream.Filter<org.uberfire.java.nio.file.Path>... supportingFileFilters ) {

        filters.add( new JavaFileFilter() );
        filters.add( new DRLFileFilter() );
        filters.add( new DSLRFileFilter() );
        filters.add( new DSLFileFilter() );
        filters.add( new RDRLFileFilter() );
        filters.add( new RDSLRFileFilter() );
        filters.add( new GlobalsFileFilter() );
        filters.add( new KModuleFileFilter() );

        for ( DirectoryStream.Filter<Path> supportingFileFilter : supportingFileFilters ) {
            filters.add( supportingFileFilter );
        }
    }

    @Override
    public boolean accept( final Path path ) {
        if ( dotFileFilter.accept( path ) ) {
            return false;
        } else {
            for ( DirectoryStream.Filter<org.uberfire.java.nio.file.Path> filter : filters ) {
                if ( filter.accept( path ) ) {
                    return true;
                }
            }
        }

        return false;
    }
}
