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

package org.uberfire.metadata.backend.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class FileTestUtil {

    protected static final List<File> tempFiles = new ArrayList<File>();

    public static File createTempDirectory()
            throws IOException {
        final File temp = File.createTempFile( "temp", Long.toString( System.nanoTime() ) );
        if ( !( temp.delete() ) ) {
            throw new IOException( "Could not delete temp file: " + temp.getAbsolutePath() );
        }

        if ( !( temp.mkdir() ) ) {
            throw new IOException( "Could not create temp directory: " + temp.getAbsolutePath() );
        }

        tempFiles.add( temp );

        return temp;
    }

    public static void cleanup() {

    }

}
