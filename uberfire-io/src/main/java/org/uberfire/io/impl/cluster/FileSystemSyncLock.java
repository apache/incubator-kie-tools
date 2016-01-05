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

package org.uberfire.io.impl.cluster;

import java.util.HashMap;
import java.util.Map;

import org.uberfire.commons.cluster.LockExecuteNotifyAsyncReleaseTemplate;
import org.uberfire.commons.message.MessageType;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.file.FileSystem;

import static org.uberfire.io.impl.cluster.ClusterMessageType.*;

public class FileSystemSyncLock<V> extends LockExecuteNotifyAsyncReleaseTemplate<V> {

    private final String serviceId;
    private final String scheme;
    private final String id;
    private final String uri;

    public FileSystemSyncLock( final String serviceId,
                               final FileSystem _fileSystem ) {
        final FileSystem fileSystem = _fileSystem.getRootDirectories().iterator().next().getFileSystem();
        this.serviceId = serviceId;
        this.scheme = fileSystem.getRootDirectories().iterator().next().toUri().getScheme();
        this.id = ( (FileSystemId) fileSystem ).id();
        this.uri = fileSystem.toString();
    }

    @Override
    public MessageType getMessageType() {
        return SYNC_FS;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public Map<String, String> buildContent() {
        return new HashMap<String, String>() {{
            put( "fs_scheme", scheme );
            put( "fs_id", id );
            put( "fs_uri", uri );
        }};
    }
}
