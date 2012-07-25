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

package org.uberfire.java.nio.file;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.FileVisitor;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

import static org.uberfire.java.nio.util.Preconditions.*;

public class SimpleFileVisitor<T> implements FileVisitor<T> {

    protected SimpleFileVisitor() {
    }

    @Override
    public FileVisitResult preVisitDirectory(T dir, BasicFileAttributes attrs)
            throws IOException {
        checkNotNull("dir", dir);
        checkNotNull("attrs", attrs);

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(T file, BasicFileAttributes attrs)
            throws IOException {
        checkNotNull("file", file);
        checkNotNull("attrs", attrs);

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(T file, IOException exc)
            throws IOException {
        checkNotNull("file", file);

        throw exc;
    }

    @Override
    public FileVisitResult postVisitDirectory(T dir, IOException exc)
            throws IOException {
        checkNotNull("dir", dir);

        if (exc != null) {
            throw exc;
        }
        return FileVisitResult.CONTINUE;
    }
}
