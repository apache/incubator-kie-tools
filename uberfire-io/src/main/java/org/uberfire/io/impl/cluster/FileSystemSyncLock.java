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
                               final FileSystem fileSystem ) {
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
