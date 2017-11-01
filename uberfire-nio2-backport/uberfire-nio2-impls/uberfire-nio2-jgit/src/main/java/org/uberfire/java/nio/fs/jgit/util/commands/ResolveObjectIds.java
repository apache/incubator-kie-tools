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

package org.uberfire.java.nio.fs.jgit.util.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.uberfire.java.nio.fs.jgit.util.Git;

public class ResolveObjectIds {

    private final Git git;
    private final String[] ids;

    public ResolveObjectIds(final Git git,
                            final String... ids) {
        this.git = git;
        this.ids = ids;
    }

    public List<ObjectId> execute() {
        final List<ObjectId> result = new ArrayList<>();

        for (final String id : ids) {
            try {
                final Ref refName = git.getRef(id);
                if (refName != null) {
                    result.add(refName.getObjectId());
                    continue;
                }

                try {
                    final ObjectId _id = ObjectId.fromString(id);
                    if (git.getRepository().getObjectDatabase().has(_id)) {
                        result.add(_id);
                    }
                } catch (final IllegalArgumentException ignored) {
                }
            } catch (final java.io.IOException ignored) {
            }
        }

        return result;
    }
}
