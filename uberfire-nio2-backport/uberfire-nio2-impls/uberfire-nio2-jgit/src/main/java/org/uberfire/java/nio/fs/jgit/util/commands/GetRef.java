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

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectIdRef;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

import static org.eclipse.jgit.lib.Constants.OBJ_TREE;

public class GetRef {

    private final Repository repo;
    private final String name;

    public GetRef(final Repository repo,
                  final String name) {
        this.repo = repo;
        this.name = name;
    }

    public Ref execute() {
        try {
            final Ref value = repo.getRefDatabase().getRef(name);
            if (value != null) {
                return value;
            }
            final ObjectId treeRef = repo.resolve(name + "^{tree}");
            if (treeRef != null) {
                try (final ObjectReader objectReader = repo.getObjectDatabase().newReader()) {
                    final ObjectLoader loader = objectReader.open(treeRef);
                    if (loader.getType() == OBJ_TREE) {
                        return new ObjectIdRef.PeeledTag(Ref.Storage.NEW,
                                                         name,
                                                         ObjectId.fromString(name),
                                                         treeRef);
                    }
                }

            }
        } catch (final Exception ignored) {
        }
        return null;
    }
}
