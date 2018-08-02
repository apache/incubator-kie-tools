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

package org.kie.workbench.common.services.backend.compiler.impl.decorators;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.utils.JGitUtils;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

/***
 * Before decorator to update a git repo before the build
 */
public class JGITCompilerBeforeDecorator<T extends CompilationResponse, C extends AFCompiler<T>> implements CompilerDecorator<T> {

    private Map<JGitFileSystem, Git> gitMap;
    private C compiler;

    public JGITCompilerBeforeDecorator(C compiler) {
        this.compiler = compiler;
        this.gitMap = new HashMap<>();
    }

    //for test
    public C getCompiler() {
        return compiler;
    }

    @Override
    public Boolean cleanInternalCache() {
        return compiler.cleanInternalCache();
    }

    @Override
    public T compile(CompilationRequest req) {
        final Optional<Git> git = getGit(req);

        return git.map(g -> compiler.compile(handleBefore(g, req))
        ).orElseGet(() -> compiler.compile(req));
    }

    @Override
    public T compile(final CompilationRequest req,
                     final Map<Path, InputStream> override) {
        final Optional<Git> git = getGit(req);

        return git.map(g -> {
                           final Map<Path, InputStream> _override = handleMap(g, override);
                           final T result = compiler.compile(handleBefore(g, req), _override);
                           try {
                               g.reset().setMode(ResetCommand.ResetType.HARD).call();
                           } catch (GitAPIException e) {
                               throw new RuntimeException(e);
                           }
                           return result;
                       }
        ).orElseGet(() -> compiler.compile(req, override));
    }

    private Map<Path, InputStream> handleMap(final Git git,
                                             final Map<Path, InputStream> override) {
        final Map<Path, InputStream> result = new HashMap<>(override.size());
        for (Map.Entry<Path, InputStream> entry : override.entrySet()) {
            if (entry.getKey().getFileSystem() instanceof JGitFileSystem) {
                final Path convertedToCheckedPath = Paths.get(git.getRepository().getDirectory().toPath().getParent().resolve(entry.getKey().toString().substring(1)).toUri());
                result.put(convertedToCheckedPath, entry.getValue());
            }
        }
        return result;
    }

    private CompilationRequest handleBefore(final Git git,
                                            final CompilationRequest req) {
        try {
            if (req.getInfo().getPrjPath().getFileSystem() instanceof JGitFileSystem) {
                JGitUtils.pullAndRebase(git);

                return new DefaultCompilationRequest(req.getMavenRepo(),
                                                     new WorkspaceCompilationInfo(Paths.get(git.getRepository().getDirectory().getParentFile().getCanonicalFile().toPath().toUri())),
                                                     req.getOriginalArgs(),
                                                     req.skipProjectDependenciesCreationList(),
                                                     false);
            }

            return req;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Optional<Git> getGit(final CompilationRequest req) {
        final Path projectPath = req.getInfo().getPrjPath();
        if (projectPath.getFileSystem() instanceof JGitFileSystem) {
            final JGitFileSystem fs = (JGitFileSystem) projectPath.getFileSystem();
            if (!gitMap.containsKey(fs)) {
                gitMap.put(fs, JGitUtils.tempClone(fs, req.getRequestUUID()));
            }
            return Optional.of(gitMap.get(fs));
        }
        return Optional.empty();
    }

    Map<JGitFileSystem, Git> getGitMap() {
        return gitMap;
    }
}