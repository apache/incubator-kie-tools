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

package org.uberfire.java.nio.file;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

import static org.uberfire.commons.validation.Preconditions.*;

/**
 * Simple file tree walker that works in a similar manner to nftw(3C).
 * @see Files#walkFileTree
 */

class FileTreeWalker {

    private final FileVisitor<? super Path> visitor;
    private final int maxDepth;

    FileTreeWalker(final FileVisitor<? super Path> visitor, final int maxDepth) {
        this.visitor = checkNotNull("visitor", visitor);
        this.maxDepth = maxDepth;
    }

    /**
     * Walk file tree starting at the given file
     */
    void walk(final Path start) throws IOException {
        checkNotNull("start", start);
        walk(start, 0);
    }

    private FileVisitResult walk(final Path file, final int depth)
            throws IOException {
        IOException exc = null;
        BasicFileAttributes attrs = null;
        try {
            attrs = Files.readAttributes(file, BasicFileAttributes.class);
        } catch (IOException ex) {
            exc = ex;
        }

        if (exc != null) {
            return visitor.visitFileFailed(file, exc);
        }

        // at maximum depth or file is not a directory
        if (depth >= maxDepth || !attrs.isDirectory()) {
            return visitor.visitFile(file, attrs);
        }

        DirectoryStream<? extends Path> stream = null;
        FileVisitResult result = null;

        try {
            stream = Files.newDirectoryStream(file);
        } catch (IOException ex) {
            return visitor.visitFileFailed(file, ex);
        }

        IOException postException = null;

        try {
            result = visitor.preVisitDirectory(file, attrs);
            if (result != FileVisitResult.CONTINUE) {
                return result;
            }

            try {
                for (final Path entry : stream) {
                    result = walk(entry, depth + 1);
                    if (result == null || result == FileVisitResult.TERMINATE) {
                        return result;
                    }
                    if (result == FileVisitResult.SKIP_SIBLINGS) {
                        break;
                    }
                }
            } catch (IOException ex) {
                postException = ex;
            }
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {
                if (postException == null) {
                    postException = ex;
                }
            }
        }

        return visitor.postVisitDirectory(file, postException);
    }
}
