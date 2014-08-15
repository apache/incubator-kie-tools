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

package org.uberfire.backend.server.security;

import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.security.authz.AuthorizationResult.*;

import javax.enterprise.inject.Alternative;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.exception.UnauthorizedException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.RoleDecisionManager;
import org.uberfire.security.impl.authz.DefaultRoleDecisionManager;
import org.uberfire.security.impl.authz.RuntimeResourceDecisionManager;
import org.uberfire.security.impl.authz.RuntimeResourceManager;

@Alternative
public class FileSystemAuthorizationManager implements AuthorizationManager {

    private final RuntimeResourceDecisionManager decisionManager = new RuntimeResourceDecisionManager( new RuntimeResourceManager() );
    private final RoleDecisionManager roleDecisionManager = new DefaultRoleDecisionManager();

    @Override
    public boolean supports( final Resource resource ) {
        return resource != null && ( resource instanceof FileSystem || resource instanceof FileSystemResourceAdaptor );
    }

    @Override
    public boolean authorize( final Resource resource,
                              final User subject ) throws UnauthorizedException {
        checkNotNull( "subject", subject );

        final FileSystemResourceAdaptor fileSystemResource;
        if ( resource instanceof FileSystem ) {
            fileSystemResource = new FileSystemResourceAdaptor( (FileSystem) resource );
        } else {
            fileSystemResource = (FileSystemResourceAdaptor) resource;
        }

        final AuthorizationResult finalResult = decisionManager.decide( fileSystemResource, subject, roleDecisionManager );

        return finalResult.equals( ACCESS_ABSTAIN ) || finalResult.equals( ACCESS_GRANTED );
    }
}
