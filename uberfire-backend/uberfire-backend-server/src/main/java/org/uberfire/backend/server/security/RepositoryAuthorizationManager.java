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

import javax.enterprise.inject.Alternative;

import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.repositories.RepositoryServiceImpl;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.security.FileSystemResourceAdaptor;
import org.uberfire.security.Resource;
import org.uberfire.security.Subject;
import org.uberfire.security.authz.AuthorizationException;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.RoleDecisionManager;
import org.uberfire.security.impl.authz.DefaultRoleDecisionManager;
import org.uberfire.security.impl.authz.RuntimeResourceDecisionManager;
import org.uberfire.security.impl.authz.RuntimeResourceManager;

import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.security.authz.AuthorizationResult.*;

@Alternative
public class RepositoryAuthorizationManager implements AuthorizationManager {

    private final RuntimeResourceDecisionManager decisionManager = new RuntimeResourceDecisionManager( new RuntimeResourceManager() );
    private final RoleDecisionManager roleDecisionManager = new DefaultRoleDecisionManager();

    private final RepositoryServiceImpl repositoryService;

    public RepositoryAuthorizationManager( final RepositoryServiceImpl repositoryService ) {
        this.repositoryService = repositoryService;
    }

    @Override
    public boolean supports( final Resource resource ) {
        return resource != null && ( resource instanceof Repository || resource instanceof FileSystem || resource instanceof FileSystemResourceAdaptor );
    }

    @Override
    public boolean authorize( final Resource resource,
                              final Subject subject ) throws AuthorizationException {
        checkNotNull( "subject", subject );

        final Repository repo;
        if ( resource instanceof FileSystem ) {
            repo = repositoryService.getRepository( (FileSystem) resource );
        } else if ( resource instanceof FileSystemResourceAdaptor ) {
            repo = repositoryService.getRepository( ( (FileSystemResourceAdaptor) resource ).getFileSystem() );
        } else {
            repo = (Repository) resource;
        }

        final AuthorizationResult finalResult = decisionManager.decide( repo, subject, roleDecisionManager );

        if ( finalResult.equals( ACCESS_ABSTAIN ) || finalResult.equals( ACCESS_GRANTED ) ) {
            return true;
        }

        return false;
    }
}
