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
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.DirectoryStream;
import org.uberfire.backend.vfs.Path;

@Portable
public class DirectoryStreamImpl implements DirectoryStream<Path> {

    private List<Path> content;

    public DirectoryStreamImpl() {

    }

    public DirectoryStreamImpl( final List<Path> content ) {
        this.content = new ArrayList<Path>( content );
    }

    @Override
    public Iterator<Path> iterator() {
        return new Iterator<Path>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < content.size();
            }

            @Override
            public Path next() {
                if ( i < content.size() ) {
                    final Path result = content.get( i );
                    i++;
                    return result;
                } else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
