/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.backend.security;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.exception.UnauthorizedException;
import org.uberfire.backend.server.security.IOSecurityAuthz;
import org.uberfire.security.Resource;

import static org.kie.workbench.common.services.backend.security.KieRoles.*;

@ApplicationScoped
@IOSecurityAuthz
public class KieFileSystemAuthorizationManager extends org.uberfire.backend.server.security.FileSystemAuthorizationManager {

    @Override
    public boolean authorize( final Resource resource,
                              final User subject ) throws UnauthorizedException {
        final boolean result = super.authorize( resource, subject );

        return result && checkRole( subject );
    }

    private boolean checkRole( final User subject ) {
        if ( subject.getRoles().contains( new RoleImpl( ADMIN.toString() ) ) ||
                subject.getRoles().contains( new RoleImpl( DEVELOPER.toString() ) ) ||
                subject.getRoles().contains( new RoleImpl( ANALYST.toString() ) ) ) {
            return true;
        }
        return false;
    }
}
