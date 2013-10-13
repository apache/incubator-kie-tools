package org.uberfire.io.impl.cluster;

import java.util.HashMap;
import java.util.Map;

import org.uberfire.commons.cluster.LockExecuteNotifyAsyncReleaseTemplate;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.commons.message.MessageType;

import static org.uberfire.io.impl.cluster.ClusterMessageType.*;

public class FileSystemSyncLock<V> extends LockExecuteNotifyAsyncReleaseTemplate<V> {

    private final FileSystem fileSystem;

    public FileSystemSyncLock( final FileSystem fileSystem ) {
        this.fileSystem = fileSystem;
    }

    @Override
    public MessageType getMessageType() {
        return SYNC_FS;
    }

    @Override
    public Map<String, String> buildContent() {
        return new HashMap<String, String>() {{
            put( "fs_scheme", fileSystem.getRootDirectories().iterator().next().toUri().getScheme() );
            put( "fs_id", ( (FileSystemId) fileSystem ).id() );
            put( "fs_uri", fileSystem.toString() );
        }};
    }
}
