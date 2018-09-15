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
package org.kie.workbench.common.services.backend.compiler.impl.classloader;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.kie.workbench.common.services.backend.compiler.impl.CommonConstants;

public class FilterPathClassesCollector implements Collector<String, Set<String>, Set<String>> {

    private static String mavenRepoPath;
    private static int mavenRepoPathLength;

    public FilterPathClassesCollector(String mavenRepoPath) {
        this.mavenRepoPath = mavenRepoPath;
        this.mavenRepoPathLength = mavenRepoPath == null? 0 : mavenRepoPath.length();
    }

    @Override
    public Supplier<Set<String>> supplier() {
        return () -> new HashSet<>();
    }

    @Override
    public BiConsumer<Set<String>, String> accumulator() {
        return (filtered, item) -> {
            if (item.endsWith(CommonConstants.JAVA_CLASS_EXT)) {
                filtered.add(getFilteredOnJavaClass(item));
            } else if (item.endsWith(CommonConstants.JAVA_ARCHIVE_RESOURCE_EXT)) {
                filtered.add(getFilteredOnJar(item, mavenRepoPath, mavenRepoPathLength));
            }
        };
    }

    @Override
    public BinaryOperator<Set<String>> combiner() {
        return (items, filtered) -> {
            filtered.addAll(items);
            return filtered;
        };
    }

    @Override
    public Function<Set<String>, Set<String>> finisher() {
        return (inputSet) -> inputSet;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.IDENTITY_FINISH);
    }

    private String getFilteredOnJavaClass(String item) {
        String path = item.substring(item.lastIndexOf(CommonConstants.MAVEN_TARGET) + CommonConstants.MAVEN_TARGET.length());
        if (path.contains(CommonConstants.SEPARATOR)) {
            return path.substring(0, path.lastIndexOf(CommonConstants.SEPARATOR)).replace(CommonConstants.SEPARATOR, CommonConstants.DOT);
        } else {
            return path.substring(0, path.lastIndexOf(CommonConstants.DOT));
        }
    }

    private String getFilteredOnJar(String item, String mavenRepoPath, int mavenRepoPathLength) {
        return item.substring(item.lastIndexOf(mavenRepoPath) + mavenRepoPathLength, item.lastIndexOf(CommonConstants.SEPARATOR)).replace(CommonConstants.SEPARATOR, CommonConstants.DOT);
    }
}
