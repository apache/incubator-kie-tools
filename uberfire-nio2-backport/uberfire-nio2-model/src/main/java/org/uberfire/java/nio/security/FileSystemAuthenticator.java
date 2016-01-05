/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.security;

/**
 * Authentication strategy used by secured file system implementations such as Git.
 */
public interface FileSystemAuthenticator {

    /**
     * Attempts to authenticate with the target filesystem using the given filesystem username and password.
     *
     * @param username
     *            the filesystem username to authenticate as. Must not be null.
     * @param password
     *            the given user's password. Can be null if the target filesystem supports null passwords.
     * @return the filesystem user if authentication was successful; null if authentication failed.
     */
    FileSystemUser authenticate( final String username,
                          final String password );
}
