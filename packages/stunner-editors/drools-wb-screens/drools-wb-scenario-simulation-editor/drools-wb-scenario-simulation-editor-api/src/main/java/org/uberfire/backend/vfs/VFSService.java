/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.uberfire.backend.vfs;

import java.util.Map;

public interface VFSService {

    Path get(String uri);

    DirectoryStream<Path> newDirectoryStream(final Path dir)
            throws IllegalArgumentException;

    DirectoryStream<Path> newDirectoryStream(final Path dir,
                                             final DirectoryStream.Filter<Path> filter)
            throws IllegalArgumentException;

    Path createDirectory(final Path dir)
            throws IllegalArgumentException, UnsupportedOperationException;

    Path createDirectories(final Path dir)
            throws UnsupportedOperationException;

    Path createDirectory(final Path dir,
                         final Map<String, ?> attrs)
            throws IllegalArgumentException, UnsupportedOperationException;

    Path createDirectories(final Path dir,
                           final Map<String, ?> attrs)
            throws UnsupportedOperationException;

    Map<String, Object> readAttributes(final Path path)
            throws UnsupportedOperationException, IllegalArgumentException;

    void setAttributes(final Path path,
                       final Map<String, Object> attrs)
            throws IllegalArgumentException;

    void delete(final Path path)
            throws IllegalArgumentException;

    boolean deleteIfExists(final Path path)
            throws IllegalArgumentException;

    Path copy(final Path source,
              final Path target)
            throws UnsupportedOperationException;

    Path move(final Path source,
              final Path target)
            throws UnsupportedOperationException;

    String readAllString(final Path path)
            throws IllegalArgumentException;

    Path write(final Path path,
               final String content)
            throws IllegalArgumentException;

    Path write(final Path path,
               final String content,
               final Map<String, ?> attrs)
            throws IllegalArgumentException;

    boolean isRegularFile(final String uri);

    boolean isRegularFile(final Path path);

    boolean isDirectory(final String uri);

    boolean isDirectory(final Path path);
}
