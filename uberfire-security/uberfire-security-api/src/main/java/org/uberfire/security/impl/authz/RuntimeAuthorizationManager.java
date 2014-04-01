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

package org.uberfire.security.impl.authz;

import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.security.authz.AuthorizationResult.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.exception.UnauthorizedException;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.RoleDecisionManager;

@ApplicationScoped
@Alternative
public class RuntimeAuthorizationManager implements AuthorizationManager {

    private final RuntimeResourceManager resourceManager = new RuntimeResourceManager();
    private final RuntimeResourceDecisionManager decisionManager = new RuntimeResourceDecisionManager( resourceManager );
    private final RoleDecisionManager roleDecisionManager = new DefaultRoleDecisionManager();

    @Override
    public boolean supports( final Resource resource ) {
        return resourceManager.supports( resource );
    }

    @Override
    public boolean authorize( final Resource resource,
                              final User user )
            throws UnauthorizedException {
        if ( !resourceManager.requiresAuthentication( resource ) ) {
            return true;
        }

        checkNotNull( "subject", user );

        final AuthorizationResult finalResult = decisionManager.decide( resource, user, roleDecisionManager );

        if ( finalResult.equals( ACCESS_ABSTAIN ) || finalResult.equals( ACCESS_GRANTED ) ) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
      return "RuntimeAuthorizationManager [resourceManager=" + resourceManager + ", decisionManager=" + decisionManager
              + ", roleDecisionManager=" + roleDecisionManager + "]";
    }

}
