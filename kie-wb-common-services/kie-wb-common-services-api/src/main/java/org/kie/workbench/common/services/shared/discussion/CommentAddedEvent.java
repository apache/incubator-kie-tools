/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.shared.discussion;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Portable
public class CommentAddedEvent {

    private String userName;
    private String comment;
    private Long timestamp;
    private Path path;

    public CommentAddedEvent() {
    }

    public CommentAddedEvent(
            String userName,
            Path path,
            String comment,
            Long timestamp) {
        this.path = checkNotNull("path", path);
        this.userName = checkNotNull("userName", userName);
        this.comment = checkNotNull("comment", comment);
        this.timestamp = checkNotNull("timestamp", timestamp);
    }

    public String getUserName() {
        return userName;
    }

    public String getComment() {
        return comment;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Path getPath() {
        return path;
    }
}
