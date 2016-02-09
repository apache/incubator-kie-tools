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

import java.io.BufferedInputStream;
import java.io.InputStream;

import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;

public class ValidatorFileSystemProvider {

    private final KieFileSystem kieFileSystem;
    private final IOService     ioService;
    private final Path          resourcePath;
    private final Filter filter;
    private final InputStream   resource;
    private final KieProject    project;

    public ValidatorFileSystemProvider( final Path resourcePath,
                                        final InputStream resource,
                                        final KieProject project,
                                        final IOService ioService,
                                        final Filter filter ) throws NoProjectException {

        if ( project == null ) {
            throw new NoProjectException();
        }

        this.resourcePath = resourcePath;
        this.resource = resource;
        this.project = project;
        this.kieFileSystem = KieServices.Factory.get().newKieFileSystem();
        this.ioService = ioService;
        this.filter = filter;
    }

    public void write() {

        //Add resource to be validated first as:-
        // - KieBuilder fails fast on some compilation issues
        // - KieBuilder validates resources in the order they were added
        // - We want to catch errors for the resource being validated first
        kieFileSystem.write( getDestinationPath(),
                             KieServices.Factory.get().getResources().newInputStreamResource( new BufferedInputStream( resource ) ) );

        //Visit other files that may be needed to support validation of required resource
        visitPaths( getProjectDirectoryStream() );
    }

    private void visitPaths( final DirectoryStream<org.uberfire.java.nio.file.Path> directoryStream ) {
        for ( final org.uberfire.java.nio.file.Path path : directoryStream ) {
            if ( Files.isDirectory( path ) ) {

                visitPaths( Files.newDirectoryStream( path ) );

            } else if ( filter.accept( path ) ) {

                kieFileSystem.write( getDestinationPath( path ),
                                     KieServices.Factory.get().getResources().newInputStreamResource( new BufferedInputStream( ioService.newInputStream( path ) ) ) );

            }
        }
    }

    private DirectoryStream<org.uberfire.java.nio.file.Path> getProjectDirectoryStream() {
        return Files.newDirectoryStream( Paths.convert( project.getRootPath() ) );
    }

    private int getRootPathLength() {
        return project.getRootPath().toURI().length() + 1;
    }

    private String getDestinationPath( final org.uberfire.java.nio.file.Path path ) {
        return path.toUri().toString().substring( getRootPathLength() );
    }

    public String getDestinationPath() {
        return resourcePath.toURI().substring( getRootPathLength() );
    }

    public KieFileSystem getFileSystem() {
        return kieFileSystem;
    }
}
