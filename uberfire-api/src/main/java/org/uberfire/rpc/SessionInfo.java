package org.uberfire.rpc;

import org.uberfire.security.Identity;

public interface SessionInfo {

    String getId();

    Identity getIdentity();

}
