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

package org.drools.lsp.server;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The project's own Drools engine, driven through the bridge class.
 *
 * <p>The project loader sees the project's classpath and the platform classes
 * only. The server's loader is deliberately not its parent: the server jar
 * carries a DRL parser and an ANTLR runtime of its own, and the project's
 * versions must win inside the engine. The bridge class is defined from this
 * server's own jar into a child of the project loader, so its references to
 * kie-api resolve against the project.
 */
final class ProjectEngine implements Engine {

    static final String BRIDGE_CLASS = "org.drools.lsp.bridge.EngineBridge";
    static final String COMPILER_CLASS = "org.drools.compiler.kie.builder.impl.KieServicesImpl";
    static final String MVEL_CLASS = "org.drools.mvel.MVELConstraint";
    private static final String BRIDGE_PACKAGE_PREFIX = "org.drools.lsp.bridge.";
    private static final Logger logger = Logger.getLogger(ProjectEngine.class.getName());

    private final Set<Path> entries;
    private final URLClassLoader projectLoader;
    private final Class<?> bridge;
    private final Method build;
    private volatile boolean closed;

    private ProjectEngine(Set<Path> entries, URLClassLoader projectLoader, Class<?> bridge, Method build) {
        this.entries = entries;
        this.projectLoader = projectLoader;
        this.bridge = bridge;
        this.build = build;
    }

    static ProjectEngine over(Collection<Path> classpathEntries) {
        Set<Path> entries = new LinkedHashSet<>(classpathEntries);
        List<URL> urls = new ArrayList<>(entries.size());
        for (Path entry : entries) {
            try {
                urls.add(entry.toUri().toURL());
            } catch (MalformedURLException e) {
                logger.fine(() -> "Skipping classpath entry " + entry + ": " + e.getMessage());
            }
        }
        URLClassLoader projectLoader = new URLClassLoader("drools-lsp-project",
                urls.toArray(new URL[0]), ClassLoader.getPlatformClassLoader());
        Class<?> bridge = null;
        Method build = null;
        if (hasCompiler(projectLoader)) {
            try {
                bridge = Class.forName(BRIDGE_CLASS, true, new BridgeClassLoader(projectLoader));
                build = bridge.getMethod("build", Map.class);
            } catch (ReflectiveOperationException | LinkageError e) {
                logger.log(Level.WARNING, "A Drools engine is on the project classpath but the compile bridge did not link", e);
            }
        }
        ProjectEngine engine = new ProjectEngine(entries, projectLoader, bridge, build);
        if (build == null) {
            // Permanently unavailable: release the loader's open jar handles now
            // rather than holding them until the next classpath change replaces it.
            engine.close();
        }
        return engine;
    }

    Set<Path> entries() {
        return entries;
    }

    @Override
    public boolean available() {
        return build != null && !closed;
    }

    @Override
    public List<Map<String, Object>> build(Map<String, String> drlByPath, Duration timeout) throws Exception {
        if (!available()) {
            throw new IllegalStateException("No Drools engine available");
        }
        AtomicReference<Object> outcome = new AtomicReference<>();
        Thread worker = new Thread(() -> {
            Thread.currentThread().setContextClassLoader(bridge.getClassLoader());
            try {
                outcome.set(build.invoke(null, drlByPath));
            } catch (Throwable t) {
                outcome.set(t);
            }
        }, "drools-lsp-engine-build");
        worker.setDaemon(true);
        worker.start();
        worker.join(Math.max(1, timeout.toMillis()));
        if (worker.isAlive()) {
            worker.interrupt();
            throw new TimeoutException("The Drools engine did not finish the build within " + timeout.toSeconds()
                    + " seconds; raise drools.lsp.compile.timeoutSeconds if the group needs longer");
        }
        Object result = outcome.get();
        if (result instanceof InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception exception) {
                throw exception;
            }
            throw e;
        }
        if (result instanceof Exception e) {
            throw e;
        }
        if (result instanceof Error e) {
            throw e;
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> messages = (List<Map<String, Object>>) result;
        return messages;
    }

    @Override
    public void close() {
        closed = true;
        try {
            projectLoader.close();
        } catch (IOException e) {
            logger.log(Level.FINE, "Closing the project class loader failed", e);
        }
    }

    boolean isClosed() {
        return closed;
    }

    Class<?> bridgeClassForTest() {
        return bridge;
    }

    ClassLoader projectLoaderForTest() {
        return projectLoader;
    }

    private static boolean hasCompiler(ClassLoader projectLoader) {
        return loads("org.kie.api.KieServices", projectLoader,
                        "No Drools engine on the project classpath; compile diagnostics stay off until the classpath changes")
                && loads(COMPILER_CLASS, projectLoader,
                        "kie-api is on the project classpath but drools-compiler is not; compile diagnostics stay off until the classpath changes")
                && loads(MVEL_CLASS, projectLoader,
                        "drools-compiler is on the project classpath but drools-mvel is not; the classic build cannot "
                                + "compile constraints, compile diagnostics stay off until the classpath changes");
    }

    private static boolean loads(String className, ClassLoader projectLoader, String absentMessage) {
        try {
            Class.forName(className, false, projectLoader);
            return true;
        } catch (ClassNotFoundException e) {
            logger.fine(absentMessage);
            return false;
        } catch (LinkageError e) {
            logger.log(Level.WARNING, className + " is on the project classpath but this Java runtime cannot load it; "
                    + "compile diagnostics stay off until the classpath changes", e);
            return false;
        }
    }

    /** Defines the bridge classes from this server's jar, everything else comes from the project. */
    private static final class BridgeClassLoader extends ClassLoader {

        BridgeClassLoader(ClassLoader parent) {
            super("drools-lsp-bridge", parent);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (!name.startsWith(BRIDGE_PACKAGE_PREFIX)) {
                throw new ClassNotFoundException(name);
            }
            String resource = name.replace('.', '/') + ".class";
            try (InputStream in = ProjectEngine.class.getClassLoader().getResourceAsStream(resource)) {
                if (in == null) {
                    throw new ClassNotFoundException(name);
                }
                byte[] bytes = in.readAllBytes();
                return defineClass(name, bytes, 0, bytes.length);
            } catch (IOException e) {
                throw new ClassNotFoundException(name, e);
            }
        }
    }
}
