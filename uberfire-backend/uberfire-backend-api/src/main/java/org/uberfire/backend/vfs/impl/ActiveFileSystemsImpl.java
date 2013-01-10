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

package org.uberfire.backend.vfs.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.FileSystem;
import org.uberfire.backend.vfs.Path;

public class ActiveFileSystemsImpl implements ActiveFileSystems {

    private final List<FileSystem> fileSystems = new ArrayList<FileSystem>();

    @Override
    public void addBootstrapFileSystem( final FileSystem fs ) {
        fileSystems.add( 0, fs );
    }

    @Override
    public void addFileSystem( final FileSystem fs ) {
        fileSystems.add( fs );
    }

    @Override
    public Collection<FileSystem> fileSystems() {
        return fileSystems;
    }

    @Override
    public FileSystem getBootstrapFileSystem() {
        return fileSystems.get( 0 );
    }

}
