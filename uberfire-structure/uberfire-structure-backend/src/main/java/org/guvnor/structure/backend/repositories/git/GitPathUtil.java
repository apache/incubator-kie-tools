/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.backend.repositories.git;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitPathUtil {

    private static final Pattern branchNameExtractorFromPath = Pattern.compile("^[A-Za-z]+://([^@]+)@.*");
    private static final Pattern branchNameExtractorFromRef = Pattern.compile("^refs/(?:heads|remotes/[^/]+)/(.*)");

    public static Optional<String> extractBranch(final String uri) {
        final Matcher matcher = branchNameExtractorFromPath.matcher(uri);
        if (matcher.matches()) {
            return Optional.of(matcher.group(1));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<String> extractBranchFromRef(final String refName) {
        final Matcher matcher = branchNameExtractorFromRef.matcher(refName);
        if (matcher.matches()) {
            return Optional.of(matcher.group(1));
        } else {
            return Optional.empty();
        }
    }
}
