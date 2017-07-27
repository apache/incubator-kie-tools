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

package org.kie.workbench.common.services.backend.compiler.nio.decorators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.JGitUtils;
import org.kie.workbench.common.services.backend.compiler.nio.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.nio.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.nio.impl.DefaultCompilationRequest;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

/***
 * Before decorator to update a git repo before the build
 */
public class JGITCompilerBeforeDecorator<T extends CompilationResponse, C extends AFCompiler<T>> implements CompilerDecorator {

    private Map<JGitFileSystem, Git> gitMap = new HashMap<>();
    private C compiler;

    public JGITCompilerBeforeDecorator(C compiler) {
        this.compiler = compiler;
    }

    @Override
    public T compileSync(CompilationRequest _req) {

        final Path path = _req.getInfo().getPrjPath();
        final CompilationRequest req;
        Git repo;
        if (path.getFileSystem() instanceof JGitFileSystem) {
            final JGitFileSystem fs = (JGitFileSystem) path.getFileSystem();
            if (!gitMap.containsKey(fs)) {
                repo = JGitUtils.tempClone(fs, _req.getRequestUUID());
                gitMap.put(fs,
                           repo);
            }
            repo = gitMap.get(fs);
            JGitUtils.applyBefore(repo);

            req = new DefaultCompilationRequest(_req.getMavenRepo(),
                                                new WorkspaceCompilationInfo(Paths.get(repo.getRepository().getDirectory().toPath().getParent().resolve(path.getFileName().toString()).normalize().toUri())),
                                                _req.getOriginalArgs(),
                                                _req.getMap(),
                                                _req.getLogRequested());
        } else {
            req = _req;
        }

        return compiler.compileSync(req);
    }

    @Override
    public T buildDefaultCompilationResponse(final Boolean value) {
        return compiler.buildDefaultCompilationResponse(value);
    }

    @Override
    public T buildDefaultCompilationResponse(final Boolean value,
                                             final List output) {
        return compiler.buildDefaultCompilationResponse(value);
    }
}
