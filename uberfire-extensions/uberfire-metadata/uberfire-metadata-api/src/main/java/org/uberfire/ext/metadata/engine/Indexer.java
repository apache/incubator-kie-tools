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
package org.uberfire.ext.metadata.engine;

import org.uberfire.java.nio.file.Path;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KObjectKey;

public interface Indexer {

    /**
     * Different Indexers can handle different file-types
     * @param path
     * @return
     */
    boolean supportsPath( final Path path );

    /**
     * Index file represented by Path into an index object
     * @param path
     * @return
     */
    KObject toKObject( final Path path );

    /**
     * Index file represented by Path into an index key object
     * @param path
     * @return
     */
    KObjectKey toKObjectKey( final Path path );

}
