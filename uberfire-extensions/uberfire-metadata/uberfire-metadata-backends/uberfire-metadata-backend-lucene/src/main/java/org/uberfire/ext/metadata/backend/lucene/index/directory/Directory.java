/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.metadata.backend.lucene.index.directory;

import java.io.IOException;

public class Directory {

    private final boolean fresh;
    private final DeleteCommand command;

    private org.apache.lucene.store.Directory directory;

    public Directory( final org.apache.lucene.store.Directory directory,
                      final DeleteCommand command,
                      final boolean fresh ) {
        this.directory = directory;
        this.command = command;
        this.fresh = fresh;
    }

    public org.apache.lucene.store.Directory getDirectory() {
        return directory;
    }

    public void close() {
        try {
            directory.close();
        } catch ( final IOException e ) {
            throw new org.uberfire.java.nio.IOException( e );
        }
    }

    public boolean freshIndex() {
        return fresh;
    }

    public void delete() {
        command.execute( directory );
    }
}
