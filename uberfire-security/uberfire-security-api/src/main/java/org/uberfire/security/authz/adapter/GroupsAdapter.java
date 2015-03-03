package org.uberfire.security.authz.adapter;

import java.util.List;

import javax.security.auth.Subject;

import org.jboss.errai.security.shared.api.Group;

public interface GroupsAdapter {

    List<Group> getGroups(final String principalName, final Object subject);
}
