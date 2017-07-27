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
package org.kie.workbench.common.services.backend.compiler.nio.impl;

import java.util.Collections;
import java.util.List;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.configuration.Compilers;
import org.kie.workbench.common.services.backend.compiler.external339.AFMavenCli;
import org.kie.workbench.common.services.backend.compiler.impl.ProcessedPoms;
import org.kie.workbench.common.services.backend.compiler.nio.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.nio.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.IncrementalCompilerEnabler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

/**
 * Run maven with https://maven.apache.org/ref/3.3.9/maven-embedder/xref/index.html
 * to use Takari plugins like a black box
 * <p>
 * MavenCompiler compiler = new DefaultMavenCompiler();
 * or
 * MavenCompiler compiler = MavenCompilerFactory.getCompiler(Decorator.LOG_OUTPUT_AFTER);
 * <p>
 * WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(tmp);
 * CompilationRequest req = new DefaultCompilationRequest(mavenRepo, info,new String[]{MavenArgs.COMPILE},new HashMap<>(), Boolean.TRUE );
 * CompilationResponse res = compiler.compileSync(req);
 */
public abstract class BaseMavenCompiler<T extends CompilationResponse> implements AFCompiler<T> {

    private static final Logger logger = LoggerFactory.getLogger(BaseMavenCompiler.class);

    private AFMavenCli cli;

    private IncrementalCompilerEnabler enabler;

    public BaseMavenCompiler() {
        cli = new AFMavenCli();
        enabler = new DefaultIncrementalCompilerEnabler(Compilers.JAVAC);
    }

    /**
     * Check if the folder exists and if it's writable and readable
     * @param mavenRepo
     * @return
     */
    public static Boolean isValidMavenRepo(final Path mavenRepo) {
        if (mavenRepo.getParent() == null) {
            return Boolean.FALSE;// used because Path("") is considered for Files.exists...
        }
        return Files.exists(mavenRepo) && Files.isDirectory(mavenRepo) && Files.isWritable(mavenRepo) && Files.isReadable(mavenRepo);
    }

    @Override
    public T compileSync(CompilationRequest req) {
        if (logger.isDebugEnabled()) {
            logger.debug("KieCompilationRequest:{}",
                         req);
        }

        if (!req.getInfo().getEnhancedMainPomFile().isPresent()) {
            ProcessedPoms processedPoms = enabler.process(req);
            if (!processedPoms.getResult()) {
                return buildDefaultCompilationResponse(Boolean.FALSE,
                                                       "Processing poms failed",
                                                       Collections.emptyList());
            }
        }
        req.getKieCliRequest().getRequest().setLocalRepositoryPath(req.getMavenRepo());
        /**
         The classworld is now Created in the NioMavenCompiler and in the DefaultMaven compielr for this reasons:
         problem: https://stackoverflow.com/questions/22410706/error-when-execute-mavencli-in-the-loop-maven-embedder
         problem:https://stackoverflow.com/questions/40587683/invocation-of-mavencli-fails-within-a-maven-plugin
         solution:https://dev.eclipse.org/mhonarc/lists/sisu-users/msg00063.html
         */

        ClassLoader original = Thread.currentThread().getContextClassLoader();
        ClassWorld kieClassWorld = new ClassWorld("plexus.core",
                                                  getClass().getClassLoader());
        int exitCode = cli.doMain(req.getKieCliRequest(),
                                  kieClassWorld);
        Thread.currentThread().setContextClassLoader(original);
        if (exitCode == 0) {
            return (T) buildDefaultCompilationResponse(Boolean.TRUE);
        } else {
            return (T) buildDefaultCompilationResponse(Boolean.FALSE);
        }
    }

    protected abstract T buildDefaultCompilationResponse(final Boolean aFalse,
                                                         final String message,
                                                         final List<String> output);
}