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

package org.uberfire.backend.vfs.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

/**
 * Represents the result of a lock request and holds the last read
 * {@link LockInfo}.
 */
@Portable
public class LockResult {

    private final boolean success;
    private final LockInfo lockInfo;

    public LockResult( @MapsTo("success") boolean success,
                       @MapsTo("lockInfo") LockInfo lockInfo ) {

        this.success = success;
        this.lockInfo = lockInfo;
    }

    public boolean isSuccess() {
        return success;
    }

    public LockInfo getLockInfo() {
        return lockInfo;
    }

    public static LockResult acquired( final Path path,
                                       final String lockedBy ) {

        return new LockResult( true,
                               new LockInfo( true,
                                             lockedBy,
                                             path ) );

    }

    public static LockResult released( final Path path ) {

        return new LockResult( true,
                               new LockInfo( false,
                                             null,
                                             path ) );
    }

    public static LockResult failed( final LockInfo lockInfo ) {

        return new LockResult( false,
                               lockInfo );
    }
    
    public static LockResult error() {
        return new LockResult(false, null);
    }

}
