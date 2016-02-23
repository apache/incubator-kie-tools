/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.editor.commons.backend.service.restriction;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSLockService;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.ext.editor.commons.service.restriction.PathOperationRestriction;
import org.uberfire.ext.editor.commons.service.restrictor.DeleteRestrictor;
import org.uberfire.ext.editor.commons.service.restrictor.RenameRestrictor;

@ApplicationScoped
public class LockRestrictor implements DeleteRestrictor,
                                       RenameRestrictor {

    @Inject
    private VFSLockService lockService;

    @Inject
    private User identity;

    @Override
    public PathOperationRestriction hasRestriction( final Path path ) {
        final LockInfo lockInfo = lockService.retrieveLockInfo( path );
        if ( lockInfo != null && lockInfo.isLocked() && !identity.getIdentifier().equals( lockInfo.lockedBy() ) ) {
            return new PathOperationRestriction() {
                @Override
                public String getMessage( final Path path ) {
                    return path.toURI() + " cannot be deleted, moved or renamed. It is locked by: " + lockInfo.lockedBy();
                }
            };
        }

        final List<LockInfo> lockInfos = lockService.retrieveLockInfos( path, true );
        if ( lockInfos != null && !lockInfos.isEmpty() ) {
            return new PathOperationRestriction() {
                @Override
                public String getMessage( final Path path ) {
                    return path.toURI() + " cannot be deleted, moved or renamed. It contains the following locked files: " + lockInfos;
                }
            };
        }

        return null;
    }
}
