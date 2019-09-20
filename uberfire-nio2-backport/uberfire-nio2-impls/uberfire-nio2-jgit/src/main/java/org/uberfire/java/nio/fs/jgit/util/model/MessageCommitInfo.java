/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.fs.jgit.util.model;

import java.util.Date;
import java.util.TimeZone;

public class MessageCommitInfo extends CommitInfo {

    public static final String MERGE_MESSAGE = "Merge branch '%s'";
    public static final String REVERT_MERGE_MESSAGE = "Revert merge from branch '%s'";
    public static final String FIX_REVERT_MERGE_MESSAGE = "Fix after merge reversion";

    public MessageCommitInfo(final String message) {
        this(null,
             null,
             null,
             message,
             null,
             null);
    }

    private MessageCommitInfo(final String sessionId,
                              final String name,
                              final String email,
                              final String message,
                              final TimeZone timeZone,
                              final Date when) {
        super(sessionId,
              name,
              email,
              message,
              timeZone,
              when);
    }

    public static MessageCommitInfo createMergeMessage(final String sourceBranch) {
        return new MessageCommitInfo(String.format(MERGE_MESSAGE, sourceBranch));
    }

    public static MessageCommitInfo createRevertMergeMessage(final String sourceBranch) {
        return new MessageCommitInfo(String.format(REVERT_MERGE_MESSAGE, sourceBranch));
    }

    public static MessageCommitInfo createFixMergeReversionMessage() {
        return new MessageCommitInfo(FIX_REVERT_MERGE_MESSAGE);
    }
}
