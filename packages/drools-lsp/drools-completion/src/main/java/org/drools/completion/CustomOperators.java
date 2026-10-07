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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.drools.drl.parser.impl.Operator;

/**
 * The project's custom DRL operators, read from where the engine reads them:
 * the {@code drools.evaluator.<id>} keys of {@code META-INF/kie.properties.conf}
 * and {@code META-INF/kie.default.properties.conf} on its classpath
 * ({@code org.kie.internal.utils.ChainedProperties}). The engine instantiates
 * each configured class, whose static initializer registers the operator; only
 * the keys are read here, so no project code runs in the server.
 *
 * <p>Each id is registered in the parser's {@link Operator} cache, which the
 * DRL10 grammar consults through {@code isPluggableEvaluator} before accepting
 * {@code ##id} — so a custom operator parses in the editor as it does in the
 * engine. Operators registered from Java rather than configured, and ids a
 * definition registers beyond its property key, are not seen.
 */
public final class CustomOperators {

    private static final Logger logger = Logger.getLogger(CustomOperators.class.getName());

    static final String PROPERTY_PREFIX = "drools.evaluator.";

    static final String[] CONF_FILES = {
        "META-INF/kie.properties.conf", "META-INF/kie.default.properties.conf"};

    /** Legacy keys the engine's own configuration skips as well. */
    private static final Set<String> IGNORED_IDS = Set.of("equality", "comparable");

    private CustomOperators() {
    }

    /** Registers every operator id declared on {@code classpathEntries}; returns the ids found. */
    public static Set<String> register(Set<Path> classpathEntries) {
        Set<String> ids = discover(classpathEntries);
        for (String id : ids) {
            Operator.addOperatorToRegistry(id, false);
            Operator.addOperatorToRegistry(id, true);
        }
        return ids;
    }

    /** Whether {@code id} is an operator the parser accepts after {@code ##}. */
    public static boolean isRegistered(String id) {
        return id != null && Operator.determineOperator(id, false) != null;
    }

    /** The operator ids declared on {@code classpathEntries}, without registering them. */
    static Set<String> discover(Set<Path> classpathEntries) {
        Set<String> ids = new TreeSet<>();
        if (classpathEntries == null) {
            return ids;
        }
        for (Path entry : classpathEntries) {
            for (String confFile : CONF_FILES) {
                try {
                    Properties properties = load(entry, confFile);
                    if (properties != null) {
                        collectIds(properties, ids);
                    }
                } catch (IOException | RuntimeException e) {
                    logger.log(Level.FINE, "Skipping " + confFile + " in " + entry, e);
                }
            }
        }
        return ids;
    }

    private static Properties load(Path entry, String confFile) throws IOException {
        if (Files.isDirectory(entry)) {
            Path file = entry.resolve(confFile);
            if (!Files.isRegularFile(file)) {
                return null;
            }
            try (InputStream in = Files.newInputStream(file)) {
                return read(in);
            }
        }
        if (Files.isRegularFile(entry) && entry.toString().endsWith(".jar")) {
            try (ZipFile jar = new ZipFile(entry.toFile())) {
                ZipEntry zipEntry = jar.getEntry(confFile);
                if (zipEntry == null) {
                    return null;
                }
                try (InputStream in = jar.getInputStream(zipEntry)) {
                    return read(in);
                }
            }
        }
        return null;
    }

    private static Properties read(InputStream in) throws IOException {
        Properties properties = new Properties();
        properties.load(in);
        return properties;
    }

    private static void collectIds(Properties properties, Set<String> into) {
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith(PROPERTY_PREFIX)) {
                String id = key.substring(PROPERTY_PREFIX.length()).trim();
                if (!id.isEmpty() && !IGNORED_IDS.contains(id)) {
                    into.add(id);
                }
            }
        }
    }
}
