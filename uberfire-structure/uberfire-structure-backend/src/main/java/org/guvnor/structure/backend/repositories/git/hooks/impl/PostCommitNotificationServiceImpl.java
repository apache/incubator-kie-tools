/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.structure.backend.repositories.git.hooks.impl;

import org.guvnor.structure.backend.repositories.git.hooks.PostCommitNotificationService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.repositories.impl.git.event.NotificationType;
import org.guvnor.structure.repositories.impl.git.event.PostCommitNotificationEvent;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.SpacesAPI;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
@Startup
public class PostCommitNotificationServiceImpl implements PostCommitNotificationService {

    private SessionInfo sessionInfo;

    private Event<PostCommitNotificationEvent> notificationEvent;

    private MessageReader reader;

    PostCommitNotificationServiceImpl() {
        // CDI Proxy
    }

    @Inject
    public PostCommitNotificationServiceImpl(SessionInfo sessionInfo, Event<PostCommitNotificationEvent> notificationEvent, MessageReader reader) {
        this.sessionInfo = sessionInfo;
        this.notificationEvent = notificationEvent;
        this.reader = reader;
    }

    @PostConstruct
    public void init() {
        reader.init(System.getProperty(BUNDLE_PARAM));
    }

    @Override
    public void notifyUser(GitRepository repo, Integer exitCode) {
        if (sessionInfo != null && !SpacesAPI.DEFAULT_SPACE.equals(repo.getSpace())) {
            Optional<String> optional = reader.resolveMessage(exitCode);

            if (optional.isPresent()) {
                notificationEvent.fire(new PostCommitNotificationEvent(NotificationType.fromExitCode(exitCode), optional.get()));
            }
        }
    }
}
