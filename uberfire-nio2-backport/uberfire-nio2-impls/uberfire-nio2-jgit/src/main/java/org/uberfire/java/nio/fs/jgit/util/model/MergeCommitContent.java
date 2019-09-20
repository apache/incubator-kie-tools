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

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.revwalk.RevCommit;

public class MergeCommitContent extends DefaultCommitContent {

    private final List<RevCommit> parents;

    public MergeCommitContent(final Map<String, File> content,
                              final List<RevCommit> parents) {
        super(content);

        this.parents = parents;
    }

    public List<RevCommit> getParents() {
        return parents;
    }
}
