/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.completion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ClassIndex {

    private static final Logger logger = Logger.getLogger(ClassIndex.class.getName());

    private final Map<String, List<String>> index;

    private ClassIndex(Map<String, List<String>> index) {
        this.index = index;
    }

    public static ClassIndex empty() {
        return new ClassIndex(new HashMap<>());
    }

    public static ClassIndex build(Set<Path> classpathEntries) {
        Map<String, List<String>> index = new HashMap<>();
        for (Path entry : classpathEntries) {
            if (Files.isDirectory(entry)) {
                scanDirectory(entry, index);
            } else if (entry.toString().endsWith(".jar") && Files.isRegularFile(entry)) {
                scanJar(entry, index);
            }
        }
        return new ClassIndex(index);
    }

    public static ClassIndex merge(ClassIndex base, ClassIndex overlay) {
        Map<String, List<String>> merged = new HashMap<>(base.index);
        for (Map.Entry<String, List<String>> entry : overlay.index.entrySet()) {
            merged.merge(entry.getKey(), entry.getValue(), (a, b) -> {
                List<String> combined = new ArrayList<>(a);
                combined.addAll(b);
                return combined;
            });
        }
        return new ClassIndex(merged);
    }

    public List<String> getAll() {
        return getMatching("");
    }

    /**
     * The simple names of every indexed type (the index keys). Used as a typo
     * suggestion pool for the unknown-type lint, so a misspelled classpath type
     * can be matched to the real one. Returned as an unmodifiable view over the
     * (effectively immutable) index to avoid copying the key set on every call.
     */
    public Set<String> simpleNames() {
        return java.util.Collections.unmodifiableSet(index.keySet());
    }

    public List<String> getMatching(String prefix) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : index.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                result.addAll(entry.getValue());
            }
        }
        result.sort(String::compareTo);
        return result;
    }

    public int size() {
        return index.values().stream().mapToInt(List::size).sum();
    }

    private static void scanDirectory(Path dir, Map<String, List<String>> index) {
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.filter(p -> p.toString().endsWith(".class"))
                .forEach(p -> {
                    String relative = dir.relativize(p).toString();
                    addClassEntry(relative, index);
                });
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to scan directory: " + dir, e);
        }
    }

    private static void scanJar(Path jarPath, Map<String, List<String>> index) {
        try (JarFile jar = new JarFile(jarPath.toFile())) {
            jar.stream()
                .map(JarEntry::getName)
                .filter(name -> name.endsWith(".class"))
                .forEach(name -> addClassEntry(name, index));
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to scan JAR: " + jarPath, e);
        }
    }

    private static void addClassEntry(String path, Map<String, List<String>> index) {
        String fqcn = path.replace('/', '.').replace('\\', '.');
        if (fqcn.endsWith(".class")) {
            fqcn = fqcn.substring(0, fqcn.length() - ".class".length());
        }

        String simpleName = fqcn.contains(".") ? fqcn.substring(fqcn.lastIndexOf('.') + 1) : fqcn;
        if (simpleName.contains("$") || simpleName.equals("module-info") || simpleName.equals("package-info")) {
            return;
        }

        index.computeIfAbsent(simpleName, k -> new ArrayList<>()).add(fqcn);
    }
}
