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

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An item in a package
 */
@Portable
public class FolderItem {

    private Path path;
    private String fileName;
    private FolderItemType type;

    public FolderItem() {
        //For Errai-marshalling
    }

    public FolderItem( final Path path,
                       final String fileName,
                       final FolderItemType type ) {
        this.path = PortablePreconditions.checkNotNull( "path",
                                                        path );
        this.fileName = PortablePreconditions.checkNotNull( "fileName",
                                                            fileName );
        this.type = PortablePreconditions.checkNotNull( "type",
                                                        type );
    }

    public Path getPath() {
        return this.path;
    }

    public String getFileName() {
        return this.fileName;
    }

    public FolderItemType getType() {
        return type;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof FolderItem ) ) {
            return false;
        }

        FolderItem folderItem = (FolderItem) o;

        if ( !path.equals( folderItem.path ) ) {
            return false;
        }
        if ( !fileName.equals( folderItem.fileName ) ) {
            return false;
        }
        if ( !type.equals( folderItem.type ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + fileName.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

}
