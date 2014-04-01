/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.security.server.authz;

import java.util.List;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceManager;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.ResourceDecisionManager;
import org.uberfire.security.authz.RoleDecisionManager;
import org.uberfire.security.authz.VotingStrategy;
import org.uberfire.security.impl.authz.ConsensusBasedVoter;
import org.uberfire.security.impl.authz.RolesResourceImpl;
import org.uberfire.security.server.URLResource;
import org.uberfire.security.server.URLResourceManager;

import static org.uberfire.commons.validation.Preconditions.*;
import static org.uberfire.security.authz.AuthorizationResult.*;

public class URLAccessDecisionManager implements ResourceDecisionManager {

    private static final VotingStrategy DEFAULT_VOTER = new ConsensusBasedVoter();

    private final URLResourceManager resourceManager;

    public URLAccessDecisionManager(final ResourceManager resourceManager) {
        this.resourceManager = checkInstanceOf("resourceManager", resourceManager, URLResourceManager.class);
    }

    @Override
    public boolean supports(Resource resource) {
        if (resource == null) {
            return false;
        }
        if (resource instanceof URLResource) {
            return true;
        }
        return false;
    }

    @Override
    public AuthorizationResult decide(final Resource resource, final User user, final RoleDecisionManager roleDecisionManager) {
        final URLResource urlResource = checkInstanceOf("resource", resource, URLResource.class);
        final List<Role> roles = resourceManager.getMandatoryRoles(urlResource);

        if (roles == null || roles.isEmpty()) {
            return ACCESS_ABSTAIN;
        }

        return DEFAULT_VOTER.vote(roleDecisionManager.decide(new RolesResourceImpl(roles), user));
    }
}
