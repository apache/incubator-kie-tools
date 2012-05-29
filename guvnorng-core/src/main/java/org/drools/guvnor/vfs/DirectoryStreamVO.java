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

package org.drools.guvnor.vfs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.drools.java.nio.IOException;
import org.drools.java.nio.file.DirectoryStream;
import org.drools.java.nio.file.ExtendedPath;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DirectoryStreamVO implements DirectoryStream<ExtendedPath> {

    private final List<ExtendedPathVO> content = new ArrayList<ExtendedPathVO>();

    public DirectoryStreamVO(final DirectoryStream<ExtendedPath> stream) {
        for (final ExtendedPath path : stream) {
            content.add(new ExtendedPathVO(path));
        }
    }

    public DirectoryStreamVO() {
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public Iterator<ExtendedPath> iterator() {

        return new Iterator<ExtendedPath>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < content.size();
            }

            @Override
            public ExtendedPath next() {
                if (i < content.size()) {
                    ExtendedPath result = content.get(i);
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
