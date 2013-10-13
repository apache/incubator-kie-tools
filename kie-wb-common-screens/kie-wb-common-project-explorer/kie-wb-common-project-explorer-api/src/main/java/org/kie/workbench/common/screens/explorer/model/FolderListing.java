/*
 * Copyright 2012 JBoss Inc
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
package org.kie.workbench.common.screens.explorer.model;

import java.util.Collection;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * The contents of a folder
 */
@Portable
public class FolderListing {

    private Path path;
    private Path parentPath;
    private Collection<FolderItem> folderItems;
    private List<Path> segments;

    public FolderListing() {
        //For Errai-marshalling
    }

    public FolderListing( final Path path,
                          final Path parentPath,
                          final Collection<FolderItem> folderItems,
                          final List<Path> segments ) {
        this.path = PortablePreconditions.checkNotNull( "path",
                                                        path );
        this.parentPath = PortablePreconditions.checkNotNull( "parentPath",
                                                              parentPath );
        this.folderItems = PortablePreconditions.checkNotNull( "folderItems",
                                                               folderItems );
        this.segments = PortablePreconditions.checkNotNull( "segments",
                                                            segments );
    }

    public Path getPath() {
        return path;
    }

    public Path getParentPath() {
        return parentPath;
    }

    public Collection<FolderItem> getFolderItems() {
        return folderItems;
    }

    public List<Path> getSegments() {
        return segments;
    }

}
