/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenConfig;
import org.kie.workbench.common.services.backend.compiler.impl.external339.ReusableAFMavenCli;
import org.kie.workbench.common.services.backend.compiler.impl.incrementalenabler.DefaultIncrementalCompilerEnabler;
import org.kie.workbench.common.services.backend.compiler.impl.incrementalenabler.IncrementalCompilerEnabler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardOpenOption;

/**
 * Run maven with https://maven.apache.org/ref/3.3.9/maven-embedder/xref/index.html
 * to use Takari plugins like a black box
 * <p>
 * MavenCompiler compiler = new DefaultMavenCompiler();
 * or
 * MavenCompiler compiler = MavenCompilerFactory.getCompiler(Decorator.LOG_OUTPUT_AFTER);
 * <p>
 * WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(tmp);
 * CompilationRequest req = new DefaultCompilationRequest(mavenRepo, info,new String[]{MavenArgs.COMPILE}, Boolean.TRUE );
 * CompilationResponse res = compiler.compileSync(req);
 */
public class BaseMavenCompiler<T extends CompilationResponse> implements AFCompiler<T> {

    private static final Logger logger = LoggerFactory.getLogger(BaseMavenCompiler.class);
    private int writeBlockSize = 1024;
    private ReusableAFMavenCli cli;

    private IncrementalCompilerEnabler enabler;

    public BaseMavenCompiler() {
        cli = new ReusableAFMavenCli();
        enabler = new DefaultIncrementalCompilerEnabler();
    }

    public Boolean cleanInternalCache() {
        return enabler.cleanHistory() && cli.cleanInternals();
    }

    @Override
    public T compile(CompilationRequest req) {
        MDC.clear();
        MDC.put(MavenConfig.COMPILATION_ID, req.getRequestUUID());
        Thread.currentThread().setName(req.getRequestUUID());
        if (logger.isDebugEnabled()) {
            logger.debug("KieCompilationRequest:{}", req);
        }

        enabler.process(req);

        req.getKieCliRequest().getRequest().setLocalRepositoryPath(req.getMavenRepo());
        /**
         The classworld is now Created in the DefaultMaven compiler for this reasons:
         problem: https://stackoverflow.com/questions/22410706/error-when-execute-mavencli-in-the-loop-maven-embedder
         problem:https://stackoverflow.com/questions/40587683/invocation-of-mavencli-fails-within-a-maven-plugin
         solution:https://dev.eclipse.org/mhonarc/lists/sisu-users/msg00063.html
         */

        ClassLoader original = Thread.currentThread().getContextClassLoader();
        ClassWorld kieClassWorld = new ClassWorld("plexus.core", getClass().getClassLoader());

        int exitCode = cli.doMain(req.getKieCliRequest(), kieClassWorld);

        Thread.currentThread().setContextClassLoader(original);
        if (exitCode == 0) {
            return (T) new DefaultKieCompilationResponse(Boolean.TRUE);
        } else {
            return (T) new DefaultKieCompilationResponse(Boolean.FALSE);
        }
    }

    @Override
    public T compile(final CompilationRequest req,
                     final Map<Path, InputStream> override) {

        final List<BackupItem> backup = new ArrayList<>(override.size());
        for (Map.Entry<Path, InputStream> entry : override.entrySet()) {
            Path path = entry.getKey();
            InputStream input = entry.getValue();
            try {
                boolean isChanged = Files.exists(path);
                backup.add(new BackupItem(path, isChanged ? Files.readAllBytes(path) : null, isChanged));
                if(!Files.exists(path.getParent())){
                    Files.createDirectories(path.getParent());
                }
                Files.write(path, readAllBytes(input), StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                logger.error("Path not writed:" + entry.getKey() + "\n");
                logger.error(e.getMessage());
                logger.error("\n");
            }
        }

        T result = compile(req);

        if (req.getRestoreOverride()) {
            for (BackupItem item : backup) {
                if (item.isAChange()) {
                    Files.write(item.getPath(), item.getContent());
                } else {
                    Files.delete(item.getPath());
                }
            }
        }

        return result;
    }

    public byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(in, out);
        out.close();
        return out.toByteArray();
    }

    public void copy(InputStream in, OutputStream out) throws IOException {
        byte[] bytes = new byte[writeBlockSize];
        int len;
        while ((len = in.read(bytes)) != -1) {
            out.write(bytes, 0, len);
        }
    }

    class BackupItem {

        private byte[] content;
        private Path path;
        private boolean isAChange;

        public BackupItem(Path path, byte[] content, boolean isAChange) {
            this.path = path;
            this.content = content;
            this.isAChange = isAChange;
        }

        public boolean isAChange() {
            return isAChange;
        }

        public byte[] getContent() {
            return content;
        }

        public Path getPath() {
            return path;
        }
    }
}