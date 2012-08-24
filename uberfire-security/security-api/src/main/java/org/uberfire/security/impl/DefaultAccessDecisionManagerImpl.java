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

package org.uberfire.security.impl;

import javax.inject.Inject;

import org.uberfire.security.Principal;
import org.uberfire.security.Role;
import org.uberfire.security.annotations.All;
import org.uberfire.security.authz.AccessDecisionManager;
import org.uberfire.security.authz.RestrictedAccess;
import org.uberfire.security.authz.SimpleRestrictedAccess;

public class DefaultAccessDecisionManagerImpl implements AccessDecisionManager {

    private final Principal principal;

    @Inject
    public DefaultAccessDecisionManagerImpl(final Principal principal) {
        this.principal = principal;
    }

    @Override
    public boolean accessGranted(final RestrictedAccess object) {
        if (object instanceof SimpleRestrictedAccess) {
            return grantAccess((SimpleRestrictedAccess) object);
        } else {
            throw new IllegalArgumentException("RestrictedAccess type not supported.");
        }
    }

    @Override
    public boolean accessDenied(final RestrictedAccess object) {
        return !accessGranted(object);
    }

    private boolean grantAccess(final SimpleRestrictedAccess object) {
        if (principal == null || object == null ||
                object.getRoles() == null || object.getRoles().length == 0) {
            return true;
        }

        boolean mustHaveAllRoles = containsAllTrait(object.getTraitTypes());

        boolean grant = false;
        if (mustHaveAllRoles) {
            for (final String mandatoryRole : object.getRoles()) {
                for (final Role role : principal.getRoles()) {
                    if (role.getName().equals(mandatoryRole)) {
                        grant = true;
                        break;
                    }
                }
                if (!grant) {
                    break;
                }
            }
        } else {
            any_role:
            for (final String mandatoryRole : object.getRoles()) {
                for (final Role role : principal.getRoles()) {
                    if (role.getName().equals(mandatoryRole)) {
                        grant = true;
                        break any_role;
                    }
                }
            }
        }

        return grant;
    }

    private boolean containsAllTrait(final String[] traitTypes) {
        if (traitTypes == null) {
            return false;
        }

        for (final String traitType : traitTypes) {
            if (All.class.getName().equals(traitType)) {
                return true;
            }
        }
        return false;
    }
}
