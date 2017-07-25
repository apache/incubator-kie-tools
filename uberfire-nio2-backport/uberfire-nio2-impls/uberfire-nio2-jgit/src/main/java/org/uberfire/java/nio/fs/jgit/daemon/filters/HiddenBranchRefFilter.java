/*
 * 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.fs.jgit.daemon.filters;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.RefFilter;

/**
 * This RefFilter is used to exclude hidden branches
 * from {@link org.eclipse.jgit.transport.UploadPack}.
 * Check {@link org.uberfire.java.nio.fs.jgit.daemon.git.Daemon}
 */
public class HiddenBranchRefFilter implements RefFilter {

    private static final String HIDDEN_BRANCH_REGEXP = "PR-\\d+-\\S+-\\S+";
    private static Pattern pattern = Pattern.compile(HIDDEN_BRANCH_REGEXP);

    @Override
    public Map<String, Ref> filter(final Map<String, Ref> refs) {
        return refs.entrySet()
                .stream()
                .filter(ref -> !HiddenBranchRefFilter.isHidden(ref.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey,
                                          Map.Entry::getValue));
    }

    /**
     * Checks if a branch name matches the hidden branch regexp
     * @param branch the branch you want to check.
     * @return return if the branch is hidden or not
     */
    public static boolean isHidden(String branch) {
        return pattern.matcher(branch).matches();
    }
}
