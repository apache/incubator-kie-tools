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

package org.guvnor.common.services.backend.util;

import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.rpc.SessionInfo;

@ApplicationScoped
public class CommentedOptionFactoryImpl
        implements CommentedOptionFactory {

    private static final String UNKNOWN_IDENTITY = "unknown";

    private SafeSessionInfo safeSessionInfo;

    public CommentedOptionFactoryImpl() {
    }

    @Inject
    public CommentedOptionFactoryImpl(SessionInfo safeSessionInfo) {
        this.safeSessionInfo = new SafeSessionInfo(safeSessionInfo);
    }

    @Override
    public CommentedOption makeCommentedOption(final String commitMessage) {
        new SafeSessionInfo(safeSessionInfo);
        return makeCommentedOption(commitMessage,
                                   safeSessionInfo.getIdentity(),
                                   safeSessionInfo);
    }

    @Override
    public CommentedOption makeCommentedOption(final String commitMessage,
                                               final User identity,
                                               final SessionInfo sessionInfo) {
        return new CommentedOption(new SafeSessionInfo(sessionInfo).getId(),
                                   getIdentityName(identity),
                                   null,
                                   commitMessage,
                                   new Date());
    }

    @Override
    public CommentedOption makeCommentedOption(final String sessionId,
                                               final String commitMessage) {
        return new CommentedOption(sessionId,
                                   safeSessionInfo.getIdentity().getIdentifier(),
                                   null,
                                   commitMessage,
                                   new Date());
    }

    @Override
    public String getSafeSessionId() {
        return safeSessionInfo.getId();
    }

    @Override
    public String getSafeIdentityName() {
        return safeSessionInfo.getIdentity().getIdentifier();
    }

    protected String getIdentityName(final User identity) {
        try {
            return identity.getIdentifier();
        } catch (Exception e) {
            return UNKNOWN_IDENTITY;
        }
    }
}
