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

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.rpc.SessionInfo;

public interface CommentedOptionFactory {

    CommentedOption makeCommentedOption(final String commitMessage);

    CommentedOption makeCommentedOption(final String commitMessage,
                                        final User identity,
                                        final SessionInfo sessionInfo);

    CommentedOption makeCommentedOption(final String sessionId,
                                        final String commitMessage);

    /**
     * @return Safe session id, even when SessionInfo does not exist
     */
    String getSafeSessionId();

    /**
     * @return Safe identity name, even when Identity does not exist
     */
    String getSafeIdentityName();
}
