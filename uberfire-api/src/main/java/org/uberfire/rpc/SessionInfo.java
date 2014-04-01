package org.uberfire.rpc;

import org.jboss.errai.security.shared.api.identity.User;

public interface SessionInfo {

    String getId();

    User getIdentity();

}
