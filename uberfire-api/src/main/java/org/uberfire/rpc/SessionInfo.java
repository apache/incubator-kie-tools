package org.uberfire.rpc;

import org.uberfire.security.Subject;

public interface SessionInfo {

    String getId();

    Subject getIdentity();

}
