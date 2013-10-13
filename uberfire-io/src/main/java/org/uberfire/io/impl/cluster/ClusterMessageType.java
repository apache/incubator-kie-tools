package org.uberfire.io.impl.cluster;

import org.uberfire.commons.message.MessageType;

public enum ClusterMessageType implements MessageType {
    NEW_FS, SYNC_FS, QUERY_FOR_FS, QUERY_FOR_FS_RESULT;
}
