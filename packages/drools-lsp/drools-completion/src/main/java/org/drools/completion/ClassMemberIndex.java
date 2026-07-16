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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lazily reflects the members of classes on the resolved project classpath
 * (the same entries {@link ClassIndex} is built from), so completion can
 * offer field and bean-property names for pattern types.
 *
 * <p>Classes are loaded with {@code Class.forName(fqcn, false, loader)} —
 * <b>static initializers never run</b>, so member lookup cannot execute
 * arbitrary user code inside the language server. The loader's parent is the
 * platform class loader, so the server's own classes never leak into user
 * completions. Results (including misses) are cached per FQCN.
 */
public final class ClassMemberIndex implements AutoCloseable {

    private static final Logger logger = Logger.getLogger(ClassMemberIndex.class.getName());

    private static final ClassMemberIndex EMPTY = new ClassMemberIndex((ClassLoader) null);

    private final ClassLoader loader;
    private final boolean ownsLoader;
    private final Map<String, List<Field>> cache = new ConcurrentHashMap<>();

    /** Visible for tests: reflect against an existing (externally-owned) loader. */
    ClassMemberIndex(ClassLoader loader) {
        this(loader, false);
    }

    private ClassMemberIndex(ClassLoader loader, boolean ownsLoader) {
        this.loader = loader;
        this.ownsLoader = ownsLoader;
    }

    /** An index that resolves nothing. */
    public static ClassMemberIndex empty() {
        return EMPTY;
    }

    /** Builds an index over the given classpath entries (jars and class dirs). */
    public static ClassMemberIndex of(Set<Path> classpathEntries) {
        if (classpathEntries == null || classpathEntries.isEmpty()) {
            return EMPTY;
        }
        List<URL> urls = new ArrayList<>(classpathEntries.size());
        for (Path entry : classpathEntries) {
            try {
                urls.add(entry.toUri().toURL());
            } catch (Exception e) {
                logger.log(Level.FINE, "Skipping classpath entry " + entry, e);
            }
        }
        URLClassLoader loader = new URLClassLoader(
                urls.toArray(new URL[0]), ClassLoader.getPlatformClassLoader());
        return new ClassMemberIndex(loader, true);
    }

    @Override
    public void close() {
        cache.clear();
        if (ownsLoader && loader instanceof java.io.Closeable closeable) {
            try {
                closeable.close();
            } catch (java.io.IOException e) {
                logger.log(Level.FINE, "Failed to close class member index loader", e);
            }
        }
    }

    /**
     * Returns the completable members of {@code fqcn}: enum constants, bean
     * properties derived from public no-arg {@code getX()}/{@code isX()}
     * methods (including inherited ones), and public instance fields. Empty
     * when the class cannot be loaded.
     */
    public List<Field> membersOf(String fqcn) {
        if (loader == null || fqcn == null || fqcn.isEmpty()) {
            return Collections.emptyList();
        }
        return cache.computeIfAbsent(fqcn, this::reflectMembers);
    }

    /**
     * Returns the fully-qualified names of {@code fqcn}'s direct supertypes —
     * its superclass (omitting {@code java.lang.Object}) followed by its
     * directly-implemented interfaces. Loaded without running static
     * initializers; empty when the class can't be loaded. Used by type
     * hierarchy to walk classpath ancestry one level at a time.
     */
    public List<String> supertypesOf(String fqcn) {
        if (loader == null || fqcn == null || fqcn.isEmpty()) {
            return Collections.emptyList();
        }
        Class<?> clazz;
        try {
            clazz = Class.forName(fqcn, false, loader);
        } catch (Throwable t) {
            return Collections.emptyList();
        }
        try {
            List<String> out = new ArrayList<>();
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null && superclass != Object.class) {
                out.add(superclass.getName());
            }
            for (Class<?> iface : clazz.getInterfaces()) {
                out.add(iface.getName());
            }
            return Collections.unmodifiableList(out);
        } catch (Throwable t) {
            logger.log(Level.FINE, "Failed to reflect supertypes of " + fqcn, t);
            return Collections.emptyList();
        }
    }

    /**
     * The names addressable as {@code Type.X} on {@code fqcn}: public fields
     * (including enum constants and static fields, inherited included) and
     * public nested-type simple names. Returns {@code null} when the class
     * cannot be loaded — letting callers distinguish "no such member" (a real
     * typo) from "couldn't verify" (classpath gap), so they only flag the
     * former. Loaded without running static initializers.
     */
    public Set<String> memberNames(String fqcn) {
        if (loader == null || fqcn == null || fqcn.isEmpty()) {
            return null;
        }
        Class<?> clazz;
        try {
            clazz = Class.forName(fqcn, false, loader);
        } catch (Throwable t) {
            return null;
        }
        try {
            Set<String> names = new LinkedHashSet<>();
            for (java.lang.reflect.Field f : clazz.getFields()) {
                names.add(f.getName());
            }
            for (Class<?> nested : clazz.getClasses()) {
                names.add(nested.getSimpleName());
            }
            return names;
        } catch (Throwable t) {
            logger.log(Level.FINE, "Failed to reflect member names of " + fqcn, t);
            return null;
        }
    }

    private List<Field> reflectMembers(String fqcn) {
        Class<?> clazz;
        try {
            clazz = Class.forName(fqcn, false, loader);
        } catch (Throwable t) {
            return Collections.emptyList();
        }
        try {
            Map<String, Field> members = new LinkedHashMap<>();
            if (clazz.isEnum()) {
                for (java.lang.reflect.Field f : clazz.getFields()) {
                    if (f.isEnumConstant()) {
                        members.put(f.getName(),
                                new Field(f.getName(), clazz.getSimpleName()));
                    }
                }
            }
            for (Method m : clazz.getMethods()) {
                String property = propertyNameOf(m);
                if (property != null) {
                    members.putIfAbsent(property,
                            new Field(property, m.getReturnType().getSimpleName()));
                }
            }
            for (java.lang.reflect.Field f : clazz.getFields()) {
                if (!Modifier.isStatic(f.getModifiers())) {
                    members.putIfAbsent(f.getName(),
                            new Field(f.getName(), f.getType().getSimpleName()));
                }
            }
            return Collections.unmodifiableList(new ArrayList<>(members.values()));
        } catch (Throwable t) {
            // LinkageError etc. while reflecting — treat as unknown.
            logger.log(Level.FINE, "Failed to reflect members of " + fqcn, t);
            return Collections.emptyList();
        }
    }

    /**
     * Maps a public no-arg {@code getX()}/{@code isX()} method to its bean
     * property name (JavaBeans decapitalize rule), or returns {@code null}
     * for non-accessors and {@code getClass()}.
     */
    private static String propertyNameOf(Method m) {
        if (m.getParameterCount() != 0 || m.getReturnType() == void.class
                || Modifier.isStatic(m.getModifiers())) {
            return null;
        }
        String name = m.getName();
        String raw;
        if (name.startsWith("get") && name.length() > 3 && Character.isUpperCase(name.charAt(3))) {
            if ("getClass".equals(name)) {
                return null;
            }
            raw = name.substring(3);
        } else if (name.startsWith("is") && name.length() > 2 && Character.isUpperCase(name.charAt(2))) {
            Class<?> returnType = m.getReturnType();
            if (returnType != boolean.class && returnType != Boolean.class) {
                return null;
            }
            raw = name.substring(2);
        } else {
            return null;
        }
        if (raw.length() > 1 && Character.isUpperCase(raw.charAt(0)) && Character.isUpperCase(raw.charAt(1))) {
            return raw;
        }
        char[] chars = raw.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }
}
