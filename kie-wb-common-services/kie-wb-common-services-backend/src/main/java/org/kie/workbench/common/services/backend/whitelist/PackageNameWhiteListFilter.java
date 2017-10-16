/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.whitelist;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kie.workbench.common.services.backend.file.AntPathMatcher;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;

import static org.kie.soup.commons.validation.PortablePreconditions.*;

public class PackageNameWhiteListFilter {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private final Collection<String> packageNames;
    private final Set<String> patterns;

    public PackageNameWhiteListFilter(final Collection<String> packageNames,
                                      final WhiteList whiteList) {

        this.packageNames = checkNotNull("packageNames",
                                         packageNames);
        checkNotNull("whitelist",
                     whiteList);
        this.patterns = makePatterns(whiteList);
    }

    private Set<String> makePatterns(final WhiteList whiteList) {
        Set<String> patterns = new HashSet<String>();

        //Convert to Paths as we're delegating to an Ant-style pattern matcher.
        //Convert once outside of the nested loops for performance reasons.
        for (String packageName : whiteList) {
            patterns.add(packageName.replaceAll("\\.",
                                                AntPathMatcher.DEFAULT_PATH_SEPARATOR));
        }
        return patterns;
    }

    /**
     * @return Package Names matching the White List to the available packages
     */
    public WhiteList getFilteredPackageNames() {
        final WhiteList whiteList = new WhiteList();

        final Map<String, String> packageNamePatterns = getPatterns();

        for (String pattern : patterns) {
            for (Map.Entry<String, String> packageNamePath : packageNamePatterns.entrySet()) {
                if (ANT_PATH_MATCHER.match(pattern,
                                           packageNamePath.getValue())) {
                    whiteList.add(packageNamePath.getKey());
                }
            }
        }

        return whiteList;
    }

    /**
     * Fetching the paths to a map to avoid loops inside loops
     *
     * @return
     */
    private HashMap<String, String> getPatterns() {
        final HashMap<String, String> packageNamePaths = new HashMap<String, String>();
        for (String packageName : packageNames) {
            packageNamePaths.put(packageName,
                                 packageName.replaceAll("\\.",
                                                        AntPathMatcher.DEFAULT_PATH_SEPARATOR));
        }
        return packageNamePaths;
    }
}
