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

package org.uberfire.java.nio.fs.file;

import java.io.File;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.AbstractBasicFileAttributeView;
import org.uberfire.java.nio.base.BasicFileAttributesImpl;
import org.uberfire.java.nio.base.FileTimeImpl;
import org.uberfire.java.nio.base.LazyAttrLoader;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

/**
 *
 */
public class SimpleBasicFileAttributeView extends AbstractBasicFileAttributeView<Path> {

    private BasicFileAttributes attrs = null;

    public SimpleBasicFileAttributeView( final Path path ) {
        super( path );
    }

    @Override
    public <T extends BasicFileAttributes> T readAttributes() throws IOException {
        if ( attrs == null ) {
            final File file = path.toFile();
            this.attrs = new BasicFileAttributesImpl( path.toString(), new FileTimeImpl( file.lastModified() ), null, null, new LazyAttrLoader<Long>() {
                private Long size = null;

                @Override
                public Long get() {
                    if ( size == null ) {
                        size = file.length();
                    }

                    return size;
                }
            }, file.isFile(), file.isDirectory() );
        }
        return (T) attrs;
    }

    @Override
    public Class<? extends BasicFileAttributeView>[] viewTypes() {
        return new Class[]{ BasicFileAttributeView.class, SimpleBasicFileAttributeView.class };
    }
}
