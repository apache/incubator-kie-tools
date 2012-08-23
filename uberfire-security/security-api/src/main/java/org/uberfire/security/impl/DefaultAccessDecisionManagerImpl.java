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
import org.uberfire.security.annotations.AllRoles;
import org.uberfire.security.annotations.AnyRole;
import org.uberfire.security.authz.AccessDecisionManager;
import org.uberfire.security.authz.BasicRestrictedAccess;
import org.uberfire.security.authz.RestrictedAccess;
import org.uberfire.security.authz.Role;
import org.uberfire.security.authz.SimpleRestrictedAccess;

public class DefaultAccessDecisionManagerImpl implements AccessDecisionManager {

    private final Principal principal;

    @Inject
    public DefaultAccessDecisionManagerImpl(final Principal principal) {
        this.principal = principal;
    }

    @Override
    public boolean grantAccess(final RestrictedAccess object) {
        if (object instanceof SimpleRestrictedAccess) {
            return grantAccess((SimpleRestrictedAccess) object);
        } else if (object instanceof BasicRestrictedAccess) {
            return grantAccess((BasicRestrictedAccess) object);
        } else {
            throw new IllegalArgumentException("RestrictedAccess type not supported.");
        }
    }

//    @Override
//    public boolean grantAccess(final RestrictedAccess type) {
////        if (principal == null) {
////            return true;
////        }
////        if (roles == null || roles.length == 0) {
////            return true;
////        }
////        boolean hasRole = false;
////        roles:
////        for (final Role mandatoryRole : roles) {
////            for (final Role role : principal.getRoles()) {
////                if (role.equals(mandatoryRole)) {
////                    hasRole = true;
////                    break roles;
////                }
////            }
////        }
////        return hasRole;
//    }

    @Override
    public boolean denyAccess(final RestrictedAccess object) {
        return !grantAccess(object);
    }

    private boolean grantAccess(final SimpleRestrictedAccess object) {
        if (principal == null || object == null || object.getRestrictedType() == null ||
                object.getRoles() == null || object.getRoles().length == 0) {
            return true;
        }

        boolean grant = false;
        if (AnyRole.class.getName().equals(object.getRestrictedType())) {
            any_role:
            for (final String mandatoryRole : object.getRoles()) {
                for (final Role role : principal.getRoles()) {
                    if (role.getName().equals(mandatoryRole)) {
                        grant = true;
                        break any_role;
                    }
                }
            }
        } else if (AllRoles.class.getName().equals(object.getRestrictedType())) {
//            all_roles:
//            for (final String mandatoryRole : object.getRoles()) {
//                for (final Role role : principal.getRoles()) {
//                    if (role.getName().equals(mandatoryRole)) {
//                        grant = true;
//                        break all_roles;
//                    }
//                }
//            }
            return false;
        }

        return grant;
    }

    private boolean grantAccess(final BasicRestrictedAccess object) {
        return false;
    }

}
